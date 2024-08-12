package jtcpfwd.listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleListener extends Listener {

	public static final String SYNTAX = "[Simple@][<interface>:]<port>";

	private final ServerSocket ss;

	public SimpleListener(String rule) throws Exception {
		InetAddress bindAddr = null;
		int pos = rule.lastIndexOf(':');
		if (pos != -1) {
			bindAddr = InetAddress.getByName(rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		}
		ss = new ServerSocket(Integer.parseInt(rule), 50, bindAddr);
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
