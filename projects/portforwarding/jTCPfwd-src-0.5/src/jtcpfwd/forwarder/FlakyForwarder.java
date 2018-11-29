package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;

public class FlakyForwarder extends Forwarder {

	public static final String SYNTAX = "Flaky@<successPercentage>,<minMsec>,<maxMsec>,<forwarder>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class,
				DelayedCloser.class };
	}

	private final int successPercentage, minMsec, maxMsec;
	private final Forwarder forwarder;

	public FlakyForwarder(String rule) throws Exception {
		String[] parts = rule.split(",", 4);
		successPercentage = Integer.parseInt(parts[0]);
		minMsec = Integer.parseInt(parts[1]);
		maxMsec = Integer.parseInt(parts[2]);
		forwarder = Lookup.lookupForwarder(parts[3]);
	}

	public Module[] getUsedModules() {
		return new Module[] { forwarder };
	}

	public void dispose() throws IOException {
		forwarder.dispose();
	}

	/**
	 * Connect a new socket where the listener will be forwarded to.
	 */
	public Socket connect(Socket listener) {
		Random r = new Random();
		if (r.nextInt(100) >= successPercentage)
			return null;
		final Socket socket = forwarder.connect(listener);
		if (socket == null)
			return null;
		final int delay = r.nextInt(maxMsec - minMsec + 1) + minMsec;
		new Thread(new DelayedCloser(socket, delay)).start();
		return socket;
	}

	public static final class DelayedCloser implements Runnable {
		private final Socket socket;
		private final int delay;

		DelayedCloser(Socket socket, int delay) {
			this.socket = socket;
			this.delay = delay;
		}

		public void run() {
			try {
				Thread.sleep(delay);
				socket.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
