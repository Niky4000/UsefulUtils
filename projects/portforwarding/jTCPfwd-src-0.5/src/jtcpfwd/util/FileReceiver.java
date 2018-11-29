package jtcpfwd.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class FileReceiver extends Thread {

	private final File basedir;
	private final char marker;
	private final Socket socket;
	int sequenceNumber = 0;
	private final OutputStream out;

	public FileReceiver(File basedir, char marker, Socket socket, OutputStream out) {
		super("FileReceiver");
		this.basedir = basedir;
		this.marker = marker;
		this.socket = socket;
		this.out = out;
	}

	public void run() {
		try {
			try {
				while (true) {
					byte[] data = null;
					File next = new File(basedir, marker + "" + sequenceNumber);
					try {
						if (!next.exists())
							continue;
						int filesize = (int) next.length();
						DataInputStream fis = new DataInputStream(new FileInputStream(next));
						try {
							int size = fis.readInt();
							if (size != filesize)
								continue;
							data = new byte[size - 4];
							fis.readFully(data);
						} finally {
							fis.close();
						}
					} catch (FileNotFoundException ex) {
						// sharing violation
						data = null;
						Thread.sleep(100);
					} catch (IOException ex) {
						ex.printStackTrace();
						data = null;
						Thread.sleep(5000);
					}
					if (data != null) {
						next.delete();
						sequenceNumber++;
						if (data.length == 0) {
							out.close();
							break;
						} else {
							out.write(data, 0, data.length);
						}
					}
				}
			} finally {
				socket.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
