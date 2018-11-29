package jtcpfwd.listener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jtcpfwd.util.BurstLimitedRandom;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;
import jtcpfwd.util.udp.UDPAsyncSender;

public class UDPListener extends Listener {

	public static final String SYNTAX = "UDP@[<dropNumerator>,<dropDenominator>;][<interface>:]<port>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				BurstLimitedRandom.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class, UDPAsyncSender.class };
	}

	private final DatagramSocket ds;
	private final Map/* <InetSocketAddress,DataOutputStream> */socketStreams = new HashMap();
	private final BurstLimitedRandom dropRandomReceive, dropRandomSend;

	public UDPListener(String rule) throws Exception {
		InetAddress bindAddr = null;
		int pos = rule.indexOf(';');
		if (pos != -1) {
			Random r = new Random();
			dropRandomReceive = BurstLimitedRandom.parse(r, rule.substring(0, pos));
			dropRandomSend = BurstLimitedRandom.parse(r, rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		} else {
			dropRandomReceive = dropRandomSend = null;
		}
		pos = rule.indexOf(':');
		if (pos != -1) {
			bindAddr = InetAddress.getByName(rule.substring(0, pos));
			rule = rule.substring(pos + 1);
		}
		ds = new DatagramSocket(Integer.parseInt(rule), bindAddr);
	}

	protected synchronized Socket tryAccept() throws IOException {
		Socket newSocket = null;
		while (newSocket == null) {
			DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
			ds.receive(dp);
			if (dropRandomReceive != null && dropRandomReceive.nextBoolean())
				continue;
			final SocketAddress address = dp.getSocketAddress();
			DataOutputStream out = (DataOutputStream) socketStreams.get(address);
			if (out == null) {
				PipedSocketImpl sock = new PipedSocketImpl(((InetSocketAddress) address).getAddress());
				newSocket = sock.createSocket();
				out = new DataOutputStream(sock.getLocalOutputStream());
				final DataInputStream in = new DataInputStream(sock.getLocalInputStream());
				new Thread(new UDPAsyncSender(dropRandomSend, in, address, ds)).start();
				socketStreams.put(address, out);
			}
			out.writeShort(dp.getLength());
			out.write(dp.getData(), dp.getOffset(), dp.getLength());
		}
		return newSocket;
	}

	protected void tryDispose() throws IOException {
		ds.close();
	}
}
