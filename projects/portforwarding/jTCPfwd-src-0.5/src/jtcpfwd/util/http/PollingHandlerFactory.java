package jtcpfwd.util.http;

import java.net.InetAddress;

import javax.servlet.ServletOutputStream;

import jtcpfwd.util.PollingHandler;

public interface PollingHandlerFactory {
	public PollingHandler createHandler(InetAddress remoteAddress, ServletOutputStream out, String createParam) throws Exception;
}
