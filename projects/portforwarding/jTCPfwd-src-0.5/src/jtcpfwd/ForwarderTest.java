package jtcpfwd;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

/**
 * Simple forwarder tester
 */
public class ForwarderTest {


	public static void main(String[] args) throws Exception {
		new ForwarderTest(args, new Random());
	}

	private int currentCount = 0;

	private long endTime = Long.MAX_VALUE;

	
	public ForwarderTest(String[] args, Random r) throws Exception {
		if (args.length != 3 && args.length != 4) {
			System.out.println("Usage: java jtcpfwd.ForwarderTest <listenPort> <host> <port> [<seconds>]");
			return;
		}
		ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
		final SocketAddress addr = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
		if (args.length == 4) {
			endTime = System.currentTimeMillis() + 1000 * Integer.parseInt(args[3]);
		}
		while (true) {
			int count = r.nextInt(20) + 1;
			if (count > 15)
				count = 1;
			Thread[] threads = new Thread[count];
			Socket[] outsocks = new Socket[count];
			Socket[] insocks = new Socket[count];
			System.out.println("Creating " + count + " new forwarders...");
			for (int i = 0; i < count; i++) {
				final int idx = i;
				final Socket sock = new Socket();
				outsocks[idx] = sock;
				threads[i] = new Thread(new Runnable() {
					public void run() {
						try {
							sock.connect(addr);
						} catch (IOException ex) {
							ex.printStackTrace();
							System.exit(1);
						}
					}
				});
				threads[i].start();
			}
			for (int i = 0; i < insocks.length; i++) {
				insocks[i] = ss.accept();
			}
			for (int i = 0; i < count; i++) {
				threads[i].join();
			}
			synchronized (ForwarderTest.class) {
				currentCount += count;
				System.out.println(count + " new forwarders created -> " + currentCount);
			}
			if (r.nextBoolean()) {
				Socket[] tmp = outsocks;
				outsocks = insocks;
				insocks = tmp;
			}
			if (count > 1) {
				for (int i = 0; i < insocks.length; i++) {
					outsocks[i].getOutputStream().write(i);
				}
				Socket[] origInsocks = insocks;
				insocks = new Socket[insocks.length];
				for (int i = 0; i < insocks.length; i++) {
					int newPos = origInsocks[i].getInputStream().read();
					if (insocks[newPos] != null)
						throw new RuntimeException();
					insocks[newPos] = origInsocks[i];
				}
				for (int i = 0; i < insocks.length; i++) {
					if (insocks[i] == null)
						throw new RuntimeException();
				}
				if (r.nextBoolean()) {
					Socket[] tmp = outsocks;
					outsocks = insocks;
					insocks = tmp;
				}
			}
			for (int i = 0; i < insocks.length; i++) {
				new SocketThread(insocks[i], outsocks[i], r.nextLong()).start();
			}
			System.out.println("Forwarders started, waiting...");
			while (true) {
				Thread.sleep(1000);
				synchronized (ForwarderTest.class) {
					System.out.println("Forwarders left: " + currentCount);
					if (currentCount < 10 && !shouldStop())
						break;
					if (currentCount == 0 && shouldStop()) {
						ss.close();
						return;
					}
				}
			}
		}
	}

	private boolean shouldStop() throws IOException {
		return System.in.available() > 0 || endTime < System.currentTimeMillis();
	}

	private class SocketThread extends Thread {
		private final Socket in;
		private final Socket out;
		private final Random r;

		private SocketThread(Socket in, Socket out, long seed) {
			this.in = in;
			this.out = out;
			this.r = new Random(seed);
		}

		public void run() {
			try {
				DataInputStream outDis = new DataInputStream(out.getInputStream());
				DataInputStream inDis = new DataInputStream(in.getInputStream());
				while (!shouldStop() && r.nextInt(20) != 0) {
					byte[] outBytes = r.nextBoolean() ? null : new byte[r.nextInt(131072)];
					byte[] inBytes = r.nextBoolean() ? null : new byte[r.nextInt(131072)];
					Thread outThread = null, inThread = null;
					if (outBytes != null) {
						r.nextBytes(outBytes);
						outThread = new SenderThread(out, outBytes, inDis);
						outThread.start();
					}
					if (inBytes != null) {
						r.nextBytes(inBytes);
						inThread = new SenderThread(in, inBytes, outDis);
						inThread.start();
					}
					if (outThread != null)
						outThread.join();
					if (inThread != null)
						inThread.join();
				}
				if (inDis.available() > 0 || outDis.available() > 0)
					throw new IOException();
				if (r.nextBoolean()) {
					new Thread(new Runnable() {
						public void run() {
							try {
								in.close();
							} catch (IOException ex) {
								ex.printStackTrace();
								System.exit(1);
							}
						}
					}).start();
				}
				out.close();
				try {
					if (inDis.read() != -1)
						throw new IOException();
				} catch (SocketException ex) {
				}
				try {
					if (outDis.read() != -1)
						throw new IOException();
				} catch (SocketException ex) {
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			} finally {
				synchronized (ForwarderTest.class) {
					currentCount--;
				}
			}
		}
	}

	private static class SenderThread extends Thread {
		private final Socket out;
		private final byte[] data;
		private final DataInputStream in;

		public SenderThread(Socket out, byte[] data, DataInputStream in) {
			this.out = out;
			this.data = data;
			this.in = in;
		}

		public void run() {
			try {
				final OutputStream o = out.getOutputStream();
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							o.write(data);
							o.flush();
						} catch (IOException ex) {
							ex.printStackTrace();
							System.exit(1);
						}
					}
				});
				t.start();
				byte[] newData = new byte[data.length];
				in.readFully(newData);
				if (!Arrays.equals(data, newData)) {
					System.err.print("Data different!");
					for (int i = 0; i < data.length; i++) {
						if (i % 16 == 0)
							System.err.println();
						System.err.print(data[i] + "/" + newData[i] + "\t");
					}
					System.err.println();
					throw new IOException();
				}
				t.join();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}
}