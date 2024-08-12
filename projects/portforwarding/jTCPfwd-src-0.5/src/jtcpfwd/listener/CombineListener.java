package jtcpfwd.listener;

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
import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;

public class CombineListener extends Listener {

	public static final String SYNTAX = "Combine@<listener>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { Dispatcher.class, Handler.class,
				Lookup.class, Forwarder.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class };
	}

	private final Listener listener;
	private final DataOutputStream dos;
	private final Dispatcher dispatcher;
	private final List/* <Socket> */childSockets = new ArrayList();
	private Socket socket;

	public CombineListener(String rule) throws Exception {
		listener = Lookup.lookupListener(rule);
		PipedSocketImpl sock = new PipedSocketImpl();
		final DataInputStream dis = new DataInputStream(sock.getLocalInputStream());
		dos = new DataOutputStream(sock.getLocalOutputStream());
		socket = sock.createSocket();
		new Thread(dispatcher = new Dispatcher(childSockets, dis)).start();
	}

	public Module[] getUsedModules() {
		return new Module[] { listener };
	}

	protected void tryDispose() throws IOException {
		listener.dispose();
		dispatcher.dispose();
		dos.close();
		for (int i = 0; i < childSockets.size(); i++) {
			if (childSockets.get(i) != null)
				((Socket) childSockets.get(i)).close();
		}
	}

	protected Socket tryAccept() throws IOException, NoMoreSocketsException {
		if (socket != null) {
			Socket s = socket;
			socket = null;
			return s;
		}
		while (true) {
			Socket s = listener.accept();
			final int id;
			synchronized (childSockets) {
				id = childSockets.size();
				childSockets.add(s);
			}
			synchronized (dos) {
				dos.writeInt(id);
				dos.writeShort(-1);
				dos.flush();
			}
			final InputStream in = s.getInputStream();
			new Thread(new Handler(dos, in, id)).start();
		}
	}

	public static final class Dispatcher implements Runnable {
		private final List childSockets;
		private final DataInputStream dis;
		private boolean disposed;

		public Dispatcher(List childSockets, DataInputStream dis) {
			this.childSockets = childSockets;
			this.dis = dis;
		}

		public void run() {
			try {
				while (!disposed) {
					int id = dis.readInt();
					int length = dis.readShort();
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
			} catch (EOFException ex) {
				// done
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		public void dispose() throws IOException {
			disposed = true;
			dis.close();
		}
	}

	public static final class Handler implements Runnable {
		private final DataOutputStream dos;
		private final InputStream in;
		private final int id;

		public Handler(DataOutputStream dos, InputStream in, int id) {
			this.dos = dos;
			this.in = in;
			this.id = id;
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
