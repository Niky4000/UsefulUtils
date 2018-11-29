package jtcpfwd.destination;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;

import jtcpfwd.Module;
import jtcpfwd.util.NumberExpression;

public class RoundRobinDestination extends Destination {

	public static final String SYNTAX = ":RoundRobin:#<numberexpression1>=<destination1>[#<expr2>=<destination2>[#...]]";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				NumberExpression.class,
				NumberExpression.NumberRange.class, };
	}

	private final NumberExpression[] expressions;
	private final Destination[] destinations;
	private int connectionCount = 0;

	public RoundRobinDestination(String rule) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		expressions = new NumberExpression[st.countTokens()];
		destinations = new Destination[st.countTokens()];
		int idx = 0;
		while (st.hasMoreTokens()) {
			String r = st.nextToken();
			int pos = r.indexOf('=');
			expressions[idx] = new NumberExpression(r.substring(0, pos));
			destinations[idx++] = Destination.lookupDestination(r.substring(pos + 1));
		}
	}

	public Module[] getUsedModules() {
		return destinations;
	}

	public void dispose() throws IOException {
		for (int i = 0; i < destinations.length; i++) {
			destinations[i].dispose();
		}
	}

	public synchronized InetSocketAddress getNextDestination() {
		connectionCount++;
		for (int i = 0; i < destinations.length; i++) {
			if (expressions[i].matches(connectionCount)) {
				return destinations[i].getNextDestination();
			}
		}
		return null;
	}
}
