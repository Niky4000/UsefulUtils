package jtcpfwd.forwarder;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import jtcpfwd.Module;
import jtcpfwd.destination.Destination;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;
import jtcpfwd.util.udp.UDPDispatcher;
import jtcpfwd.util.udp.UDPHandler;
import jtcpfwd.util.udp.UDPReceiver;
import jtcpfwd.util.udp.UDPSender;

public class UDPTunnelForwarder extends Forwarder {

	public static final String SYNTAX = "UDPTunnel@<destination>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Destination.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				UDPDispatcher.class,
				UDPReceiver.class, UDPReceiver.ReceiverThread.class,
				UDPHandler.class, UDPSender.class, UDPSender.SenderThread.class,
				WrappedPipedOutputStream.class };
	}

	private final Destination destination;

	public UDPTunnelForwarder(String rule) throws Exception {
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
			DatagramSocket writeSocket = new DatagramSocket();
			UDPDispatcher writeDispatcher = new UDPDispatcher(null, writeSocket);
			writeDispatcher.start();
			DatagramSocket readSocket = new DatagramSocket();
			UDPDispatcher readDispatcher = new UDPDispatcher(null, readSocket);
			readDispatcher.start();
			UDPSender sender = new UDPSender(writeDispatcher, target);
			UDPReceiver receiver = new UDPReceiver(readDispatcher, target, 1);
			UDPSender readSender = new UDPSender(receiver, target);
			readSender.send(new byte[0], 0, 0);
			byte[] id = receiver.receive();
			if (id.length != 4)
				throw new RuntimeException("Received invalid ID of length " + id.length);
			sender.send(id, 0, 4);
			PipedSocketImpl sock = new PipedSocketImpl();
			Socket socket = sock.createSocket();
			sender.sendAsync(socket, sock.getLocalInputStream());
			receiver.receiveAsync(socket, sock.getLocalOutputStream());
			return socket;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
