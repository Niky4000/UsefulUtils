package jtcpfwd.forwarder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;
import jtcpfwd.util.NullOutputStream;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.SplitOutputStream;
import jtcpfwd.util.StreamForwarder;
import jtcpfwd.util.WrappedPipedOutputStream;

public class SplitForwarder extends Forwarder {

	public static final String SYNTAX = "Split@#<forwarder1>[#<forwarder2>[#...]]";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class, StreamForwarder.class,
				SplitOutputStream.class, NullOutputStream.class };
	}

	private final Forwarder[] forwarders;

	public SplitForwarder(String rule) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		forwarders = new Forwarder[st.countTokens()];
		int idx = 0;
		while (st.hasMoreTokens()) {
			String r = st.nextToken();
			forwarders[idx++] = Lookup.lookupForwarder(r);
		}
	}

	public Module[] getUsedModules() {
		return forwarders;
	}

	public void dispose() throws IOException {
		for (int i = 0; i < forwarders.length; i++) {
			forwarders[i].dispose();
		}
	}

	public Socket connect(Socket listener) {
		try {
			PipedSocketImpl sock = new PipedSocketImpl();
			OutputStream target = sock.getLocalOutputStream();
			OutputStream[] forwardOutputStreams = new OutputStream[forwarders.length];
			for (int i = 0; i < forwarders.length; i++) {
				Socket s = forwarders[i].connect(listener);
				forwardOutputStreams[i] = s.getOutputStream();
				new StreamForwarder(s.getInputStream(), target).start();
				if (i == 0) {
					target = new NullOutputStream();
				}
			}
			sock.overwriteOutputStream(new SplitOutputStream(forwardOutputStreams));
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
