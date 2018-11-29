package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.StreamForwarder;
import jtcpfwd.util.WrappedPipedOutputStream;

public class StdInOutForwarder extends Forwarder {

	public static final String SYNTAX = "StdInOut@";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				PipedSocketImpl.class,
				PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class,
				StreamForwarder.class };
	}

	private static boolean started = false;

	public StdInOutForwarder(String rule) throws Exception {
	}

	public void dispose() throws IOException {
	}

	public Socket connect(Socket listener) {
		return doConnect();
	}

	private static synchronized Socket doConnect() {
		try {
			if (started)
				return null;
			started = true;
			PipedSocketImpl sock = new PipedSocketImpl();
			new StreamForwarder(sock.getLocalInputStream(), System.out).start();
			new StreamForwarder(System.in, sock.getLocalOutputStream()).start();
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
