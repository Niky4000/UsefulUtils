package jtcpfwd.listener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jtcpfwd.util.ClipboardTransferData;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;

public class ClipboardListener extends Listener {

	public static final String SYNTAX = "Clipboard@";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				ClipboardTransferData.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class };
	}

	private static boolean listening = false;
	private final OutputStream out;
	private final InputStream in;
	private Socket socket;
	private ClipboardTransferData ctd;

	public ClipboardListener(String rule) throws Exception {
		if (listening)
			throw new IllegalStateException("Only one ClipboardListener supported");
		listening = true;
		PipedSocketImpl sock = new PipedSocketImpl();
		in = sock.getLocalInputStream();
		out = sock.getLocalOutputStream();
		socket = sock.createSocket();
	}

	protected Socket tryAccept() throws IOException {
		if (socket != null) {
			Socket s = socket;
			socket = null;
			return s;
		}
		ctd = new ClipboardTransferData();
		while (!disposed) {
			ctd = ctd.copyAndWait();
			if (disposed) break;
			ctd.interact(out, in, true);
		}
		return null;
	}

	protected void tryDispose() throws IOException {
		if (ctd != null)
			ctd.dispose();
	}
}
