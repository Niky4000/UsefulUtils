package com.some.tcp;

import com.some.tcp.bean.SocketBean;
import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class TCPForwardServerR2 {

	private static final int QUEUE_SIZE = 16;
	private static final int CRITICAL_QUEUE_SIZE = 2;

	public void init(int sourcePort, int destinationPort) throws IOException, InterruptedException {
		SocketBean socketBean = new SocketBean();
		Thread clientListernerThread = new Thread(() -> {
			serverImpl(destinationPort, socketBean, socket -> socketBean.setClientSocket2(socket));
		});
		Thread serverListernerThread = new Thread(() -> {
			serverImpl(sourcePort, socketBean, socket -> socketBean.setServerSocket2(socket));
		});
		clientListernerThread.setName("clientListernerThread");
		serverListernerThread.setName("serverListernerThread");
		clientListernerThread.start();
		serverListernerThread.start();

		while (true) {
			socketBean.waitForSocketsToBeReady();
			Socket clientSocket2 = socketBean.getClientSocket2();
			Socket serverSocket2 = socketBean.getServerSocket2();
			ClientThread clientThread = new ClientThread(clientSocket2, serverSocket2);
			clientThread.setName("clientThread");
			clientThread.start();
		}
	}

	private void serverImpl(final int port, SocketBean socketBean, Consumer<Socket> setter) {
		while (true) {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
				while (true) {
					Socket clientSocket = serverSocket.accept();
					// Turn on keep-alive for both the sockets 
					clientSocket.setKeepAlive(true);
					setter.accept(clientSocket);
					socketBean.notifyIt();
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (serverSocket != null) {
					try {
						serverSocket.close();
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}
			}
		}
	}
}
