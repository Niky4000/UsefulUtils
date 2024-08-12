package jtcpfwd.forwarder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jtcpfwd.util.ClipboardTransferData;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;

public class ClipboardForwarder extends Forwarder implements Runnable {

	public static final String SYNTAX = "Clipboard@";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				ClipboardTransferData.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class };
	}

	private static boolean started = false;
	private boolean disposed = false;
	private InputStream in = null;
	private OutputStream out = null;
	private ClipboardTransferData ctd;
	
	public ClipboardForwarder(String rule) throws Exception {
	}

	public Socket connect(Socket listener) {
		try {
			if (started)
				throw new IllegalStateException("Only one ClipboardForwarder connection supported!");
			started = true;
			PipedSocketImpl sock = new PipedSocketImpl();
			in = sock.getLocalInputStream();
			out = sock.getLocalOutputStream();
			new Thread(this).start();
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void run() {
		try {
			Thread.sleep(1000);
			ctd = ClipboardTransferData.fromClipboard();
			ctd.connect();
			while (!disposed) {
				ctd = ctd.copyAndWait();
				if (disposed) break;
				ctd.interact(out, in, false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void dispose() throws IOException {
		disposed = true;
		if (ctd != null)
			ctd.dispose();
	}
}
