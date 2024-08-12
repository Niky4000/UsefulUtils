package jtcpfwd.listener;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.NoMoreSocketsException;
import socks.ProxyServer;
import socks.server.ServerAuthenticatorNone;

public class SOCKSProxyListener extends Listener implements Runnable {

	public static final String SYNTAX = "SOCKSProxy@<port>";

	public static final Class[] getRequiredClasses() {
		throw new UnsupportedOperationException("SOCKS based listeners and forwarders cannot be included into jTCPfwd lite version");
	}

	private final int port;
	private final ProxyServer ps;

	public SOCKSProxyListener(String rule) throws Exception {
		ps = new ProxyServer(new ServerAuthenticatorNone());
		port = Integer.parseInt(rule);
		new Thread(this).start();
	}

	public void run() {
		try {
			ps.start(port);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected synchronized Socket tryAccept() throws IOException, NoMoreSocketsException {
		throw new NoMoreSocketsException();
	}

	protected void tryDispose() throws IOException {
		ps.stop();
	}
}
