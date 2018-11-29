package jtcpfwd.forwarder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.listener.Listener;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;

public class CombineForwarder extends Forwarder implements Runnable {

	public static final String SYNTAX = "Combine@<forwarder>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, NoMoreSocketsException.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class, ReaderThread.class };
	}

	private final Forwarder forwarder;
	private List/* <Socket> */childSockets = new ArrayList();
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private boolean disposed = false;

	public CombineForwarder(String rule) throws Exception {
		forwarder = Lookup.lookupForwarder(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { forwarder };
	}

	public Socket connect(Socket listener) {
		try {
			if (dis != null)
				throw new IllegalStateException("Only one connection supported!");
			PipedSocketImpl sock = new PipedSocketImpl();
			dis = new DataInputStream(sock.getLocalInputStream());
			dos = new DataOutputStream(sock.getLocalOutputStream());
			new Thread(this).start();
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void run() {
		try {
			while (!disposed) {
				final int id = dis.readInt();
				int length = dis.readShort();
				if (length == -1) {
					Socket sock = forwarder.connect(null);
					synchronized (childSockets) {
						if (childSockets.size() != id)
							throw new IllegalStateException("Next ID " + childSockets.size() + " does not match " + id);
						childSockets.add(sock);
					}
					if (sock == null) {
						synchronized (dos) {
							dos.writeInt(id);
							dos.writeShort(0);
							dos.flush();
						}
					} else {
						final InputStream in = sock.getInputStream();
						new Thread(new ReaderThread(dos, id, in)).start();
					}
				} else {
					byte[] data = new byte[length];
					dis.readFully(data);
					synchronized (childSockets) {
						Socket s = (Socket) childSockets.get(id);
						if (data.length == 0) {
							s.close();
							childSockets.set(id, null);
						} else {
							s.getOutputStream().write(data);
							s.getOutputStream().flush();
						}
					}
				}
			}
		} catch (EOFException ex) {
			// done
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void dispose() throws IOException {
		disposed = true;
		forwarder.dispose();
		if (dis != null) {
			dos.close();
			dis.close();
		}
		for (int i = 0; i < childSockets.size(); i++) {
			if (childSockets.get(i) != null)
				((Socket) childSockets.get(i)).close();
		}
	}

	public static final class ReaderThread implements Runnable {
		private final DataOutputStream dos;
		private final int id;
		private final InputStream in;

		public ReaderThread(DataOutputStream dos, int id, InputStream in) {
			this.dos = dos;
			this.id = id;
			this.in = in;
		}

		public void run() {
			try {
				byte[] buf = new byte[16384];
				int len;
				while ((len = in.read(buf)) != -1) {
					synchronized (dos) {
						dos.writeInt(id);
						dos.writeShort(len);
						dos.write(buf, 0, len);
						dos.flush();
					}
				}
				synchronized (dos) {
					dos.writeInt(id);
					dos.writeShort(0);
					dos.flush();
				}
			} catch (SocketException ex) {
				if (!ex.getMessage().equalsIgnoreCase("socket closed"))
					ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
