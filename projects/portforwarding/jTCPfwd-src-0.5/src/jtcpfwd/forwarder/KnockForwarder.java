package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;
import jtcpfwd.listener.knockrule.KnockRule;

public class KnockForwarder extends Forwarder {

	public static final String SYNTAX = "Knock@#<forwarder>#<peer>#<rule>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class,
				KnockRule.class };
	}

	private final Forwarder forwarder;
	private final InetAddress peer;
	private final KnockRule knockRule;

	public KnockForwarder(String rule) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		forwarder = Lookup.lookupForwarder(st.nextToken());
		peer = InetAddress.getByName(st.nextToken());
		knockRule = KnockRule.lookup(st);
	}

	public Module[] getUsedModules() {
		return new Module[] { forwarder, knockRule };
	}

	public Socket connect(Socket listener) {
		try {
			knockRule.trigger(peer);
			Socket sock = forwarder.connect(listener);
			if (sock != null)
				return sock;
			Thread.sleep(100);
			sock = forwarder.connect(listener);
			if (sock != null)
				return sock;
			Thread.sleep(1000);
			return forwarder.connect(listener);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		knockRule.dispose();
		forwarder.dispose();
	}
}
