package jtcpfwd.forwarder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import jtcpfwd.Module;
import jtcpfwd.destination.Destination;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.PipedSocketImpl.PipedSocket;
import jtcpfwd.util.WrappedPipedOutputStream;
import jtcpfwd.util.udp.BidirectionalUDPStream;

public class UDPTunnelPTPForwarder extends Forwarder {

	public static final String SYNTAX = "UDPTunnelPTP@<destination>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Destination.class,
				BidirectionalUDPStream.class,
				BidirectionalUDPStream.ReaderThreadRunner.class,
				BidirectionalUDPStream.WriterThreadRunner.class,
				WrappedPipedOutputStream.class,
				PipedSocketImpl.class, PipedSocket.class };
	}

	private final Destination destination;

	public UDPTunnelPTPForwarder(String rule) throws Exception {
		destination = Destination.lookupDestination(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { destination };
	}

	public void dispose() throws IOException {
		destination.dispose();
	}

	public Socket connect(Socket listener) {
		try {
			final InetSocketAddress target = destination.getNextDestination();
			if (target == null)
				return null;
			PipedSocketImpl sock = new PipedSocketImpl();
			DatagramSocket socket = new DatagramSocket();
			OutputStream out = new BidirectionalUDPStream(sock.getLocalOutputStream(), socket, target.getAddress(), target.getPort());
			sock.overwriteOutputStream(out);
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}