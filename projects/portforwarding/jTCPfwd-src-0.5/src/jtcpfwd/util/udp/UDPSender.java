package jtcpfwd.util.udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

public class UDPSender implements UDPHandler {

	private final DatagramSocket socket;
	private final InetSocketAddress target;

	private boolean waiting = false;
	private int sequenceNumber = 0;

	public UDPSender(UDPDispatcher dispatcher, InetSocketAddress target) {
		this.socket = dispatcher.getDatagramSocket();
		this.target = target;
		dispatcher.registerHandler(target, this);
	}

	public UDPSender(UDPReceiver receiver, InetSocketAddress target) {
		this.socket = receiver.getDatagramSocket();
		this.target = target;
		sequenceNumber = -1;
		receiver.setZeroAckHandler(this);
	}

	public void send(byte[] data, int offset, int length) throws IOException {
		if (length > 508)
			throw new IOException("Packet too long");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		synchronized (this) {
			waiting = true;
			sequenceNumber++;
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(sequenceNumber);
			dos.write(data, offset, length);
			dos.close();
		}
		byte[] result = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(result, result.length, target);
		try {
			for (int i = 0; i < 10; i++) {
				socket.send(dp);
				synchronized (this) {
					if (waiting)
						wait(500);
					if (!waiting)
						return;
				}
			}
			throw new IOException("Packet was not acked");
		} catch (InterruptedException ex) {
			throw new InterruptedIOException();
		}
	}

	public void handle(byte[] data, InetSocketAddress address) throws IOException {
		if (data.length != 4)
			throw new IOException("Invalid ack length");
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		int seq = dis.readInt();
		dis.close();
		synchronized (this) {
			if (seq == sequenceNumber && waiting) {
				waiting = false;
				notifyAll();
			}
		}
	}

	public void sendAsync(Socket socket, InputStream in) {
		new SenderThread(socket, in).start();
	}

	public class SenderThread extends Thread {
		private final Socket socket;
		private final InputStream in;

		public SenderThread(Socket socket, InputStream in) {
			super("SenderThread");
			this.socket = socket;
			this.in = in;
		}

		public void run() {
			try {
				try {
					byte[] buf = new byte[508];
					int len;
					while ((len = in.read(buf)) != -1) {
						send(buf, 0, len);
					}
					send(buf, 0, 0);
				} finally {
					socket.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
