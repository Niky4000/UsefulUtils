package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import jtcpfwd.Module;
import jtcpfwd.destination.Destination;
import jtcpfwd.util.SSLOptions;

public class SSLForwarder extends Forwarder {

	public static final String SYNTAX = "SSL@#[<option>#[<option>#[...]]]<destination>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { SSLOptions.class, SSLOptions.SSLDummyTrustManager.class, Destination.class };
	}

	private final Destination destination;
	private final SSLOptions options;

	public SSLForwarder(String rule) throws Exception {
		options = SSLOptions.parseOptions(rule, false);
		destination = Destination.lookupDestination(options.getRule());
	}

	public Module[] getUsedModules() {
		return new Module[] { destination };
	}

	public Socket connect(Socket listener) {
		try {
			final InetSocketAddress target = destination.getNextDestination();
			if (target == null)
				return null;
			Socket s = options.createSocket();
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
