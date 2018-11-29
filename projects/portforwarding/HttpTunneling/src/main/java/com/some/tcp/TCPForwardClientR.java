package com.some.tcp;

import java.io.IOException;
import java.net.Socket;

/**
 * @author NAnishhenko
 */
public class TCPForwardClientR {

//    public static void main(String[] args) throws IOException {
//        new TCPForwardClientR().init("192.168.192.216", 22888, "172.29.4.26", 22);
//    }
    public void init(String sourceHost, int sourcePort, String destinationHost, int destinationPort) throws IOException, InterruptedException {
//        while (true) {
        Socket clientSocket = new Socket(sourceHost, sourcePort);
        Socket mServerSocket = new Socket(destinationHost, destinationPort);
        ClientThread clientThread = new ClientThread(clientSocket, mServerSocket);
        clientThread.start();
        clientThread.join();
//        }
    }
}
