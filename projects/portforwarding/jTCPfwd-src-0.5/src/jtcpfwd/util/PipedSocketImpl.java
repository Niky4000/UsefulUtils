package jtcpfwd.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;

public class PipedSocketImpl extends SocketImpl {

	private final PipedInputStream in;
	private PipedInputStream inLocal;

	private OutputStream out;
	private final OutputStream outLocal;
	private final InetAddress remoteAddress;

	public PipedSocketImpl() throws IOException {
		this(InetAddress.getByName("127.0.0.1"));
	}

	public PipedSocketImpl(InetAddress remoteAddress) throws IOException {
		this.remoteAddress = remoteAddress;
		in = new PipedInputStream();
		inLocal = new PipedInputStream();
		out = new WrappedPipedOutputStream(new PipedOutputStream(inLocal));
		outLocal = new WrappedPipedOutputStream(new PipedOutputStream(in));
	}

	public void overwriteOutputStream(OutputStream newOut) {
		if (inLocal == null)
			throw new IllegalStateException("Output stream has already been overwritten");
		inLocal = null;
		out = newOut;
	}

	public Socket createSocket() throws IOException {
		Socket sock = new PipedSocket(this);
		sock.connect(new InetSocketAddress(remoteAddress, 1111));
		return sock;
	}

	protected void accept(SocketImpl s) throws IOException {
		throw new UnsupportedOperationException();
	}

	protected int available() throws IOException {
		return in.available();
	}

	protected void bind(InetAddress host, int port) throws IOException {
		throw new UnsupportedOperationException();
	}

	protected void close() throws IOException {
		out.close();
		outLocal.close();
	}

	protected void connect(String host, int port) throws IOException {
		throw new UnsupportedOperationException();
	}

	protected void connect(InetAddress address, int port) throws IOException {
		throw new UnsupportedOperationException();
	}

	protected void connect(SocketAddress address, int timeout) throws IOException {
		this.address = ((InetSocketAddress) address).getAddress();
		this.port = ((InetSocketAddress) address).getPort();
	}

	protected void create(boolean stream) throws IOException {
		if (!stream)
			throw new UnsupportedOperationException();
	}

	protected InputStream getInputStream() throws IOException {
		return in;
	}

	protected OutputStream getOutputStream() throws IOException {
		return out;
	}

	public InputStream getLocalInputStream() {
		return inLocal;
	}

	public OutputStream getLocalOutputStream() {
		return outLocal;
	}

	protected void listen(int backlog) throws IOException {
		throw new UnsupportedOperationException();
	}

	protected void sendUrgentData(int data) throws IOException {
		throw new UnsupportedOperationException();
	}

	public Object getOption(int optID) throws SocketException {
		throw new UnsupportedOperationException();
	}

	public void setOption(int optID, Object value) throws SocketException {
		throw new UnsupportedOperationException();
	}

	public static class PipedSocket extends Socket {
		public PipedSocket(SocketImpl impl) throws SocketException {
			super(impl);
		}
	}
}
