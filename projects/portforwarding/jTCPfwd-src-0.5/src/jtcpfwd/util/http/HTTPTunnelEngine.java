package jtcpfwd.util.http;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jtcpfwd.util.PollingHandler;

public abstract class HTTPTunnelEngine {

	private static final long serialVersionUID = 1L;

	private static final List/* <PollingHandler> */allHandlers = new ArrayList();

	public static HTTPTunnelEngine getInstance(PollingHandlerFactory factory, String param) {
		if (param == null || param.length() == 0)
			return new StreamingHTTPTunnelEngine(factory);
		else if (param.startsWith("Camouflage=")) {
			return new CamouflageHTTPTunnelEngine(factory, param.substring(11));
		} else {
			throw new IllegalArgumentException("Unsupported HTTP tunnel parameter");
		}
	}

	private final PollingHandlerFactory factory;

	protected HTTPTunnelEngine(PollingHandlerFactory factory) {
		this.factory = factory;
	}

	public abstract boolean doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	public abstract void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	protected PollingHandler getHandler(int id) {
		synchronized (allHandlers) {
			Object handler = allHandlers.get(id);
			if (handler == null)
				throw new NoSuchElementException();
			return (PollingHandler) handler;
		}
	}

	protected void removeHandler(int id) {
		synchronized (allHandlers) {
			allHandlers.set(id, null);
			while (id == allHandlers.size() - 1 && allHandlers.get(id) == null) {
				allHandlers.remove(id);
				id--;
			}
		}
	}

	protected int createHandler(HttpServletRequest request, ServletOutputStream out, String createParam) throws Exception {
		PollingHandler handler = factory.createHandler(InetAddress.getByName(request.getRemoteAddr()), out, createParam);
		if (handler == null)
			return -1;
		synchronized (allHandlers) {
			int id = allHandlers.size();
			allHandlers.add(handler);
			return id;
		}
	}
}
