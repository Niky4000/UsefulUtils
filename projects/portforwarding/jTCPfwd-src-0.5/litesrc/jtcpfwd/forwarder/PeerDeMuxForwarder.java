package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;
import jtcpfwd.util.HostMatcher;

public class PeerDeMuxForwarder extends Forwarder {

	public static final String SYNTAX = "PeerDeMux@#<host1>[/<prefix1>]=<forwarder1>[#<host2>[/<prefix2>]=<forwarder2>[#...]]";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class,
				HostMatcher.class };
	}

	private final String[] rules;
	private final Forwarder[] forwarders;

	public PeerDeMuxForwarder(String rule) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		rules = new String[st.countTokens()];
		forwarders = new Forwarder[st.countTokens()];
		for (int i = 0; i < rules.length; i++) {
			String token = st.nextToken();
			int pos = token.indexOf('=');
			rules[i] = token.substring(0, pos);
			forwarders[i] = Lookup.lookupForwarder(token.substring(pos + 1));
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
		try {
			for (int i = 0; i < rules.length; i++) {
				if (HostMatcher.isMatch(listener, new String[] { rules[i] })) {
					return forwarders[i].connect(listener);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
