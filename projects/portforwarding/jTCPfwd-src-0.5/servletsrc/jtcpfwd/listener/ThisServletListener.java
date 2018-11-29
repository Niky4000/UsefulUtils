package jtcpfwd.listener;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThisServletListener extends Listener {

	private static List/* <ThisServletListener> */listeners = new ArrayList();

	public static final Class[] getRequiredClasses() {
		throw new UnsupportedOperationException("ThisServletListener cannot be included into jTCPfwd lite version");
	}

	public static synchronized int assignId() {
		listeners.add(null);
		return listeners.size() - 1;
	}

	public static synchronized ThisServletListener getById(int id) {
		return (ThisServletListener) listeners.get(id);
	}

	public static final String SYNTAX = "ThisServlet@<id>";

	private List/* <Socket> */socketsToAccept = new ArrayList();

	public ThisServletListener(String rule) throws Exception {
		int id = Integer.parseInt(rule);
		synchronized (ThisServletListener.class) {
			if (listeners.get(id) != null)
				throw new RuntimeException("Listener " + id + " instantiated twice");
			listeners.set(id, this);
		}
	}

	public synchronized void addSocketToAccept(Socket socket) {
		socketsToAccept.add(socket);
		notifyAll();
	}

	protected synchronized Socket tryAccept() throws IOException {
		try {
			while (socketsToAccept.size() == 0 && !disposed)
				wait();
			if (disposed)
				return null;
			return (Socket) socketsToAccept.remove(0);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected synchronized void tryDispose() throws IOException {
		disposed = true;
		notifyAll();
	};
}
