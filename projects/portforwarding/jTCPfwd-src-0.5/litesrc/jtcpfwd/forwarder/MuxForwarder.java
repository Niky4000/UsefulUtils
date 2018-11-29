package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;

public class MuxForwarder extends Forwarder {

	public static final String SYNTAX = "Mux@[<auth>:]<idx>=<forwarder>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class };
	}

	private final int index;
	private final Forwarder forwarder;
	private final byte[] auth;

	public MuxForwarder(String rule) throws Exception {
		int pos = rule.indexOf('=');
		String idx = rule.substring(0, pos);
		forwarder = Lookup.lookupForwarder(rule.substring(pos + 1));
		pos = idx.indexOf(':');
		if (pos == -1) {
			auth = null;
		} else {
			auth = idx.substring(0, pos).getBytes("ISO-8859-1");
			idx = idx.substring(pos + 1);
		}
		index = Integer.parseInt(idx);
	}

	public Module[] getUsedModules() {
		return new Module[] { forwarder };
	}

	public void dispose() throws IOException {
		forwarder.dispose();
	}

	public Socket connect(Socket listener) {
		Socket sock = forwarder.connect(listener);
		try {
			if (sock != null) {
				if (auth != null)
					sock.getOutputStream().write(auth);
				sock.getOutputStream().write(index);
			}
			return sock;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
