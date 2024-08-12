package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;
import jtcpfwd.util.NumberExpression;

public class RoundRobinForwarder extends Forwarder {

	public static final String SYNTAX = "RoundRobin@[#]#<numberexpression1>=<forwarder1>[#<expr2>=<forwarder2>[#...]]";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class,
				NumberExpression.class, NumberExpression.NumberRange.class };
	}

	private final NumberExpression[] expressions;
	private final Forwarder[] forwarders;
	private final boolean looping;
	private int connectionCount = 0;

	public RoundRobinForwarder(String rule) throws Exception {
		looping = rule.length() > 1 && rule.charAt(0) == rule.charAt(1);
		if (looping) {
			rule = rule.substring(1);
		}
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		expressions = new NumberExpression[st.countTokens()];
		forwarders = new Forwarder[st.countTokens()];
		int idx = 0;
		while (st.hasMoreTokens()) {
			String r = st.nextToken();
			int pos = r.indexOf('=');
			expressions[idx] = new NumberExpression(r.substring(0, pos));
			forwarders[idx++] = Lookup.lookupForwarder(r.substring(pos + 1));
		}
	}

	public Module[] getUsedModules() {
		return forwarders;
	}

	public void dispose() throws IOException {
		for (int i = 0; i < forwarders.length; i++) {
			forwarders[i].dispose();
		}
	}

	public Socket connect(Socket listener) {
		connectionCount++;
		while (true) {
			for (int i = 0; i < forwarders.length; i++) {
				if (expressions[i].matches(connectionCount)) {
					Socket result = forwarders[i].connect(listener);
					if (result != null)
						return result;
				}
			}
			if (!looping)
				break;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
}
