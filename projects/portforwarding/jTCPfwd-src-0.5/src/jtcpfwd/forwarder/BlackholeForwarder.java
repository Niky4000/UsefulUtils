package jtcpfwd.forwarder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jtcpfwd.util.NullOutputStream;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;

public class BlackholeForwarder extends Forwarder {

	public static final String SYNTAX = "Blackhole@";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class,
				NullOutputStream.class };
	}

	public final List /* <OutputStream> */blackHoles = new ArrayList();

	public BlackholeForwarder(String rule) throws Exception {
		if (rule.length() != 0)
			throw new IllegalArgumentException("Rule must be empty");
	}

	public Socket connect(Socket listener) {
		try {
			PipedSocketImpl sock = new PipedSocketImpl();
			sock.overwriteOutputStream(new NullOutputStream());
			blackHoles.add(sock.getLocalOutputStream());
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		for (int i = 0; i < blackHoles.size(); i++) {
			((OutputStream) blackHoles.get(i)).close();
		}
	}
}