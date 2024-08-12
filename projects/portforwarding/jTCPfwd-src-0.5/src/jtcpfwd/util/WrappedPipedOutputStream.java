package jtcpfwd.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;

/**
 * Wrapper around {@link PipedOutputStream} that sends all data from a dedicated
 * thread, to avoid "Write end dead" exceptions.
 */
public class WrappedPipedOutputStream extends OutputStream implements Runnable {
	private final PipedOutputStream wrapped;

	private boolean writePending = false;
	private byte[] data;
	private int offsetOrArg;
	private int length;
	private boolean closed = false;

	public WrappedPipedOutputStream(PipedOutputStream wrapped) {
		this.wrapped = wrapped;
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	public synchronized void write(int b) throws IOException {
		try {
			if (closed)
				throw new IOException("Stream is closed");
			while (writePending)
				wait();
			data = null;
			offsetOrArg = b;
			length = 1;
			writePending = true;
			notifyAll();
			while (writePending)
				wait();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized void write(byte[] b, int off, int len) throws IOException {
		try {
			if (closed)
				throw new IOException("Stream is closed");
			if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0))
				throw new IndexOutOfBoundsException();
			while (writePending)
				wait();
			data = b;
			offsetOrArg = off;
			length = len;
			writePending = true;
			notifyAll();
			while (writePending)
				wait();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized void flush() throws IOException {
		try {
			if (closed)
				return;
			while (writePending)
				wait();
			data = null;
			length = 0;
			writePending = true;
			notifyAll();
			while (writePending)
				wait();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized void close() throws IOException {
		try {
			if (closed)
				return;
			while (writePending)
				wait();
			data = null;
			length = -1;
			writePending = true;
			notifyAll();
			closed = true;
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized void run() {
		try {
			while (true) {
				while (!writePending)
					wait();
				if (data == null) {
					if (length == 1) {
						wrapped.write(offsetOrArg);
					} else if (length == 0) {
						wrapped.flush();
					} else if (length == -1) {
						wrapped.close();
						break;
					}
				} else {
					wrapped.write(data, offsetOrArg, length);
				}
				writePending = false;
				notifyAll();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
