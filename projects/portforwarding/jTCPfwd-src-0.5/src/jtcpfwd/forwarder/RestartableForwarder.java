package jtcpfwd.forwarder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ConcurrentModificationException;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.PipedSocketImpl.PipedSocket;
import jtcpfwd.util.PollingHandler;
import jtcpfwd.util.StreamForwarder;
import jtcpfwd.util.WrappedPipedOutputStream;

public class RestartableForwarder extends Forwarder {

	public static final String SYNTAX = "Restartable@<forwarder>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class,
				PipedSocketImpl.class, PipedSocket.class,
				WrappedPipedOutputStream.class,
				PollingHandler.class,
				PollingHandler.OutputStreamHandler.class,
				StreamForwarder.class,
				ConnectionThread.class,
				AsyncSender.class };
	}

	private static long globalCounter = 0;

	private final Forwarder forwarder;

	public RestartableForwarder(String rule) throws Exception {
		forwarder = Lookup.lookupForwarder(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { forwarder };
	}

	/**
	 * Connect a new socket where the listener will be forwarded to.
	 */
	public Socket connect(Socket listener) {
		try {
			PipedSocketImpl sock = new PipedSocketImpl();
			synchronized (RestartableForwarder.class) {
				new ConnectionThread(forwarder, sock, ++globalCounter).start();
			}
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		forwarder.dispose();
	}

	private static class ConnectionThread extends Thread {

		private final PipedSocketImpl sock;
		private final long globalID;
		private boolean closed = false;
		private final Forwarder forwarder;

		public ConnectionThread(Forwarder forwarder, PipedSocketImpl sock, long globalID) {
			this.forwarder = forwarder;
			this.sock = sock;
			this.globalID = globalID;
		}

		public void run() {
			InputStream in = sock.getLocalInputStream();
			OutputStream out = sock.getLocalOutputStream();
			final PollingHandler ph = new PollingHandler(out, 1048576);
			new StreamForwarder(in, ph).start();
			while (!closed) {
				try {
					final Socket innerSocket = forwarder.connect(null);
					if (innerSocket == null)
						continue;
					final DataOutputStream innerOut = new DataOutputStream(innerSocket.getOutputStream());
					innerOut.writeLong(globalID);
					final int[] generationCount = new int[1];
					innerOut.writeInt(ph.getAndResetToSendAckedCount(generationCount));
					innerOut.writeInt(ph.getReceiveOffset());
					innerOut.flush();
					Thread t = new Thread(new AsyncSender(this, generationCount, innerSocket, innerOut, ph));
					t.start();
					InputStream innerIn = innerSocket.getInputStream();
					byte[] buf = new byte[4096];
					int len;
					while ((len = innerIn.read(buf)) != -1) {
						ph.receiveBytes(buf, 0, len);
					}
					innerSocket.close();
				} catch (SocketException ex) {
					if (!ex.getMessage().equalsIgnoreCase("socket closed") && !ex.getMessage().equals("Socket is closed"))
						ex.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			try {
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static final class AsyncSender implements Runnable {
		private final int[] generationCount;
		private final Socket innerSocket;
		private final DataOutputStream innerOut;
		private final PollingHandler ph;
		private final ConnectionThread thread;

		AsyncSender(ConnectionThread thread, int[] generationCount, Socket innerSocket, DataOutputStream innerOut, PollingHandler ph) {
			this.thread = thread;
			this.generationCount = generationCount;
			this.innerSocket = innerSocket;
			this.innerOut = innerOut;
			this.ph = ph;
		}

		public void run() {
			try {
				byte[] buf;
				while ((buf = ph.getSendBytes(15000, -1, true, generationCount[0])) != null) {
					innerOut.write(buf, 0, buf.length);
					innerOut.flush();
				}
				// closed, therefore break
				thread.closed = true;
				innerSocket.close();
			} catch (ConcurrentModificationException ex) {
				// ignore
			} catch (SocketException ex) {
				if (!ex.getMessage().equalsIgnoreCase("socket closed") && !ex.getMessage().equals("Socket is closed"))
					ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
