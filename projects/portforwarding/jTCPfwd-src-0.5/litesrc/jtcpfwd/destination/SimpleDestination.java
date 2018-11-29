package jtcpfwd.destination;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleDestination extends Destination {

	public static final String SYNTAX = "[:Simple:]<host>:<port>";

	private final InetSocketAddress target;

	public SimpleDestination(String rule) throws Exception {
		int pos = rule.lastIndexOf(':');
		target = new InetSocketAddress(rule.substring(0, pos), Integer.parseInt(rule.substring(pos + 1)));
	}

	public InetSocketAddress getNextDestination() {
		return target;
	}

	public void dispose() throws IOException {
	}
}
