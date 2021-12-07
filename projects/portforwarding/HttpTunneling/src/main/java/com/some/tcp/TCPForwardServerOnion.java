package com.some.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author me
 */
public class TCPForwardServerOnion extends TCPForwardServer {

	public void init(int sourcePort, String destinationHost, int destinationPort) throws IOException {
		ServerSocket serverSocket = new ServerSocket(sourcePort);
		while (true) {
			Socket clientSocket = serverSocket.accept();
//			// Connect to the destination server 
//			Socket mServerSocket = new Socket(destinationHost, destinationPort);
//			// Turn on keep-alive for both the sockets 
//			mServerSocket.setKeepAlive(true);
//			clientSocket.setKeepAlive(true);
//			ClientThread clientThread = new ClientThread(clientSocket, mServerSocket);
//			clientThread.start();
			InetSocketAddress hiddenerProxyAddress = new InetSocketAddress("127.0.0.1", 9050);
			Proxy hiddenProxy = new Proxy(Proxy.Type.SOCKS, hiddenerProxyAddress);
			Socket mServerSocket = new Socket(hiddenProxy);
//		SocketAddress sa = new InetSocketAddress("www.facebook.com", 80);
			InetSocketAddress sa = InetSocketAddress.createUnresolved(destinationHost, destinationPort);
			mServerSocket.connect(sa);
//			Socket mServerSocket = new Socket(destinationHost, destinationPort);
			// Turn on keep-alive for both the sockets 
			mServerSocket.setKeepAlive(true);
			clientSocket.setKeepAlive(true);
			ClientThread clientThread = new ClientThread(clientSocket, mServerSocket);
			clientThread.start();
		}
	}
}
