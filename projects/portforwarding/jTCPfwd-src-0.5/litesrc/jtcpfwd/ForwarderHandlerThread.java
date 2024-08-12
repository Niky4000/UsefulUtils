package jtcpfwd;

import java.net.Socket;

import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.util.StreamForwarder;

/**
 * Handler for forwarding an accepted connection.
 */
public class ForwarderHandlerThread extends Thread {
	private final Socket source;
	private final Forwarder forwarder;

	public ForwarderHandlerThread(Socket source, Forwarder forwarder) {
		super("Handler");
		this.source = source;
		this.forwarder = forwarder;
	}

	public void run() {
		try {
			Socket destination = forwarder.connect(source);
			try {
				if (destination != null) {
					StreamForwarder f1 = new StreamForwarder(source.getInputStream(), destination.getOutputStream());
					StreamForwarder f2 = new StreamForwarder(destination.getInputStream(), source.getOutputStream());
					f1.start();
					f2.start();
					f1.join();
					f2.join();
				}
			} finally {
				source.close();
				if (destination != null)
					destination.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}