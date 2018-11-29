package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.Watchdog;
import jtcpfwd.util.WrappedPipedOutputStream;

public class WatchdogForwarder extends Forwarder {

	public static final String SYNTAX = "Watchdog@[<timeout>[,<initialtimeout>]]";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Watchdog.class, Watchdog.SocketHandler.class,
				Watchdog.TimeoutHandler.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class };
	}

	public void dispose() throws IOException {
		dog.dispose();
	}

	private final Watchdog dog;

	public WatchdogForwarder(String rule) throws Exception {
		dog = new Watchdog(rule);
	}

	/**
	 * Connect a new socket where the listener will be forwarded to.
	 */
	public Socket connect(Socket listenSocket) {
		return dog.createSocket();
	}
}
