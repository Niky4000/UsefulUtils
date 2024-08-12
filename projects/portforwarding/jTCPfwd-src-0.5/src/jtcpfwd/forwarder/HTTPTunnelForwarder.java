package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.util.EnglishWordCoder;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.PollingHandler;
import jtcpfwd.util.WrappedPipedOutputStream;
import jtcpfwd.util.http.HTTPTunnelClient;

public class HTTPTunnelForwarder extends Forwarder {

	public static final String SYNTAX = "HTTPTunnel@<timeout>;<baseURL>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class, PollingHandler.class,
				PollingHandler.OutputStreamHandler.class,
				HTTPTunnelClient.class, HTTPTunnelClient.CamouflageHandler.class,
				HTTPTunnelClient.HTTPSender.class, HTTPTunnelClient.HTTPReceiver.class,
				EnglishWordCoder.class };
	}

	private final int timeout;
	private final String baseURL;

	public HTTPTunnelForwarder(String rule) throws Exception {
		int pos = rule.indexOf(';');
		timeout = Integer.parseInt(rule.substring(0, pos));
		baseURL = rule.substring(pos + 1);
	}

	/**
	 * Connect a new socket where the listener will be forwarded to.
	 */
	public Socket connect(Socket listener) {
		try {
			PipedSocketImpl sock = new PipedSocketImpl();
			final PollingHandler pth = new PollingHandler(sock.getLocalOutputStream(), 1048576);
			HTTPTunnelClient.handle(baseURL, timeout, pth);
			sock.overwriteOutputStream(pth);
			return sock.createSocket();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
	}
}
