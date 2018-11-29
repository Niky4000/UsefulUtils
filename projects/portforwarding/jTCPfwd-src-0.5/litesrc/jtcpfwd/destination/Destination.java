package jtcpfwd.destination;

import java.net.InetSocketAddress;

import jtcpfwd.Module;

/**
 * An abstract source that "computes" destination addresses.
 */
public abstract class Destination extends Module {

	public static Destination lookupDestination(String rule) throws Exception {
		if (!rule.startsWith(":") || rule.substring(1).indexOf(':') == -1)
			rule = ":Simple:" + rule;
		if (!rule.startsWith(":"))
			throw new RuntimeException();
		rule = rule.substring(1);
		int pos = rule.indexOf(":");
		Class c = Module.lookup(Destination.class, rule.substring(0, pos));
		return (Destination) c.getConstructor(new Class[] { String.class }).newInstance(new Object[] { rule.substring(pos + 1) });
	}

	/**
	 * Return the next destination address.
	 */
	public abstract InetSocketAddress getNextDestination();
}
