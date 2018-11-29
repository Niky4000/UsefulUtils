package jtcpfwd.util.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jtcpfwd.util.EnglishWordCoder;
import jtcpfwd.util.PollingHandler;

public class CamouflageHTTPTunnelEngine extends HTTPTunnelEngine {
	private final String argumentName;

	public CamouflageHTTPTunnelEngine(PollingHandlerFactory factory, String argumentName) {
		super(factory);
		this.argumentName = argumentName;
	}

	public boolean doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return false;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		String value = request.getParameter(argumentName);
		if (value == null) {
			out.println("parameter " + argumentName + " missing");
			return;
		}
		byte[] rawData = EnglishWordCoder.decode(value.trim());
		if (rawData.length < 16) {
			out.println("parameter too short");
			return;
		}
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rawData));
		int id = dis.readInt();
		String createParam = id == -1 ? dis.readUTF() : null;
		int offs = 16;
		if (createParam != null)
			offs += createParam.getBytes("UTF-8").length + 2;
		int receiveOffset = dis.readInt();
		int sendOffset = dis.readInt();
		int timeout = dis.readInt();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		if (id == -1) {
			try {
				id = createHandler(request, out, createParam);
				if (id == -1)
					return;
				dos.writeByte(1);
				dos.writeInt(id);
			} catch (Exception ex) {
				ex.printStackTrace(new PrintStream(out));
				out.flush();
				return;
			}
		}
		PollingHandler handler = getHandler(id);
		handler.setReceiveOffset(receiveOffset);
		handler.receiveBytes(rawData, offs, rawData.length - offs);
		dos.writeByte(2);
		dos.writeInt(rawData.length - offs);

		int generation = handler.setSendOffset(sendOffset);
		byte[] data = handler.getSendBytes(timeout, -1, false, generation);
		if (data == null) {
			dos.writeByte(-1);
			removeHandler(id);
		} else {
			dos.writeByte(0);
			dos.writeInt(data.length);
			dos.write(data);
		}
		dos.writeByte(42);
		out.println(("<pre>\n" + new EnglishWordCoder(baos.toByteArray()).getPlainText() + "\n</pre>").replaceAll("\n", "\r\n"));
	}
}
