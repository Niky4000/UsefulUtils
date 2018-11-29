package com.some.tcp;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPForwardServerR {

//    public static void main(String[] args) throws IOException {
//        new TCPForwardServerR().init(22777, 22888);
//    }
    public void init(int sourcePort, int destinationPort) throws IOException, InterruptedException {
        final BlockingQueue<Socket> clientSocketQueue = new LinkedBlockingQueue<>();
        final BlockingQueue<Socket> serverSocketQueue = new LinkedBlockingQueue<>();
        Thread clientListernerThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(destinationPort);
                Socket clientSocket = serverSocket.accept();
                clientSocketQueue.offer(clientSocket);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Thread serverListernerThread = new Thread(() -> {
            try {
                ServerSocket serverSocket2 = new ServerSocket(sourcePort);
                Socket mServerSocket = serverSocket2.accept();
                serverSocketQueue.offer(mServerSocket);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        clientListernerThread.start();
        serverListernerThread.start();

        while (true) {
            Socket clientSocket = clientSocketQueue.take();
            Socket mServerSocket = serverSocketQueue.take();
            ClientThread clientThread = new ClientThread(clientSocket, mServerSocket);
            clientThread.start();
        }
    }
}
