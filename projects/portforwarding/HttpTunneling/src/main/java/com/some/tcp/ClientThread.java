package com.some.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * ClientThread is responsible for starting forwarding between the client and
 * the server. It keeps track of the client and servers sockets that are both
 * closed on input/output error durinf the forwarding. The forwarding is
 * bidirectional and is performed by two ForwardThread instances.
 */
class ClientThread extends Thread {

	private Socket mClientSocket;
	private Socket mServerSocket;
	private boolean mForwardingActive = false;
	Semaphore semaphore = new Semaphore(0);

	public ClientThread(Socket aClientSocket, Socket mServerSocket) {
		this.setName("ClientThread");
		mClientSocket = aClientSocket;
		this.mServerSocket = mServerSocket;
	}

	/**
	 * Establishes connection to the destination server and starts bidirectional
	 * forwarding ot data between the client and the server.
	 */
	@Override
	public void run() {
		InputStream clientIn;
		OutputStream clientOut;
		InputStream serverIn;
		OutputStream serverOut;
		try {

			// Turn on keep-alive for both the sockets 
//            mServerSocket.setKeepAlive(true);
//            mClientSocket.setKeepAlive(true);
			// Obtain client & server input & output streams 
			clientIn = mClientSocket.getInputStream();
			clientOut = mClientSocket.getOutputStream();
			serverIn = mServerSocket.getInputStream();
			serverOut = mServerSocket.getOutputStream();
		} catch (IOException ioe) {
//            System.err.println("Can not connect to "
//                    + destinationHost + ":"
//                    + destinationPort);
			ioe.printStackTrace();
			connectionBroken();
			return;
		}

		// Start forwarding data between server and client 
		mForwardingActive = true;
		String dateStr = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date());
		ForwardThread clientForward = new ForwardThread(this, clientIn, serverOut, "ForwardThread1_" + dateStr);
		clientForward.start();
		ForwardThread serverForward = new ForwardThread(this, serverIn, clientOut, "ForwardThread2_" + dateStr);
		serverForward.start();

		System.out.println("TCP Forwarding "
			+ (mClientSocket.getInetAddress() != null ? mClientSocket.getInetAddress().getHostAddress() : "unknown InetAddress")
			+ ":" + mClientSocket.getPort() + " <--> "
			+ (mServerSocket.getInetAddress() != null ? mServerSocket.getInetAddress().getHostAddress() : "unknown server InetAddress")
			+ ":" + mServerSocket.getPort() + " started.");
	}

	/**
	 * Called by some of the forwarding threads to indicate that its socket
	 * connection is brokean and both client and server sockets should be
	 * closed. Closing the client and server sockets causes all threads blocked
	 * on reading or writing to these sockets to get an exception and to finish
	 * their execution.
	 */
	public synchronized void connectionBroken() {
		try {
			mServerSocket.close();
		} catch (Exception e) {
		}
		try {
			mClientSocket.close();
		} catch (Exception e) {
		}

		if (mForwardingActive) {
			System.out.println("TCP Forwarding "
				+ (mClientSocket.getInetAddress() != null ? mClientSocket.getInetAddress().getHostAddress() : "unknown InetAddress")
				+ ":" + mClientSocket.getPort() + " <--> "
				+ (mServerSocket.getInetAddress() != null ? mServerSocket.getInetAddress().getHostAddress() : "unknown server InetAddress")
				+ ":" + mServerSocket.getPort() + " stopped.");
			mForwardingActive = false;
			semaphore.release();
		}
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

}
