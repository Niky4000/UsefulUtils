package jtcpfwd.forwarder.filter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DelayFilter extends Filter {

	public static final String SYNTAX = "Delay,<milliseconds>[,<queuelength>]";

	public static final Class[] getRequiredClasses() {
		return new Class[] { DelayOutputStream.class, DelayedPacket.class };
	}
	
	private final int delay, queuelength;

	public DelayFilter(String rule) throws Exception {
		int pos = rule.lastIndexOf(',');
		if (pos == -1) {
			queuelength = -1;
		} else {
			queuelength = Integer.parseInt(rule.substring(pos + 1));
			rule = rule.substring(0, pos);
		}
		delay = Integer.parseInt(rule);
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		return new DelayOutputStream(sameDirection);
	}

	public class DelayOutputStream extends FilterOutputStream implements Runnable {

		private final List/* <DelayedPacket> */queue = new ArrayList();

		DelayOutputStream(OutputStream out) {
			super(out);
			new Thread(this).start();
		}

		public void run() {
			try {
				while (true) {
					DelayedPacket next;
					synchronized (this) {
						while (queue.size() == 0)
							wait();
						next = (DelayedPacket) queue.remove(0);
						notifyAll();
					}
					while (next.timestamp > System.currentTimeMillis()) {
						long wait = System.currentTimeMillis() - next.timestamp - 100;
						Thread.sleep(Math.max(10, wait));
					}
					if (next.data == null) {
						out.close();
						return;
					}
					if (next.data.length > 0) {
						out.write(next.data);
					} else {
						out.flush();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void write(int b) throws IOException {
			addPacket(new byte[] { (byte) b });
		}

		public void write(byte[] b, int off, int len) throws IOException {
			if (len == 0)
				return;
			byte[] copy = new byte[len];
			System.arraycopy(b, off, copy, 0, len);
			addPacket(copy);
		}

		private synchronized void addPacket(byte[] data) {
			try {
				while (queuelength > 0 && queue.size() > queuelength)
					wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			queue.add(new DelayedPacket(System.currentTimeMillis() + delay, data));
			notifyAll();
		}

		public void close() throws IOException {
			addPacket(null);
		}

		public void flush() throws IOException {
			addPacket(new byte[0]);
		}
	}

	public static class DelayedPacket {
		public final long timestamp;
		public final byte[] data;

		DelayedPacket(long timestamp, byte[] data) {
			this.timestamp = timestamp;
			this.data = data;
		}
	}
}