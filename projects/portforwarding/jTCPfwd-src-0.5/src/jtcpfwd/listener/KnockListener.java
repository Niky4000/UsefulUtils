package jtcpfwd.listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.listener.knockrule.KnockRule;

public class KnockListener extends Listener implements Runnable {

	public static final String[] SUPPORTED_KNOCK_RULES = {
			"UDP", "TCP", "UDPDNS", "Count"
	};

	public static final String SYNTAX = "Knock@#[<interface>:]<port>[,<timeout>]#<rule>\r\n  " +
			"Knock@##<varname>#<forwarder>#<rule>\r\n" +
			"\r\n" +
			"Supported knock rules: \r\n" +
			buildKnockRuleDescription();

	private static String buildKnockRuleDescription() {
		StringBuffer sb = new StringBuffer();
		try {
			for (int i = 0; i < SUPPORTED_KNOCK_RULES.length; i++) {
				try {
					Class clazz = Module.lookup(KnockRule.class, SUPPORTED_KNOCK_RULES[i]);
					sb.append("  " + (String) clazz.getField("SYNTAX").get(null) + "\r\n");
				} catch (ClassNotFoundException ex) {
					// ignore
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sb.toString();
	}

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Forwarder.class, KnockRule.class };
	}

	private final KnockRule knockRule;
	private final List/* <Socket> */socketsToAccept = new ArrayList();
	private final String forwarderTemplate;

	public KnockListener(String rule) throws Exception {
		boolean varMode = rule.length() > 1 && rule.charAt(0) == rule.charAt(1);
		if (varMode)
			rule = rule.substring(1);
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		if (varMode) {
			String varname = st.nextToken();
			String template = st.nextToken();
			forwarderTemplate = template.replaceAll("\\Q" + varname + "\\E", "\0");
		} else {
			forwarderTemplate = "ListenOnce@#\0;" + st.nextToken();
		}
		knockRule = KnockRule.lookup(st);
		knockRule.listen();
		new Thread(this).start();
	}

	public Module[] getUsedModules() {
		try {
			String forwarderRule = forwarderTemplate.replaceAll("\0", "127.0.0.1");
			Forwarder fwd = Lookup.lookupForwarder(forwarderRule);
			return new Module[] { knockRule, fwd };
		} catch (Exception ex) {
			ex.printStackTrace();
			return new Module[] { knockRule };
		}
	}

	public void run() {
		try {
			while (!disposed) {
				InetAddress address = knockRule.getNextAddress();
				String addressMask = address == null ? "0.0.0.0/0" : address.getHostAddress();
				String forwarderRule = forwarderTemplate.replaceAll("\0", addressMask);
				Forwarder fwd = Lookup.lookupForwarder(forwarderRule);
				Socket s = fwd.connect(null);
				if (s != null) {
					addSocketToAccept(s);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private synchronized void addSocketToAccept(Socket socket) {
		socketsToAccept.add(socket);
		notifyAll();
	}

	protected synchronized Socket tryAccept() throws IOException {
		try {
			while (socketsToAccept.size() == 0) {
				if (disposed)
					return null;
				wait();
			}
			return (Socket) socketsToAccept.remove(0);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected synchronized void tryDispose() throws IOException {
		disposed = true;
		notifyAll();
		knockRule.dispose();
	}
}
