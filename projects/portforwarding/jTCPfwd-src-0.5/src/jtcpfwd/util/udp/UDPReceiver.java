package jtcpfwd.util.udp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class UDPReceiver implements UDPHandler {

	private final DatagramSocket socket;
	private final SocketAddress source;

	private byte[] available = null;
	private int sequenceNumber = 0;
	private UDPHandler zeroAckHandler = null;

	public UDPReceiver(UDPDispatcher dispatcher, SocketAddress source, int initialSequenceNumber) {
		this.socket = dispatcher.getDatagramSocket();
		this.source = source;
		sequenceNumber = initialSequenceNumber;
		dispatcher.registerHandler(source, this);
	}

	public DatagramSocket getDatagramSocket() {
		return socket;
	}

	public void setZeroAckHandler(UDPHandler handler) {
		this.zeroAckHandler = handler;
	}

	public synchronized byte[] receive() throws IOException {
		try {
			while (available == null)
				wait();
			byte[] result = available;
			available = null;
			return result;
		} catch (InterruptedException ex) {
			throw new InterruptedIOException();
		}
	}

	public void handle(byte[] data, InetSocketAddress address) throws IOException {
		if (data.length < 4)
			throw new IOException("Packet too short");
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		int seq = dis.readInt();
		byte[] payload = new byte[data.length - 4];
		dis.readFully(payload);
		dis.close();
		synchronized (this) {
			if (seq == 0 && zeroAckHandler != null) {
				zeroAckHandler.handle(data, address);
			} else if (seq == sequenceNumber && available == null) {
				sequenceNumber++;
				available = payload;
				notifyAll();
			}
			if (seq < sequenceNumber) {
				DatagramPacket ack = new DatagramPacket(data, 0, 4, source);
				socket.send(ack);
			}
		}
	}

	public void receiveAsync(final Socket socket, final OutputStream out) {
		new ReceiverThread(socket, out).start();
	}

	public final class ReceiverThread extends Thread {
		private final Socket socket;
		private final OutputStream out;

		public ReceiverThread(Socket socket, OutputStream out) {
			super("ReceiverThread");
			this.socket = socket;
			this.out = out;
		}

		public void run() {
			try {
				try {
					byte[] buf;
					while ((buf = receive()).length > 0) {
						out.write(buf);
						out.flush();
					}
					out.close();
				} finally {
					socket.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
