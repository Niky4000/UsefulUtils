package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

import jtcpfwd.util.HostMatcher;

public class ListenOnceForwarder extends Forwarder {

	public static final String SYNTAX = "ListenOnce@[#<host>[/<prefix>][#<host>[/<prefix>][#...]];][<interface>:]<port>[,<timeout>]";

	public static final Class[] getRequiredClasses() {
		return new Class[] { HostMatcher.class };
	}

	private final InetAddress bindAddress;
	private final int port, timeout;
	private final String[] rules;
	private boolean disposed;
	private ServerSocket ss;
	private Object ssLock = new Object();

	public ListenOnceForwarder(String rule) throws Exception {
		int pos = rule.lastIndexOf(';');
		if (pos != -1) {
			StringTokenizer st = new StringTokenizer(rule.substring(1, pos), "" + rule.charAt(0));
			rules = new String[st.countTokens()];
			for (int i = 0; i < rules.length; i++) {
				rules[i] = st.nextToken();
			}
			rule = rule.substring(pos + 1);

			// disable DNS caching
			java.security.Security.setProperty("networkaddress.cache.ttl", "0");
			java.security.Security.setProperty("networkaddress.cache.negative.ttl", "0");
		} else {
			rules = null;
		}
		pos = rule.lastIndexOf(':');
		if (pos != -1) {
			bindAddress = InetAddress.getByName(rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		} else {
			bindAddress = null;
		}
		pos = rule.indexOf(',');
		if (pos != -1) {
			timeout = Integer.parseInt(rule.substring(pos + 1));
			rule = rule.substring(0, pos);
		} else {
			timeout = 0;
		}
		port = Integer.parseInt(rule);
	}

	public Socket connect(Socket listener) {
		try {
			synchronized (this) {
				synchronized (ssLock) {
					if (disposed)
						return null;
					ss = new ServerSocket(port, 1, bindAddress);
				}
				try {
					if (timeout != 0) {
						ss.setSoTimeout(timeout);
					}
					Socket s = ss.accept();
					while (rules != null && !HostMatcher.isMatch(s, rules)) {
						s.close();
						s = ss.accept();
					}
					return s;
				} finally {
					synchronized (ssLock) {
						ss.close();
						ss = null;
					}
				}
			}
		} catch (SocketTimeoutException ex) {
			// ignore
			return null;
		} catch (BindException ex) {
			// ignore
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		synchronized (ssLock) {
			disposed = true;
			if (ss != null)
				ss.close();
		}
	}
}