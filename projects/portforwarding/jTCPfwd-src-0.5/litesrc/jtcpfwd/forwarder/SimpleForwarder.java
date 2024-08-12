package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import jtcpfwd.Module;
import jtcpfwd.destination.Destination;

public class SimpleForwarder extends Forwarder {

	public static final String SYNTAX = "[Simple@]<destination>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { Destination.class };
	}

	private final Destination destination;

	public SimpleForwarder(String rule) throws Exception {
		destination = Destination.lookupDestination(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { destination };
	}

	/**
	 * Connect a new socket where the listener will be forwarded to.
	 */
	public Socket connect(Socket listener) {
		try {
			InetSocketAddress target = destination.getNextDestination();
			if (target == null)
				return null;
			Socket s = new Socket();
			s.connect(target);
			return s;
		} catch (ConnectException ex) {
			// ignore
			return null;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		destination.dispose();
	}
}
