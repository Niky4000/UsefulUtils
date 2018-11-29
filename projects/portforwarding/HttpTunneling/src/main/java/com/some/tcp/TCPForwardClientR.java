package com.some.tcp;

import static com.some.tcp.TCPForwardServerR.WORKING_PORT;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author NAnishhenko
 */
public class TCPForwardClientR {

    public static void main(String[] args) throws IOException {
        new TCPForwardServerR().init(22888, "192.168.192.215", 22);
    }

    public void init(int sourcePort, String destinationHost, int destinationPort) throws IOException {
        Socket clientSocket = new Socket(destinationHost, destinationPort);
        Socket mServerSocket = new Socket(destinationHost, destinationPort);
        ClientThread clientThread = new ClientThread(clientSocket, mServerSocket);
        clientThread.start();

    }
}
