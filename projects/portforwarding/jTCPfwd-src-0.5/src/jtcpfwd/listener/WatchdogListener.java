package jtcpfwd.listener;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.Watchdog;
import jtcpfwd.util.WrappedPipedOutputStream;

public class WatchdogListener extends Listener {

	public static final String SYNTAX = "Watchdog@<delay>[;<timeout>[,<initialtimeout>]]";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Watchdog.class, Watchdog.SocketHandler.class,
				Watchdog.TimeoutHandler.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class };
	}

	private final int delay;
	private final Watchdog dog;

	public WatchdogListener(String rule) throws Exception {
		int pos = rule.indexOf(';');
		if (pos == -1) {
			delay = Integer.parseInt(rule);
			rule = "";
		} else {
			delay = Integer.parseInt(rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		}
		dog = new Watchdog(rule);
	}

	protected synchronized Socket tryAccept() throws IOException {
		try {
			Thread.sleep(delay);
			return dog.createSocket();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected void tryDispose() throws IOException {
		dog.dispose();
	}
}
