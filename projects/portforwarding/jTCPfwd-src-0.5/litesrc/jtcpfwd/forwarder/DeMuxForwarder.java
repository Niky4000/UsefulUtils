package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;

public class DeMuxForwarder extends Forwarder {

	public static final String SYNTAX = "DeMux@#[auth=<auth>#]<idx1>=<forwarder1>[#<idx2>=<forwarder2>[#...]]";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class };
	}

	private final Forwarder[] forwarders = new Forwarder[256];
	private byte[] auth = new byte[0];

	public DeMuxForwarder(String rule) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		while (st.hasMoreTokens()) {
			String r = st.nextToken();
			if (r.startsWith("auth=")) {
				auth = r.substring(5).getBytes("ISO-8859-1");
			} else {
				int pos = r.indexOf('=');
				addForwarder(r.substring(0, pos), r.substring(pos + 1), false);
			}
		}
	}

	public Module[] getUsedModules() {
		return forwarders;
	}

	public void dispose() throws IOException {
		for (int i = 0; i < forwarders.length; i++) {
			if (forwarders[i] != null)
				forwarders[i].dispose();
		}
	}

	private void addForwarder(String index, String forwarder, boolean expanded) throws Exception {
		for (int i = 0; i < index.length(); i++) {
			if (!Character.isDigit(index.charAt(i))) {
				for (char ch = '0'; ch <= '9'; ch++) {
					addForwarder(index.replace(index.charAt(i), ch), forwarder.replace(index.charAt(i), ch), true);
				}
				return;
			}
		}
		int idx = Integer.parseInt(index);
		if (expanded && idx > 255)
			return;
		forwarders[idx] = Lookup.lookupForwarder(forwarder);
	}

	public Socket connect(Socket listener) {
		int index;
		try {
			for (int i = 0; i < auth.length; i++) {
				if (listener.getInputStream().read() != (auth[i] & 0xff)) {
					return null;
				}
			}
			index = listener.getInputStream().read();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		if (index < 0 || forwarders[index] == null) {
			return null;
		}
		return forwarders[index].connect(listener);
	}
}
