package jtcpfwd.listener;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import jtcpfwd.Module;
import jtcpfwd.destination.Destination;

public class ReverseListener extends Listener {

	public static final String SYNTAX = "Reverse@<destination>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { Destination.class };
	}

	private final Destination destination;
	private Socket currentSocket;

	public ReverseListener(String rule) throws Exception {
		destination = Destination.lookupDestination(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { destination };
	}

	protected Socket tryAccept() throws IOException {
		try {
			final InetSocketAddress target = destination.getNextDestination();
			if (target == null)
				return null;
			Socket s = new Socket();
			currentSocket = s;
			s.connect(target);
			boolean ok = false;
			try {
				int b = s.getInputStream().read();
				if (b == -1)
					return null;
				if (b != 42)
					throw new IOException("Invalid marker byte received");
				ok = true;
				return s;
			} finally {
				if (!ok)
					s.close();
			}
		} catch (ConnectException ex) {
			// ignore;
			return null;
		} finally {
			currentSocket = null;
		}
	}

	protected void tryDispose() throws IOException {
		destination.dispose();
		if (currentSocket != null)
			currentSocket.close();
	}
}
