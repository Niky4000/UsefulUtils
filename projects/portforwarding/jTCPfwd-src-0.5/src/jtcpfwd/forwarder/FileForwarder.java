package jtcpfwd.forwarder;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import jtcpfwd.util.FileReceiver;
import jtcpfwd.util.FileSender;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;

public class FileForwarder extends Forwarder {

	public static final String SYNTAX = "File@<directory>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				FileSender.class, FileReceiver.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class };
	}

	private final File directory;

	public FileForwarder(String rule) throws Exception {
		directory = new File(rule);
		if (!directory.isDirectory())
			throw new IOException("Not a directory: " + directory);
	}

	public Socket connect(Socket listener) {
		try {
			String name;
			do {
				name = "c" + (int) Math.floor(Math.random() * 1000000);
			} while (new File(directory, name).exists());
			File basedir = new File(directory, name);
			if (!basedir.mkdir())
				throw new IOException("Could not create directory " + name);
			PipedSocketImpl sock = new PipedSocketImpl();
			Socket socket = sock.createSocket();
			new FileSender(basedir, 'f', socket, sock.getLocalInputStream()).start();
			new FileReceiver(basedir, 'b', socket, sock.getLocalOutputStream()).start();
			return socket;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
	}
}
