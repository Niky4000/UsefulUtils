package jtcpfwd.util.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class UDPDispatcher extends Thread {
	private final DatagramSocket ds;
	private final Map/* <SocketAddress,UDPHandler> */handlers = new HashMap();
	private final UDPHandler wildcardHandler;

	public UDPDispatcher(UDPHandler wildcardHandler, DatagramSocket ds) {
		this.wildcardHandler = wildcardHandler;
		this.ds = ds;
		setName("UDPDispatcher");
	}

	public DatagramSocket getDatagramSocket() {
		return ds;
	}

	public void registerHandler(SocketAddress target, UDPHandler sender) {
		handlers.put(target, sender);
	}

	public void run() {
		try {
			byte[] buffer = new byte[512];
			while (true) {
				DatagramPacket dp = new DatagramPacket(buffer, 512);
				ds.receive(dp);
				UDPHandler handler = (UDPHandler) handlers.get(dp.getSocketAddress());
				byte[] data = new byte[dp.getLength()];
				System.arraycopy(buffer, dp.getOffset(), data, 0, dp.getLength());
				if (handler == null)
					handler = wildcardHandler;
				if (handler != null)
					handler.handle(data, (InetSocketAddress) dp.getSocketAddress());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
