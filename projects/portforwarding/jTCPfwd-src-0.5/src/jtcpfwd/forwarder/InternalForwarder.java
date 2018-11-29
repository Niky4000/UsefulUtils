package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.InternalListener;
import jtcpfwd.listener.Listener;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;

public class InternalForwarder extends Forwarder {

	public static final String SYNTAX = "Internal@<name>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Listener.class, InternalListener.class,
				NoMoreSocketsException.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class };
	}

	private final String name;

	public InternalForwarder(String rule) throws Exception {
		this.name = rule;
	}

	/**
	 * Connect a new socket where the listener will be forwarded to.
	 */
	public Socket connect(Socket listenSocket) {
		try {
			InternalListener listener = InternalListener.getListener(name);
			if (listener == null)
				return null;
			PipedSocketImpl socket1 = new PipedSocketImpl();
			PipedSocketImpl socket2 = new PipedSocketImpl();
			socket1.overwriteOutputStream(socket2.getLocalOutputStream());
			socket2.overwriteOutputStream(socket1.getLocalOutputStream());
			listener.addSocketToAccept(socket1.createSocket());
			return socket2.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
	}
}
