package jtcpfwd;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class SingleForwarderTest implements Runnable {

	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			System.out.println("Usage: java jtcpfwd.SingleForwarderTest <listenPort> <host> <port> in|out|both");
			return;
		}
		ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
		System.out.println("Listening.");
		System.out.println("Sleeping");
		Thread.sleep(5000);
		System.out.println("Creating Socket...");
		Socket s1 = new Socket(args[1], Integer.parseInt(args[2]));
		s1.getOutputStream().write(42);
		System.out.println("Created Socket.");
		Socket s2 = ss.accept();
		System.out.println("Accepted Socket.");
		ss.close();
		if (s2.getInputStream().read() != 42)
			throw new IOException("Original byte incorrect");
		System.out.println("Closed Listener.");
		if (args[3].equals("in") || args[3].equals("both")) {
			new Thread(new SingleForwarderTest("I", s2, s1)).start();
		}
		if (args[3].equals("out") || args[3].equals("both")) {
			new Thread(new SingleForwarderTest("O", s1, s2)).start();
		}
		System.out.println("Threads started.");
	}

	private Random r;
	private final DataInputStream in;
	private final OutputStream out;
	private final String name;

	public SingleForwarderTest(String name, Socket inSocket, Socket outSocket) throws IOException {
		this.name = name;
		this.in = new DataInputStream(inSocket.getInputStream());
		this.out = outSocket.getOutputStream();
		r = new Random();
	}

	public void run() {
		try {
			while (System.in.available() == 0) {
				final byte[] data = new byte[r.nextInt(131072)];
				r.nextBytes(data);
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							out.write(data);
							out.flush();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				});
				t.start();
				byte[] newData = new byte[data.length];
				in.readFully(newData);
				if (!Arrays.equals(data, newData))
					throw new IOException();
				t.join();
				System.out.println(name + ": Sent " + data.length);
			}
			System.out.println(name + ": Thread finished.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
