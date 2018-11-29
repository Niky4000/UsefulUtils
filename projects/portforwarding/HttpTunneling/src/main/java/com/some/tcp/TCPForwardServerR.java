package com.some.tcp;

import java.io.*;
import java.net.*;

public class TCPForwardServerR {

    public static void main(String[] args) throws IOException {
        new TCPForwardServerR().init(22777, 22888);
    }

    public void init(int sourcePort, int destinationPort) throws IOException {
        ServerSocket serverSocket = new ServerSocket(destinationPort);
        Socket clientSocket = serverSocket.accept();
        ServerSocket serverSocket2 = new ServerSocket(sourcePort);
        Socket mServerSocket = serverSocket2.accept();
        ClientThread clientThread = new ClientThread(clientSocket, mServerSocket);
        clientThread.start();
    }
}
