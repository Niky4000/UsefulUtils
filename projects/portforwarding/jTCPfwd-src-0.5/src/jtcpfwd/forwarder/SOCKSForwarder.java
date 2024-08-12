package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import jtcpfwd.destination.Destination;
import socks.Proxy;
import socks.SocksSocket;

public class SOCKSForwarder extends Forwarder {

	public static final String SYNTAX = "SOCKS@#<proxyHost>[:<proxyPort>[:<user>[:<password>]]]#<destination>";

	public static final Class[] getRequiredClasses() {
		throw new UnsupportedOperationException("SOCKS based listeners and forwarders cannot be included into jTCPfwd lite version");
	}

	private final Destination destination;
	private final Proxy proxy;

	public SOCKSForwarder(String rule) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		proxy = Proxy.parseProxy(st.nextToken());
		destination = Destination.lookupDestination(st.nextToken());
	}

	public void dispose() throws IOException {
		destination.dispose();
	}

	/**
	 * Connect a new socket where the listener will be forwarded to.
	 */
	public Socket connect(Socket listener) {
		try {
			final InetSocketAddress target = destination.getNextDestination();
			if (target == null)
				return null;
			return new SocksSocket(proxy, target.getAddress(), target.getPort());
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
