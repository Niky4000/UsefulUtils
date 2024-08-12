package jtcpfwd.forwarder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import jtcpfwd.Module;
import jtcpfwd.destination.Destination;
import jtcpfwd.util.BurstLimitedRandom;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;
import jtcpfwd.util.udp.UDPAsyncSender;

public class UDPForwarder extends Forwarder {

	public static final String SYNTAX = "UDP@[<dropNumerator>,<dropDenominator>;]<destination>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Destination.class, BurstLimitedRandom.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class, UDPAsyncSender.class,
				AsyncReceiver.class };
	}

	private final Destination destination;
	private final BurstLimitedRandom dropRandomReceive, dropRandomSend;

	public UDPForwarder(String rule) throws Exception {
		int pos = rule.indexOf(';');
		if (pos != -1) {
			Random r = new Random();
			dropRandomReceive = BurstLimitedRandom.parse(r, rule.substring(0, pos));
			dropRandomSend = BurstLimitedRandom.parse(r, rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		} else {
			dropRandomReceive = dropRandomSend = null;
		}
		destination = Destination.lookupDestination(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { destination };
	}

	public Socket connect(Socket listener) {
		try {
			final InetSocketAddress target = destination.getNextDestination();
			if (target == null)
				return null;
			final DatagramSocket ds = new DatagramSocket();
			PipedSocketImpl sock = new PipedSocketImpl();
			final DataInputStream dis = new DataInputStream(sock.getLocalInputStream());
			final DataOutputStream dos = new DataOutputStream(sock.getLocalOutputStream());
			new Thread(new UDPAsyncSender(dropRandomSend, dis, target, ds)).start();
			new Thread(new AsyncReceiver(ds, dos, dropRandomReceive)).start();
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		destination.dispose();
	}

	public static final class AsyncReceiver implements Runnable {
		private final DatagramSocket ds;
		private final DataOutputStream dos;
		private final BurstLimitedRandom dropRandomReceive;

		AsyncReceiver(DatagramSocket ds, DataOutputStream dos, BurstLimitedRandom dropRandomReceive) {
			this.ds = ds;
			this.dos = dos;
			this.dropRandomReceive = dropRandomReceive;
		}

		public void run() {
			try {
				while (true) {
					DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
					ds.receive(dp);
					if (dropRandomReceive != null && dropRandomReceive.nextBoolean())
						continue;
					dos.writeShort(dp.getLength());
					dos.write(dp.getData(), dp.getOffset(), dp.getLength());
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
