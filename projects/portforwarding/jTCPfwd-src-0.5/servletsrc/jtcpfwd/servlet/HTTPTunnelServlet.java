package jtcpfwd.servlet;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jtcpfwd.ForwarderThread;
import jtcpfwd.listener.ThisServletListener;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.PollingHandler;
import jtcpfwd.util.http.HTTPTunnelEngine;
import jtcpfwd.util.http.PollingHandlerFactory;

public class HTTPTunnelServlet extends HttpServlet implements PollingHandlerFactory {

	private static final String USAGE_MESSAGE = "<h1>jTCPfwd HTTP Tunnel Servlet</h1><p>Use the <tt>HTTPTunnel</tt> forwarder to connect to this servlet.</p>";
	private static final String DISABLED_MESSAGE = "<h1>jTCPfwd HTTP Tunnel Servlet</h1><p>This servlet does not use http tunnelling.</p>";
	private int tunnellingID = -1;
	private HTTPTunnelEngine engine;
	private ForwarderThread thread;

	public void init() throws ServletException {
		String listener = getInitParameter("listener");
		int pos = listener.indexOf("ThisServlet@");
		if (pos != -1) {
			pos += "ThisServlet@".length();
			tunnellingID = ThisServletListener.assignId();
			listener = listener.substring(0, pos) + tunnellingID + listener.substring(pos);
		}
		String forwarder = getInitParameter("forwarder");
		try {
			thread = new ForwarderThread(listener, forwarder);
			thread.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String engineParam = getInitParameter("engine");
		engine = HTTPTunnelEngine.getInstance(this, engineParam);
	}

	public void destroy() {
		try {
			thread.dispose();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public PollingHandler createHandler(InetAddress remoteAddress, ServletOutputStream out, String createParam) throws IOException {
		if (createParam == null || !createParam.equals("1")) {
			out.println(USAGE_MESSAGE);
			out.flush();
			return null;
		}
		PipedSocketImpl sock = new PipedSocketImpl(remoteAddress);
		PollingHandler handler = new PollingHandler(sock.getLocalOutputStream(), 1048576);
		sock.overwriteOutputStream(handler);
		ThisServletListener.getById(tunnellingID).addSocketToAccept(sock.createSocket());
		return handler;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (tunnellingID == -1) {
			response.getOutputStream().println(DISABLED_MESSAGE);
			return;
		}
		if (!engine.doGet(request, response)) {
			response.getOutputStream().println(USAGE_MESSAGE);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (tunnellingID == -1) {
			response.getOutputStream().println(DISABLED_MESSAGE);
			return;
		}
		engine.doPost(request, response);
	}
}