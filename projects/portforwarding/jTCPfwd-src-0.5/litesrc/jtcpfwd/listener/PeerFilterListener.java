package jtcpfwd.listener;

import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.util.HostMatcher;

public class PeerFilterListener extends Listener {

	public static final String SYNTAX = "PeerFilter@#<host>[/<prefix>][#<host>[/<prefix>][#...]]#<listener>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { Lookup.class, Forwarder.class, HostMatcher.class };
	}

	private final Listener listener;
	private final String[] rules;

	public PeerFilterListener(String rule) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));

		rules = new String[st.countTokens() - 1];
		for (int i = 0; i < rules.length; i++) {
			rules[i] = st.nextToken();
		}
		listener = Lookup.lookupListener(st.nextToken());

		// disable DNS caching
		java.security.Security.setProperty("networkaddress.cache.ttl", "0");
		java.security.Security.setProperty("networkaddress.cache.negative.ttl", "0");
	}

	public Module[] getUsedModules() {
		return new Module[] { listener };
	}

	protected Socket tryAccept() throws IOException, NoMoreSocketsException {
		while (true) {
			Socket s = listener.accept();
			if (s == null)
				return null;
			if (HostMatcher.isMatch(s, rules)) {
				return s;
			}
			s.close();
		}
	}

	protected void tryDispose() throws IOException {
		listener.dispose();
	}
}
