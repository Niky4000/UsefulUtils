package com.some.tcp.original;

/**
 * This program is an example from the book "Internet programming with Java" by
 * Svetlin Nakov. It is freeware. For more information:
 * http://www.nakov.com/books/inetjava/
 */
import java.io.*;
import java.net.*;

/**
 * TCPForwardServer is a simple TCP bridging software that allows a TCP port on
 * some host to be transparently forwarded to some other TCP port on some other
 * host. TCPForwardServer continuously accepts client connections on the
 * listening TCP port (source port) and starts a thread (ClientThread) that
 * connects to the destination host and starts forwarding the data between the
 * client socket and destination socket.
 */
public class TCPForwardServerOriginal {

//    public static final int SOURCE_PORT = 22888;
//    public static final String DESTINATION_HOST = "mail.abv.bg";
//    public static final int DESTINATION_PORT = 25;
//    public static final String DESTINATION_HOST = "192.168.192.215";
//    public static final int DESTINATION_PORT = 22;
    public static void main(String[] args) throws IOException {
        new TCPForwardServerOriginal().init(22888, "192.168.192.215", 22);
    }

    public void init(int sourcePort, String destinationHost, int destinationPort) throws IOException {
        ServerSocket serverSocket = new ServerSocket(sourcePort);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientThread clientThread = new ClientThread(clientSocket, destinationHost, destinationPort);
            clientThread.start();
        }
    }
}

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
    private final String destinationHost;
    private final int destinationPort;

    public ClientThread(Socket aClientSocket, String destinationHost, int destinationPort) {
        mClientSocket = aClientSocket;
        this.destinationHost = destinationHost;
        this.destinationPort = destinationPort;
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
            // Connect to the destination server 
            mServerSocket = new Socket(destinationHost, destinationPort);

            // Turn on keep-alive for both the sockets 
            mServerSocket.setKeepAlive(true);
            mClientSocket.setKeepAlive(true);

            // Obtain client & server input & output streams 
            clientIn = mClientSocket.getInputStream();
            clientOut = mClientSocket.getOutputStream();
            serverIn = mServerSocket.getInputStream();
            serverOut = mServerSocket.getOutputStream();
        } catch (IOException ioe) {
            System.err.println("Can not connect to "
                    + destinationHost + ":"
                    + destinationPort);
            connectionBroken();
            return;
        }

        // Start forwarding data between server and client 
        mForwardingActive = true;
        ForwardThread clientForward = new ForwardThread(this, clientIn, serverOut);
        clientForward.start();
        ForwardThread serverForward = new ForwardThread(this, serverIn, clientOut);
        serverForward.start();

        System.out.println("TCP Forwarding "
                + mClientSocket.getInetAddress().getHostAddress()
                + ":" + mClientSocket.getPort() + " <--> "
                + mServerSocket.getInetAddress().getHostAddress()
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
                    + mClientSocket.getInetAddress().getHostAddress()
                    + ":" + mClientSocket.getPort() + " <--> "
                    + mServerSocket.getInetAddress().getHostAddress()
                    + ":" + mServerSocket.getPort() + " stopped.");
            mForwardingActive = false;
        }
    }
}

/**
 * ForwardThread handles the TCP forwarding between a socket input stream
 * (source) and a socket output stream (dest). It reads the input stream and
 * forwards everything to the output stream. If some of the streams fails, the
 * forwarding stops and the parent is notified to close all its sockets.
 */
class ForwardThread extends Thread {

    private static final int BUFFER_SIZE = 8192;

    InputStream mInputStream;
    OutputStream mOutputStream;
    ClientThread mParent;

    /**
     * Creates a new traffic redirection thread specifying its parent, input
     * stream and output stream.
     */
    public ForwardThread(ClientThread aParent, InputStream aInputStream, OutputStream aOutputStream) {
        mParent = aParent;
        mInputStream = aInputStream;
        mOutputStream = aOutputStream;
    }

    /**
     * Runs the thread. Continuously reads the input stream and writes the read
     * data to the output stream. If reading or writing fail, exits the thread
     * and notifies the parent about the failure.
     */
    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            while (true) {
                int bytesRead = mInputStream.read(buffer);
                if (bytesRead == -1) {
                    break; // End of stream is reached --> exit 
                }
                mOutputStream.write(buffer, 0, bytesRead);
                mOutputStream.flush();
            }
        } catch (IOException e) {
            // Read/write failed --> connection is broken 
        }

        // Notify parent thread that the connection is broken 
        mParent.connectionBroken();
    }
}
