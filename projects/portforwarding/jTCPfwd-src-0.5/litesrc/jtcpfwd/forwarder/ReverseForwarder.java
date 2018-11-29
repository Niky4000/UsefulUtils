package jtcpfwd.forwarder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ReverseForwarder extends Forwarder implements Runnable {

	public static final String SYNTAX = "Reverse@[<interface>:]<port>";

	private final ServerSocket ss;
	private Socket next = null;
	private boolean disposed = false;

	public ReverseForwarder(String rule) throws Exception {
		InetAddress bindAddr = null;
		int pos = rule.lastIndexOf(':');
		if (pos != -1) {
			bindAddr = InetAddress.getByName(rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		}
		ss = new ServerSocket(Integer.parseInt(rule), 50, bindAddr);
		Thread reverseListener = new Thread(this);
		reverseListener.setName("ReverseListener " + rule);
		reverseListener.start();
	}

	public Socket connect(Socket listener) {
		try {
			synchronized (this) {
				long end = System.currentTimeMillis() + 10000;
				while (next == null) {
					long rest = end - System.currentTimeMillis();
					if (rest <= 0)
						break;
					wait(rest);
				}
				Socket s = next;
				next = null;
				notifyAll();
				if (s != null) {
					OutputStream out = s.getOutputStream();
					out.write(42);
					out.flush();
				}
				return s;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		disposed = true;
		ss.close();
	}

	public void run() {
		while (!disposed) {
			try {
				Socket s = ss.accept();
				synchronized (this) {
					while (next != null)
						wait();
					next = s;
					notifyAll();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				if (disposed)
					return;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex2) {
				}
			}
		}
	}
}
