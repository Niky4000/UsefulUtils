package jtcpfwd.listener;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.forwarder.Forwarder;

public class ForwarderListener extends Listener {
	public static final String SYNTAX = "Forwarder@[<count>[,<delay>];]<forwarder>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { Forwarder.class, Lookup.class };
	}

	private int count;
	private final int delay;
	private final Forwarder forwarder;
	private boolean disposed;

	public ForwarderListener(String rule) throws Exception {
		int pos = rule.indexOf(';');
		if (pos == -1) {
			count = delay = 0;
		} else {
			String[] params = rule.substring(0, pos).split(",");
			count = Integer.parseInt(params[0]);
			if (params.length == 1) {
				delay = 0;
			} else if (params.length == 2) {
				delay = Integer.parseInt(params[1]);
			} else {
				throw new RuntimeException("Invalid parameters: " + rule);
			}
			rule = rule.substring(pos + 1);
		}
		forwarder = Lookup.lookupForwarder(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { forwarder };
	}

	protected Socket tryAccept() throws IOException, NoMoreSocketsException {
		try {
			if (count == -1) {
				throw new NoMoreSocketsException();
			}
			if (count > 0) {
				count--;
				if (count == 0) {
					count = -1;
				}
			}
			Socket s = null;
			while (s == null && !disposed) {
				if (delay > 0) {
					Thread.sleep(delay);
				}
				s = forwarder.connect(null);
			}
			return s;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected void tryDispose() throws IOException {
		disposed = true;
		forwarder.dispose();
	}
}
