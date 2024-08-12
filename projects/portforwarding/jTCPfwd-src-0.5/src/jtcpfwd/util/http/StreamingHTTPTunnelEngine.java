package jtcpfwd.util.http;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ConcurrentModificationException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jtcpfwd.util.PollingHandler;

public class StreamingHTTPTunnelEngine extends HTTPTunnelEngine {

	public StreamingHTTPTunnelEngine(PollingHandlerFactory factory) {
		super(factory);
	}

	public boolean doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();

		String id = request.getParameter("id");
		boolean writeID = false;
		if (id != null) {
		} else {
			try {
				int newID = createHandler(request, out, request.getParameter("create"));
				if (newID == -1)
					return true;
				id = "" + newID;
				writeID = true;
			} catch (Exception ex) {
				ex.printStackTrace(new PrintStream(out));
				out.flush();
				return true;
			}
		}
		String timeout = request.getParameter("t");
		int t = timeout != null ? Integer.parseInt(timeout) : 15000;
		if (timeout == null) {
			response.setContentLength(104857600);
		}
		response.setContentType("application/x-tunneled");
		if (writeID) {
			out.print("\1" + id + " ");
		}
		out.flush();
		response.flushBuffer();

		PollingHandler handler = getHandler(Integer.parseInt(id));
		String off = request.getParameter("off");
		int generation = handler.setSendOffset(Integer.parseInt(off));
		try {
			byte[] data = handler.getSendBytes(t, -1, timeout == null, generation);
			if (data == null) {
				out.write(-1);
				removeHandler(Integer.parseInt(id));
			} else {
				out.write(0);
				out.write(data);
				if (timeout == null) {
					out.flush();
					response.flushBuffer();
					while ((data = handler.getSendBytes(t, -1, true, generation)) != null) {
						out.write(data);
						out.flush();
						response.flushBuffer();
					}
				}
			}
		} catch (ConcurrentModificationException ex) {
			out.flush();
			response.flushBuffer();
		}
		return true;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletInputStream in = request.getInputStream();
		ServletOutputStream out = response.getOutputStream();
		StringBuffer sb = new StringBuffer();
		int b;
		while ((b = in.read()) != -1 && b != ';')
			sb.append((char) b);
		String[] params = sb.toString().split(",");
		if (params.length != 2) {
			out.println("invalid param count");
			return;
		}
		String id = params[0];
		PollingHandler handler = getHandler(Integer.parseInt(id));
		String off = params[1];
		if (off.length() > 0)
			handler.setReceiveOffset(Integer.parseInt(off));
		byte[] buf = new byte[4096];
		int received = 0;
		int len;
		while ((len = in.read(buf)) != -1) {
			handler.receiveBytes(buf, 0, len);
			received += len;
		}
		response.setContentType("application/x-tunneled");
		response.getOutputStream().write(String.valueOf(received).getBytes());
	}
}
