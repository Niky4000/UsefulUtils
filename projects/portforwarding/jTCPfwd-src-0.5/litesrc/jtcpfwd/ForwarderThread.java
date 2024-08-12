package jtcpfwd;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.listener.Listener;

/**
 * A thread that handles a single forwarder rule.
 */
public class ForwarderThread extends Thread {

	private final Forwarder forwarder;
	private final Listener listener;
	private boolean disposed = false;

	public ForwarderThread(String listenerRule, String forwarderRule) throws Exception {
		super(new ThreadGroup(listenerRule + "->" + forwarderRule), "Forwarder");
		System.out.println(listenerRule + "->" + forwarderRule);
		listener = Lookup.lookupListener(listenerRule);
		forwarder = Lookup.lookupForwarder(forwarderRule);
	}

	public void run() {
		try {
			while (!disposed) {
				Socket s = listener.accept();
				new ForwarderHandlerThread(s, forwarder).start();
			}
		} catch (NoMoreSocketsException ex) {
			// just terminate
		}
	}

	public void dispose() throws IOException {
		disposed = true;
		listener.dispose();
		forwarder.dispose();
	}
}
