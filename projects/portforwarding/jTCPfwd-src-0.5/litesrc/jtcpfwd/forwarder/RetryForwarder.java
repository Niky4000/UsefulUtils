package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;

public class RetryForwarder extends Forwarder {

	public static final String SYNTAX = "Retry@<count>,<delay>,<forwarder>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class };
	}

	private final int count, delay;
	private final Forwarder forwarder;

	public RetryForwarder(String rule) throws Exception {
		String[] params = rule.split(",", 3);
		count = Integer.parseInt(params[0]);
		delay = Integer.parseInt(params[1]);
		forwarder = Lookup.lookupForwarder(params[2]);
	}

	public Module[] getUsedModules() {
		return new Module[] { forwarder };
	}

	public void dispose() throws IOException {
		forwarder.dispose();
	}

	public Socket connect(Socket listener) {
		try {
			for (int i = 0; count == 0 || i < count; i++) {
				if (i > 0 && delay > 0)
					Thread.sleep(delay);
				Socket result = forwarder.connect(listener);
				if (result != null)
					return result;

			}
		} catch (InterruptedException ex) {
			// ignore
		}
		return null;
	}
}
