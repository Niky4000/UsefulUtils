package jtcpfwd.listener.knockrule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class UDPDNSKnockRule extends KnockRule implements Runnable {

	public static final String SYNTAX = "UDPDNS#<port>[#<openname>[#<ipname>]]";

	private static InetAddress RESPONSE_IP;

	static {
		try {
			RESPONSE_IP = InetAddress.getByName("1.3.3.7");
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}
	}

	private final int port;
	private final String openName;
	private final String ipName;
	private DatagramSocket ds;

	public UDPDNSKnockRule(String[] args) throws Exception {
		port = Integer.parseInt(args[0]);
		openName = (args.length > 1) ? args[1] : "open";
		ipName = (args.length > 2) ? args[2] : "ip";
		if (args.length > 3)
			throw new IllegalArgumentException("Invalid number of parameters");
	}

	public void listen() throws IOException {
		ds = new DatagramSocket(port);
		new Thread(this).start();
	}

	public void tryDispose() throws IOException {
		if (ds != null)
			ds.close();
	}

	public void run() {
		try {
			while (!disposed) {
				DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
				ds.receive(dp);
				byte[] packet = new byte[dp.getLength()];
				System.arraycopy(dp.getData(), 0, packet, 0, packet.length);
				if (checkPacket(dp, openName)) {
					addAddress(dp.getAddress());
					byte[] response = buildDNSPacket(packet, openName, RESPONSE_IP);
					ds.send(new DatagramPacket(response, response.length, dp.getAddress(), dp.getPort()));
				} else if (checkPacket(dp, ipName)) {
					byte[] response = buildDNSPacket(packet, ipName, dp.getAddress());
					ds.send(new DatagramPacket(response, response.length, dp.getAddress(), dp.getPort()));
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private boolean checkPacket(DatagramPacket dp, String name) throws IOException {
		if (name.equals("-") || dp.getLength() != 18 + name.length())
			return false;
		byte[] packet = new byte[dp.getLength()];
		System.arraycopy(dp.getData(), 0, packet, 0, packet.length);
		return Arrays.equals(packet, buildDNSPacket(packet, name, null));
	}

	public void trigger(InetAddress peer) throws IOException {

		byte[] packet = buildDNSPacket(new byte[] { 0x13, 0x37 }, openName, null);
		DatagramPacket dp = new DatagramPacket(packet, packet.length, peer, port);
		DatagramSocket ds = new DatagramSocket();
		ds.send(dp);
		byte[] response = buildDNSPacket(packet, openName, RESPONSE_IP);
		while (true) {
			dp = new DatagramPacket(new byte[response.length], response.length);
			ds.receive(dp);
			if (dp.getAddress().equals(peer) && dp.getLength() == response.length && Arrays.equals(dp.getData(), response))
				break;
		}
		ds.close();
	}

	/**
	 * Utility method to build a simple DNS request or response packet (only for
	 * type A)
	 * 
	 * @param id
	 *            A byte array (at least 2 bytes long) to take the ID from
	 * @param hostname
	 *            A simple hostname (only letters) to query
	 * @param address
	 *            The response address, or <code>null</code> to build a request
	 * @return The request/response
	 */
	private static byte[] buildDNSPacket(byte[] id, String msg, InetAddress address) throws IOException {
		boolean answer = address != null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// header
		baos.write(new byte[] {
				id[0], id[1], // ID
				answer ? (byte) 0x81 : 0x01, 0x00, // flags
				0x00, 0x01, 0x00, answer ? (byte) 0x01 : 0x00, 0x00, 0x00, 0x00, 0x00, // sections
		});
		// question section
		baos.write(msg.length());
		baos.write(msg.getBytes("ISO-8859-1"));
		baos.write(0x00); // terminator
		baos.write(new byte[] { 0x00, 0x01, 0x00, 0x01 }); // type + class
		// answer section
		if (answer) {
			baos.write(new byte[] {
					(byte) 0xc0, 0x0c, // offset of the name in question section
					0x00, 0x01, 0x00, 0x01, // type + class
					0x00, 0x00, 0x00, 0x00, // TTL
					0x00, 0x04, // RR length
			});
			baos.write(address.getAddress());
		}
		// authority section and additional section are empty
		return baos.toByteArray();
	}
}
