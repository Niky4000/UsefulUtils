package jtcpfwd.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class FileSender extends Thread {
	private final File basedir, tmpfile;
	private final char marker;
	private final Socket socket;
	private final InputStream in;
	int sequenceNumber = 0;

	public FileSender(File basedir, char marker, Socket socket, InputStream in) {
		super("FileSender");
		this.basedir = basedir;
		this.marker = marker;
		this.socket = socket;
		this.in = in;
		this.tmpfile = new File(basedir, marker + "tmp");
	}

	public void run() {
		try {
			try {
				RandomAccessFile raf = new RandomAccessFile(tmpfile, "rw");
				raf.writeInt(-2);
				byte[] buf = new byte[4096];
				int len;
				while ((len = in.read(buf)) != -1) {
					raf.write(buf, 0, len);
					if (in.available() == 0) {
						Thread.sleep(10);
						if (in.available() == 0)
							raf = rotateFile(raf);
					}
				}
				if (raf.length() > 4)
					raf = rotateFile(raf);
				raf = rotateFile(raf);
				raf.close();
				if (!tmpfile.delete())
					throw new IOException("Unable to delete temp file");
			} finally {
				socket.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private RandomAccessFile rotateFile(RandomAccessFile raf) throws IOException {
		raf.seek(0);
		raf.writeInt((int) raf.length());
		raf.close();
		if (!tmpfile.renameTo(new File(basedir, marker + "" + sequenceNumber)))
			throw new IOException("Unable to rename file");
		sequenceNumber++;
		raf = new RandomAccessFile(tmpfile, "rw");
		raf.writeInt(0);
		return raf;
	}
}
