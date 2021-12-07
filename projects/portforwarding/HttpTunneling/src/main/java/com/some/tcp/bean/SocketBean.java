/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.some.tcp.bean;

import java.net.Socket;

/**
 *
 * @author me
 */
public class SocketBean {

	private volatile Socket clientSocket2;
	private volatile Socket serverSocket2;

	public Socket getClientSocket2() {
		Socket socket = clientSocket2;
		clientSocket2 = null;
		return socket;
	}

	public void setClientSocket2(Socket clientSocket2) {
		this.clientSocket2 = clientSocket2;
	}

	public Socket getServerSocket2() {
		Socket socket = serverSocket2;
		serverSocket2 = null;
		return socket;
	}

	public void setServerSocket2(Socket serverSocket2) {
		this.serverSocket2 = serverSocket2;
	}

	public synchronized void notifyIt() {
		notify();
	}

	public synchronized void waitForSocketsToBeReady() {
		while (!(clientSocket2 != null && !clientSocket2.isClosed() && serverSocket2 != null && !serverSocket2.isClosed())) {
			try {
				wait();
			} catch (InterruptedException ex) { // Eat that!
			}
		}
	}
}
