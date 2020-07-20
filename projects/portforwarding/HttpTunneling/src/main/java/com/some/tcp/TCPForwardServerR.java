package com.some.tcp;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPForwardServerR {

    private static final int QUEUE_SIZE = 16;
    private static final int CRITICAL_QUEUE_SIZE = 2;

//    public static void main(String[] args) throws IOException {
//        new TCPForwardServerR().init(22777, 22888);
//    }
    public void init(int sourcePort, int destinationPort) throws IOException, InterruptedException {
        final BlockingQueue<Socket> clientSocketQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        final BlockingQueue<Socket> serverSocketQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        Thread clientListernerThread = new Thread(() -> {
            serverImpl("Client", destinationPort, clientSocketQueue, serverSocketQueue);
        });
        Thread serverListernerThread = new Thread(() -> {
            serverImpl("Server", sourcePort, serverSocketQueue, clientSocketQueue);
        });
        clientListernerThread.setName("clientListernerThread");
        serverListernerThread.setName("serverListernerThread");
        clientListernerThread.start();
        serverListernerThread.start();

        while (true) {
            Socket clientSocket = clientSocketQueue.take();
            Socket mServerSocket = serverSocketQueue.take();
            Socket clientSocket2 = ensureThatQueueIsEmpty(clientSocketQueue, clientSocket);
            Socket serverSocket2 = ensureThatQueueIsEmpty(serverSocketQueue, mServerSocket);
            ClientThread clientThread = new ClientThread(clientSocket2, serverSocket2);
            clientThread.start();
        }
    }

    private Socket ensureThatQueueIsEmpty(final Queue<Socket> socketQueue, Socket mainSocket) {
        Socket socket = null;
        Socket previousSocket = null;
        do {
            socket = socketQueue.poll();
            if (socket != null) {
                if (previousSocket != null) {
                    closeSocketSilently(previousSocket);
                }
                previousSocket = socket;
            } else {
                break;
            }
        } while (socket != null);
        if (previousSocket != null) {
            closeSocketSilently(mainSocket);
            return previousSocket;
        } else {
            return mainSocket;
        }
    }

    private void closeSocketSilently(Socket socket) {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serverImpl(String name, final int port, final Queue<Socket> socketQueue, final Queue<Socket> anotherQueue) {
        while (true) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    // Turn on keep-alive for both the sockets 
                    clientSocket.setKeepAlive(true);
                    socketQueue.offer(clientSocket);
                    if (socketQueue.size() >= CRITICAL_QUEUE_SIZE && anotherQueue.isEmpty()) {
                        Socket socket = ensureThatQueueIsEmpty(socketQueue, clientSocket);
                        closeSocketSilently(socket);
                        System.out.println(name + " was cleaned and socketQueue equals to " + socketQueue.size() + "!");
                    }
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
