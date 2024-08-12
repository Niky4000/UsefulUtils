package jtcpfwd.listener;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.PipedSocketImpl.PipedSocket;
import jtcpfwd.util.PollingHandler;
import jtcpfwd.util.WrappedPipedOutputStream;

public class RestartableListener extends Listener {

	public static final String SYNTAX = "Restartable@<listener>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Forwarder.class,
				PipedSocketImpl.class, PipedSocket.class,
				WrappedPipedOutputStream.class,
				PollingHandler.class,
				PollingHandler.OutputStreamHandler.class,
				AsyncSender.class, AsyncReceiver.class, AsyncCloser.class };
	}

	private final Listener listener;
	private final Map/* <Long,PollingHandler> */existingConnections = new HashMap();

	public RestartableListener(String rule) throws Exception {
		listener = Lookup.lookupListener(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { listener };
	}

	protected void tryDispose() throws IOException {
		listener.dispose();
	}

	protected Socket tryAccept() throws IOException, NoMoreSocketsException {
		PipedSocketImpl sock = null;
		while (sock == null) {
			Socket s = listener.accept();
			DataInputStream dis = new DataInputStream(s.getInputStream());
			Long globalID;
			int receiveOffset, sendOffset;
			try {
				globalID = new Long(dis.readLong());
				receiveOffset = dis.readInt();
				sendOffset = dis.readInt();
			} catch (EOFException ex) {
				s.close();
				continue;
			}
			if (!existingConnections.containsKey(globalID)) {
				sock = new PipedSocketImpl();
				final PollingHandler ph = new PollingHandler(sock.getLocalOutputStream(), 1048576);
				sock.overwriteOutputStream(ph);
				existingConnections.put(globalID, ph);
			}
			handle(((PollingHandler) existingConnections.get(globalID)), s, receiveOffset, sendOffset);
		}
		return sock.createSocket();
	}

	private void handle(final PollingHandler ph, final Socket s, int receiveOffset, int sendOffset) {
		final int generationCount = ph.setSendOffset(sendOffset);
		final Thread t1 = new Thread(new AsyncSender(generationCount, s, ph));
		t1.start();
		ph.setReceiveOffset(receiveOffset);
		final Thread t2 = new Thread(new AsyncReceiver(ph, s));
		t2.start();
		new Thread(new AsyncCloser(s, t2, t1)).start();
	}

	public static final class AsyncCloser implements Runnable {
		private final Socket s;
		private final Thread t2;
		private final Thread t1;

		public AsyncCloser(Socket s, Thread t2, Thread t1) {
			this.s = s;
			this.t2 = t2;
			this.t1 = t1;
		}

		public void run() {
			try {
				t1.join();
				t2.join();
				s.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static final class AsyncReceiver implements Runnable {
		private final PollingHandler ph;
		private final Socket s;

		public AsyncReceiver(PollingHandler ph, Socket s) {
			this.ph = ph;
			this.s = s;
		}

		public void run() {
			try {
				InputStream in = s.getInputStream();
				byte[] buf = new byte[4096];
				int len;
				while ((len = in.read(buf)) != -1) {
					ph.receiveBytes(buf, 0, len);
				}
			} catch (SocketException ex) {
				if (!ex.getMessage().equalsIgnoreCase("socket closed") && !ex.getMessage().equals("Socket is closed"))
					ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static final class AsyncSender implements Runnable {
		private final int generationCount;
		private final Socket s;
		private final PollingHandler ph;

		public AsyncSender(int generationCount, Socket s, PollingHandler ph) {
			this.generationCount = generationCount;
			this.s = s;
			this.ph = ph;
		}

		public void run() {
			try {
				OutputStream out = s.getOutputStream();
				try {
					byte[] data;
					while ((data = ph.getSendBytes(15000, -1, true, generationCount)) != null) {
						out.write(data);
						out.flush();
					}
				} catch (ConcurrentModificationException ex) {
				}
			} catch (SocketException ex) {
				if (!ex.getMessage().equalsIgnoreCase("socket closed") && !ex.getMessage().equals("Socket is closed") && !ex.getMessage().equals("Software caused connection abort: socket write error"))
					ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
