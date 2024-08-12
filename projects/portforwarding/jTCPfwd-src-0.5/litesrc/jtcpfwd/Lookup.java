package jtcpfwd;

import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.listener.Listener;

public class Lookup {

	public static Listener lookupListener(String rule) throws Exception {
		return (Listener) lookupClass(Listener.class, rule);
	}

	public static Forwarder lookupForwarder(String rule) throws Exception {
		return (Forwarder) lookupClass(Forwarder.class, rule);
	}

	/**
	 * Lookup a forwarder or listener class.
	 */
	public static Module lookupClass(Class baseClass, String rule) throws Exception {
		if (rule.indexOf('@') == -1)
			rule = "Simple@" + rule;
		int pos = rule.indexOf("@");
		Class c = Module.lookup(baseClass, rule.substring(0, pos));
		return (Module) c.getConstructor(new Class[] { String.class }).newInstance(new Object[] { rule.substring(pos + 1) });
	}
}
