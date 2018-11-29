package jtcpfwd;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Automated runner to run {@link ForwarderTest} from Ant.
 */
public class ForwarderTestRunner {
	public static void main(String[] args) {
		try {
			main0(args);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			new ThreadWatchdogThread(10000).start();
		}
	}

	private static void main0(String[] args) throws Exception {
		args = stripComments(args);
		int separator = -1;
		for (int i = 1; i < args.length; i++) {
			if (args[i].equals("--")) {
				separator = i;
				break;
			}
		}
		if (separator == -1) {
			System.out.println("Usage: java jtcpfwd.ForwarderTestRunner <ports> <tests> -- <mainargs>");
			System.out.println();
			System.out.println("Each <test> can either be <listenport>,<host>,<port>,<seconds> to run");
			System.out.println("a ForwarderTest, or <host>,<port> to test this port to be dead.");
			return;
		}
		int first = 0;
		String[] ports = new String[0];
		if (args[0].startsWith("--ports=")) {
			ports = args[0].substring(8).split(",");
			checkFreePorts(ports);
			first = 1;
		}
		String[] mainargs = new String[args.length - separator - 1];
		System.arraycopy(args, separator + 1, mainargs, 0, mainargs.length);
		ForwarderThread[] forwarderThreads = Main.start(mainargs);
		if (forwarderThreads.length == 0)
			throw new RuntimeException("No forwarder threads started");
		Thread[] threads = new Thread[separator];
		final Random r = new Random();
		for (int i = first; i < separator; i++) {
			System.out.println("Starting test " + args[i]);
			final String[] params = args[i].split(",");
			if (params.length == 2) {
				Socket s = new Socket(params[0], Integer.parseInt(params[1]));
				if (s.getInputStream().read() != -1)
					throw new RuntimeException("Socket not closed");
				s.close();
			} else if (params.length == 4) {
				threads[i] = new Thread(new Runnable() {
					public void run() {
						try {
							new ForwarderTest(params, r);
						} catch (Throwable t) {
							t.printStackTrace();
							System.exit(1);
						}
					};
				});
				threads[i].start();
			} else {
				throw new IllegalArgumentException("Unsupported test: " + args[i]);
			}
		}
		System.out.println("All tests started, waiting for finish.");
		for (int i = 0; i < threads.length; i++) {
			if (threads[i] != null)
				threads[i].join();
		}
		System.out.println("All tests finished, shutting down modules.");
		Thread.sleep(1000);
		for (int i = 0; i < forwarderThreads.length; i++) {
			forwarderThreads[i].dispose();
		}
		System.out.println("All modules shut down.");
		checkFreePorts(ports);
	}

	private static String[] stripComments(String[] args) {
		List/*<String>*/ argList = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			if (!args[i].startsWith("#"))
				argList.add(args[i]);
		}
		return (String[]) argList.toArray(new String[argList.size()]);
	}

	private static void checkFreePorts(String[] ports) throws IOException {
		for (int i = 0; i < ports.length; i++) {
			if (ports[i].startsWith("U")) {
				int port = Integer.parseInt(ports[i].substring(1));
				try {
					new DatagramSocket(port).close();
				} catch (BindException ex) {
					throw new BindException("UDP Port " + port + " not free: " + ex.getMessage());
				}	
			} else {
				int port = Integer.parseInt(ports[i]);
				try {
					new ServerSocket(port).close();
				} catch (BindException ex) {
					throw new BindException("Port " + port + " not free: " + ex.getMessage());
				}
			}
		}
	}
}
