package jtcpfwd.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class PollingHandler extends OutputStream {

	private static final byte[] ESCAPE_SEQUENCE = new byte[] { 0, -1 };

	// receiveOffset < receiveCount, in case of retransmissions
	private int receiveOffset = 0, receiveCount = 0, receiveAcked = 0;

	// output stream to write output to
	private final OutputStreamHandler out;

	// for parsing escape values
	private int receivedEscapeBytes = 0, receivedEscapeValue = 0;
	private int receivedClosed = 0; // 0 = open, 1 = received, 2 = acked

	// output buffer and related variables
	private byte[] buffer;
	private int bufUsed = 0, bufSent = 0, bufDiscarded = 0;
	private int bufClosed = 0; // 0 = open, 1 = pending, 2 = closed, 3 = closed
								// acked
	private int delayedAck = 0;
	private int sendOffsetGenerationCount;

	// size constants
	private final int outputBufferSize, modulus;

	public PollingHandler(OutputStream out, int outputBufferSize) {
		this.out = new OutputStreamHandler(out);
		this.outputBufferSize = outputBufferSize;
		this.modulus = 4 * outputBufferSize;
		if (outputBufferSize < 1024 && outputBufferSize > 1024 * 1024)
			throw new IllegalArgumentException("output buffer size must be between 1 KB and 1 MB");
		if ((outputBufferSize & (outputBufferSize - 1)) != 0)
			throw new IllegalArgumentException("output buffer size must be a power of 2");
		buffer = new byte[1024];
	}

	public synchronized void write(byte[] b, int off, int len) throws IOException {
		if (bufClosed != 0)
			throw new IllegalStateException();
		while (len > outputBufferSize) {
			write(b, off, outputBufferSize);
			len -= outputBufferSize;
			off += outputBufferSize;
		}
		if (len == 0)
			return;
		ensureBufferCapacity(len);
		System.arraycopy(b, off, buffer, bufUsed, len);
		bufUsed += len;
		notifyAll();
	}

	public synchronized void write(int b) throws IOException {
		if (bufClosed != 0)
			throw new IllegalStateException();
		ensureBufferCapacity(1);
		buffer[bufUsed] = (byte) b;
		bufUsed++;
		notifyAll();
	}

	public synchronized void close() throws IOException {
		if (bufClosed != 0)
			return;
		bufClosed = 1;
		notifyAll();
	}
	
	public synchronized void dispose() throws IOException {
		close();
		out.close();
	}

	private void ensureBufferCapacity(int additionalSize) {
		while (bufUsed + additionalSize > outputBufferSize) {
			try {
				wait();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
		if (bufUsed + additionalSize > buffer.length) {
			int bufsize = buffer.length;
			while (bufUsed + additionalSize > bufsize)
				bufsize *= 2;
			if (bufsize > outputBufferSize)
				throw new RuntimeException();
			byte[] newBuffer = new byte[bufsize];
			System.arraycopy(buffer, 0, newBuffer, 0, bufUsed);
			buffer = newBuffer;
		}
	}

	public synchronized int getReceiveCount() {
		return receiveCount % modulus;
	}

	public synchronized int getReceiveOffset() {
		return receiveOffset % modulus;
	}

	public synchronized void setReceiveOffset(int newReceiveOffset) {
		receivedEscapeBytes = 0;
		receiveOffset = (receiveCount / modulus) * modulus + newReceiveOffset % modulus;
		if (receiveOffset > receiveCount)
			receiveOffset -= modulus;
		if (receiveOffset > receiveCount || receiveOffset < receiveCount - outputBufferSize)
			throw new IllegalArgumentException("Invalid receive offset: " + receiveOffset + " (" + newReceiveOffset + "/" + receiveCount + "/" + outputBufferSize + ")");
	}

	public synchronized void receiveBytes(byte[] received, int offset, int length) throws IOException {
		if (offset < 0 || length < 0 || offset + length > received.length)
			throw new IllegalArgumentException();
		int handled = offset;
		for (int i = offset; i < offset + length; i++) {
			switch (receivedEscapeBytes) {
			case 1:
				if (received[i] != -1) {
					receiveRawBytes(ESCAPE_SEQUENCE, 0, 1);
					handled = i;
					if (received[i] != 0)
						receivedEscapeBytes = 0;
				} else {
					receivedEscapeBytes++;
				}
				break;
			case 2:
				if (received[i] < 0) {
					if (received[i] == -1) {
						receiveRawBytes(ESCAPE_SEQUENCE, 0, 2);
					} else if (received[i] == -2) {
						receiveRawBytes(ESCAPE_SEQUENCE, 0, 1);
					} else if (received[i] == -3) {
						if (receivedClosed == 0) {
							out.close();
						}
						receivedClosed = 1;
						notifyAll();
					} else if (received[i] == -4) {
						// keep-alive, ignored
					} else if (received[i] == -5) {
						if (bufClosed == 2) {
							bufClosed = 3;
							notifyAll();
						}
					} else {
						throw new IllegalStateException("Invalid escape sequence operation: " + received[i]);
					}
					handled = i + 1;
					receivedEscapeBytes = 0;
				} else {
					receivedEscapeBytes++;
					receivedEscapeValue = received[i] & 0xff;
				}
				break;
			case 3:
			case 4:
				receivedEscapeValue = (receivedEscapeValue << 8) | (received[i] & 0xff);
				if (receivedEscapeBytes == 4) {
					receiveAck(receivedEscapeValue);
					receivedEscapeBytes = 0;
					handled = i + 1;
				} else {
					receivedEscapeBytes++;
				}
				break;

			case 0:
				if (received[i] == 0) {
					if (i + 1 == length || received[i + 1] == -1) {
						// start of an escape sequence
						receiveRawBytes(received, handled, i - handled);
						receivedEscapeBytes = 1;
					}
				}
				break;

			default:
				throw new IllegalStateException();
			}
		}
		if (receivedEscapeBytes == 0)
			receiveRawBytes(received, handled, offset + length - handled);
	}

	private void receiveAck(int ackValue) {
		if (ackValue < 0 || ackValue >= modulus)
			throw new IllegalArgumentException("" + ackValue);
		int ackOffset = bufDiscarded / modulus * modulus + ackValue;
		if (ackOffset < bufDiscarded)
			ackOffset += modulus;
		ackOffset -= bufDiscarded;
		if (ackOffset > bufUsed)
			throw new IllegalArgumentException("Ack offset too large");
		if (ackOffset > bufSent) {
			delayedAck = ackOffset;
		} else {
			bufUsed -= ackOffset;
			bufSent -= ackOffset;
			bufDiscarded += ackOffset;
			System.arraycopy(buffer, ackOffset, buffer, 0, bufUsed);
			notifyAll();
		}
	}

	private void receiveRawBytes(byte[] b, int off, int len) throws IOException {
		if (len < 0 || off < 0 || off + len > b.length)
			throw new IllegalArgumentException(off + "/" + len);
		if (len == 0)
			return;
		if (receivedClosed != 0)
			throw new IllegalStateException();
		int newLen = receiveOffset + len - receiveCount;
		if (newLen > 0) {
			int newOff = off + (len - newLen);
			if (newOff < 0)
				throw new RuntimeException(newOff + "/" + off + "/" + len + "/" + newLen + "//" + receiveOffset + "//" + receiveCount);
			out.write(b, newOff, newLen);
			receiveCount += newLen;
			if (receiveCount == receiveAcked + outputBufferSize)
				notifyAll();
		}
		receiveOffset += len;
		while (receiveOffset > modulus * 3 / 2 && receiveCount > modulus * 3 / 2 && receiveAcked > modulus * 3 / 2) {
			receiveOffset -= modulus;
			receiveCount -= modulus;
			receiveAcked -= modulus;
		}
	}

	// returns bytes to be sent (maybe empty) or null if the connection should
	// be closed.
	public synchronized byte[] getSendBytes(int timeout, int maxPayloadBytes, boolean includeKeepAlive, int generationCount) throws ConcurrentModificationException {
		if (sendOffsetGenerationCount != generationCount)
			throw new ConcurrentModificationException();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bufSent == bufUsed && receiveAcked + outputBufferSize != receiveCount && bufClosed != 1 && receivedClosed != 1) {
			if (bufClosed == 3 && receivedClosed == 2 && receiveAcked == receiveCount) {
				return null;
			}
			try {
				wait(timeout);
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
			if (sendOffsetGenerationCount != generationCount)
				throw new ConcurrentModificationException();
		}
		int written = bufSent;
		int bufWritten = bufUsed;
		if (maxPayloadBytes != -1 && bufWritten - bufSent > maxPayloadBytes) {
			bufWritten = bufSent + maxPayloadBytes;
		}
		for (int i = bufSent; i < bufWritten; i++) {
			if (buffer[i] == 0) {
				if (i == bufWritten - 1) {
					baos.write(buffer, written, i + 1 - written);
					baos.write(-1);
					baos.write(-2);
					written = i + 1;
				} else if (buffer[i + 1] == -1) {
					baos.write(buffer, written, i + 1 - written);
					baos.write(-1);
					// the second -1 will be written by the next byte
					written = i + 1;
				}
			}
		}
		baos.write(buffer, written, bufWritten - written);
		bufSent = bufWritten;
		if (delayedAck > 0 && delayedAck <= bufSent) {
			int ackOffset = delayedAck;
			bufUsed -= ackOffset;
			bufSent -= ackOffset;
			bufDiscarded += ackOffset;
			System.arraycopy(buffer, ackOffset, buffer, 0, bufUsed);
			delayedAck = 0;
			notifyAll();
		}
		if (bufClosed == 1) {
			bufClosed = 2;
			baos.write(0);
			baos.write(-1);
			baos.write(-3);
		}
		if (receivedClosed == 1) {
			receivedClosed = 2;
			baos.write(0);
			baos.write(-1);
			baos.write(-5);
		}
		if (receiveAcked < receiveCount) {
			int ack = receiveCount % modulus;
			baos.write(0);
			baos.write(-1);
			baos.write((ack >> 16) & 0x7f);
			baos.write((ack >> 8) & 0xff);
			baos.write(ack & 0xff);
			receiveAcked = receiveCount;
		}
		if (includeKeepAlive && baos.size() == 0) {
			baos.write(0);
			baos.write(-1);
			baos.write(-4);
		}
		return baos.toByteArray();
	}

	public synchronized int getAndResetToSendAckedCount(int[] outGenerationCount) {
		receiveAcked--; // make sure to re-send ack
		if (bufClosed == 2)
			bufClosed = 1; // same for close packet
		if (receivedClosed == 2)
			receivedClosed = 1; // and for close ack
		bufSent = 0;
		sendOffsetGenerationCount++;
		outGenerationCount[0] = sendOffsetGenerationCount;
		return bufDiscarded;
	}

	public synchronized int getSendOffset() {
		return bufDiscarded + bufSent;
	}

	public synchronized int setSendOffset(int sendOffset) {
		receiveAcked--; // make sure to re-send ack
		if (bufClosed == 2)
			bufClosed = 1; // same for close packet
		if (receivedClosed == 2)
			receivedClosed = 1; // and for close ack
		int newOffset = bufDiscarded / modulus * modulus + sendOffset % modulus;
		if (newOffset < bufDiscarded)
			newOffset += modulus;
		int newSent = newOffset - bufDiscarded;
		if (newSent > bufSent)
			throw new IllegalArgumentException("Send offset outside of send buffer");
		bufSent = newSent;
		sendOffsetGenerationCount++;
		return sendOffsetGenerationCount;
	}

	// writes output asynchronously to the output stream to reduce locking
	public static class OutputStreamHandler extends Thread {

		private final OutputStream out;
		private List/* <byte[]> */buffer = new ArrayList();

		public OutputStreamHandler(OutputStream out) {
			this.out = out;
			start();
		}

		public void run() {
			try {
				while (true) {
					byte[] data;
					boolean moreAvailable;
					synchronized (this) {
						while (buffer.size() == 0)
							wait();
						data = (byte[]) buffer.remove(0);
						moreAvailable = buffer.size() > 0;
					}
					if (data == null)
						break;
					out.write(data);
					if (!moreAvailable)
						out.flush();
				}
				out.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		public synchronized void write(byte[] b, int off, int len) throws IOException {
			if (len == 0)
				return;
			byte[] data = new byte[len];
			System.arraycopy(b, off, data, 0, len);
			buffer.add(data);
			notifyAll();
		}

		public synchronized void close() throws IOException {
			buffer.add(null);
			notifyAll();
		}
	}
}
