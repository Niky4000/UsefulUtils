package jtcpfwd.listener;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.PipedSocketImpl.PipedSocket;
import jtcpfwd.util.WrappedPipedOutputStream;
import jtcpfwd.util.udp.BidirectionalUDPStream;

public class UDPTunnelPTPListener extends Listener {

	public static final String SYNTAX = "UDPTunnelPTP@[<interface>:]<port>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { BidirectionalUDPStream.class,
				BidirectionalUDPStream.ReaderThreadRunner.class,
				BidirectionalUDPStream.WriterThreadRunner.class,
				WrappedPipedOutputStream.class,
				PipedSocketImpl.class, PipedSocket.class };
	}

	private final DatagramSocket socket;
	private boolean accepted = false;

	public UDPTunnelPTPListener(String rule) throws Exception {
		InetAddress bindAddr = null;
		int pos = rule.indexOf(':');
		if (pos != -1) {
			bindAddr = InetAddress.getByName(rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		}
		socket = new DatagramSocket(Integer.parseInt(rule), bindAddr);
	}

	protected synchronized Socket tryAccept() throws IOException {
		try {
			if (accepted) {
				while (true)
					wait();
			}
			accepted = true;
			PipedSocketImpl sock = new PipedSocketImpl();
			DatagramPacket dp = new DatagramPacket(new byte[512], 512);
			// get and discard the first packet to obtain peer address
			// (there can be only one)
			socket.receive(dp);
			OutputStream out = new BidirectionalUDPStream(sock.getLocalOutputStream(), socket, dp.getAddress(), dp.getPort());
			sock.overwriteOutputStream(out);
			return sock.createSocket();
		} catch (InterruptedException ex) {
			throw new InterruptedIOException();
		}
	}

	protected void tryDispose() throws IOException {
		socket.close();
	}
}