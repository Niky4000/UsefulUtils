package com.some.tcp;

import static com.some.tcp.TCPForwardServerR.WORKING_PORT;
import java.io.*;
import java.net.*;

public class TCPForwardServerR {

    public static final int WORKING_PORT = 22777;

    public static void main(String[] args) throws IOException {
        new TCPForwardServerR().init(22888, "192.168.192.215", 22);
    }

    public void init(int sourcePort, String destinationHost, int destinationPort) throws IOException {
        ServerSocket serverSocket = new ServerSocket(WORKING_PORT);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ServerSocket serverSocket2 = new ServerSocket(sourcePort);
            Socket mServerSocket = serverSocket2.accept();
            ClientThread clientThread = new ClientThread(clientSocket, mServerSocket);
            clientThread.start();
        }
    }
}
