package jtcpfwd.listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import jtcpfwd.util.SSLOptions;

public class SSLListener extends Listener {

	public static final String SYNTAX = "SSL@#[<option>#[<option>#[...]]][<interface>:]<port>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { SSLOptions.class, SSLOptions.SSLDummyTrustManager.class };
	}

	private final ServerSocket ss;
	private final SSLOptions options;

	public SSLListener(String rule) throws Exception {
		options = SSLOptions.parseOptions(rule, true);
		rule = options.getRule();
		InetAddress bindAddr = null;
		int pos = rule.lastIndexOf(':');
		if (pos != -1) {
			bindAddr = InetAddress.getByName(rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		}
		ss = options.createServerSocket(Integer.parseInt(rule), bindAddr);
	}

	protected Socket tryAccept() throws IOException {
		if (ss.isBound() && !ss.isClosed()) {
			return ss.accept();
		} else {
			return null;
		}
	}

	protected void tryDispose() throws IOException {
		ss.close();
	}
}
