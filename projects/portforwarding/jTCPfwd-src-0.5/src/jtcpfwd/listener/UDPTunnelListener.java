package jtcpfwd.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;
import jtcpfwd.util.udp.UDPDispatcher;
import jtcpfwd.util.udp.UDPHandler;
import jtcpfwd.util.udp.UDPReceiver;
import jtcpfwd.util.udp.UDPSender;

public class UDPTunnelListener extends Listener implements UDPHandler {

	public static final String SYNTAX = "UDPTunnel@[<interface>:]<port>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				AsyncSender.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				UDPDispatcher.class,
				UDPReceiver.class, UDPReceiver.ReceiverThread.class,
				UDPHandler.class, UDPSender.class, UDPSender.SenderThread.class,
				WrappedPipedOutputStream.class };
	}

	private final UDPDispatcher disp;
	private final List/* <Socket> */sockets = new ArrayList();
	private final List/* <UDPSender> */senders = new ArrayList();

	public UDPTunnelListener(String rule) throws Exception {
		InetAddress bindAddr = null;
		int pos = rule.indexOf(':');
		if (pos != -1) {
			bindAddr = InetAddress.getByName(rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		}
		DatagramSocket ds = new DatagramSocket(Integer.parseInt(rule), bindAddr);
		disp = new UDPDispatcher(this, ds);
		disp.start();
	}

	public void handle(byte[] data, InetSocketAddress address) throws IOException {
		if (data.length == 4) {
			disp.getDatagramSocket().send(new DatagramPacket(data, 0, 4, address));
			final UDPSender sender = new UDPSender(disp, address);
			final int senderIndex;
			synchronized (this) {
				senderIndex = senders.size();
				senders.add(sender);
			}
			new Thread(new AsyncSender(sender, senderIndex)).start();
		} else if (data.length == 8) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
			int sequenceNumber = dis.readInt();
			if (sequenceNumber != 1) {
				System.err.println("Invalid sequence number for initial packet: " + sequenceNumber);
				return;
			}
			int senderIndex = dis.readInt();

			UDPReceiver receiver = new UDPReceiver(disp, address, 2);
			PipedSocketImpl sock = new PipedSocketImpl((address).getAddress());
			Socket socket = sock.createSocket();
			UDPSender sender;
			synchronized (this) {
				sender = (UDPSender) senders.get(senderIndex);
				senders.set(senderIndex, null);
				sockets.add(socket);
				notifyAll();
			}
			sender.sendAsync(socket, sock.getLocalInputStream());
			receiver.receiveAsync(socket, sock.getLocalOutputStream());

			disp.getDatagramSocket().send(new DatagramPacket(data, 0, 4, address));
		} else {
			System.err.println("Unexpected packet of length " + data.length);
		}
	}

	protected synchronized Socket tryAccept() throws IOException {
		try {
			while (sockets.size() == 0) {
				if (disposed)
					return null;
				wait();
			}
			return (Socket) sockets.remove(0);
		} catch (InterruptedException ex) {
			throw new InterruptedIOException();
		}
	}

	protected synchronized void tryDispose() throws IOException {
		disp.getDatagramSocket().close();
		disposed = true;
		notifyAll();
	}

	public static final class AsyncSender implements Runnable {
		private final UDPSender sender;
		private final int senderIndex;

		public AsyncSender(UDPSender sender, int senderIndex) {
			this.sender = sender;
			this.senderIndex = senderIndex;
		}

		public void run() {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeInt(senderIndex);
				sender.send(baos.toByteArray(), 0, 4);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
