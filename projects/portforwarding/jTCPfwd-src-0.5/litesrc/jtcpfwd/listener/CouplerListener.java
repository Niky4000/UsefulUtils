package jtcpfwd.listener;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.util.NumberExpression;

public class CouplerListener extends Listener {

	public static final String SYNTAX = "Coupler@[#]#[!][+]<numbers>:<listener>[#...]";

	public static final Class[] getRequiredClasses() {
		return new Class[] { Lookup.class, Forwarder.class, NumberExpression.class, CouplerSocketCloser.class, CouplerDataForwarder.class, NumberExpression.NumberRange.class };
	}

	private final Listener[] listeners;
	private final NumberExpression[] targets;
	private final boolean[] stop, sendSame;
	private final boolean threaded;

	public CouplerListener(String rule) throws Exception {
		threaded = rule.length() > 1 && rule.charAt(0) == rule.charAt(1);
		if (threaded) {
			rule = rule.substring(1);
		}
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		int count = st.countTokens();
		if (count < 1)
			throw new IllegalArgumentException("At least one listener required!");
		listeners = new Listener[count];
		targets = new NumberExpression[count];
		stop = new boolean[count];
		sendSame = new boolean[count];
		for (int i = 0; i < count; i++) {
			String token = st.nextToken();
			int pos = token.indexOf(':');
			if (pos == -1)
				throw new IllegalArgumentException("No colon found: " + token);
			listeners[i] = Lookup.lookupListener(token.substring(pos + 1));
			token = token.substring(0, pos);
			if (token.startsWith("!")) {
				token = token.substring(1);
				stop[i] = true;
			}
			if (token.startsWith("+")) {
				token = token.substring(1);
				sendSame[i] = true;
			}
			targets[i] = new NumberExpression(token);
		}
	}

	public Module[] getUsedModules() {
		return listeners;
	}

	// will never return
	protected Socket tryAccept() throws IOException, NoMoreSocketsException {
		while (true) {
			final Socket[] sockets = new Socket[listeners.length];
			for (int i = 0; i < sockets.length; i++) {
				sockets[i] = listeners[i].accept();
			}
			for (int i = 0; i < sockets.length; i++) {
				final int idx = i;
				new Thread(new CouplerDataForwarder(sockets, idx)).start();
			}
			Runnable runnable = new CouplerSocketCloser(sockets);
			if (threaded) {
				new Thread(runnable).start();
			} else {
				runnable.run();
			}
		}
	}

	public final class CouplerSocketCloser implements Runnable {
		private final Socket[] sockets;

		CouplerSocketCloser(Socket[] sockets) {
			this.sockets = sockets;
		}

		public void run() {
			while (true) {
				boolean goOn = true, alive = false;
				synchronized (sockets) {
					for (int i = 0; i < sockets.length; i++) {
						if (sockets[i] != null)
							alive = true;
						else if (stop[i])
							goOn = false;
					}
				}
				if (!goOn || !alive)
					break;
			}
			try {
				synchronized (sockets) {
					for (int i = 0; i < sockets.length; i++) {
						if (sockets[i] != null)
							sockets[i].close();
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public final class CouplerDataForwarder implements Runnable {
		private final Socket[] sockets;
		private final int idx;

		CouplerDataForwarder(Socket[] sockets, int idx) {
			this.sockets = sockets;
			this.idx = idx;
		}

		public void run() {
			try {
				byte[] buffer = new byte[4096];
				InputStream in = sockets[idx].getInputStream();
				int len;
				while ((len = in.read(buffer)) != -1) {
					synchronized (sockets) {
						for (int j = 0; j < sockets.length; j++) {
							if (sockets[j] != null && targets[idx].matches(j + 1)) {
								if (j != idx || sendSame[idx]) {
									sockets[j].getOutputStream().write(buffer, 0, len);
								}
							}
						}
					}
				}
			} catch (SocketException ex) {
				if (!ex.getMessage().equalsIgnoreCase("socket closed") && !ex.getMessage().equals("Socket is closed"))
					ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					sockets[idx].close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				synchronized (sockets) {
					sockets[idx] = null;
				}
			}
		}
	}

	protected void tryDispose() throws IOException {
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].dispose();
		}
	}
}
