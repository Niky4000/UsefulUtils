package jtcpfwd.util.udp;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface UDPHandler {
	public void handle(byte[] data, InetSocketAddress address) throws IOException;
}
