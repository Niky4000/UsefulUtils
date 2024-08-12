package jtcpfwd.util.udp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import jtcpfwd.util.BurstLimitedRandom;

public final class UDPAsyncSender implements Runnable {
	private final DataInputStream in;
	private final SocketAddress address;
	private final BurstLimitedRandom dropRandomSend;
	private final DatagramSocket ds;

	public UDPAsyncSender(BurstLimitedRandom dropRandomSend, DataInputStream in, SocketAddress address, DatagramSocket ds) {
		this.dropRandomSend = dropRandomSend;
		this.in = in;
		this.address = address;
		this.ds = ds;
	}

	public void run() {
		try {
			while (true) {
				int length = in.readShort();
				byte[] data = new byte[length];
				in.readFully(data);
				if (dropRandomSend == null || !dropRandomSend.nextBoolean())
					ds.send(new DatagramPacket(data, length, address));
			}
		} catch (EOFException ex) {
			// done
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}