/**
 * 
 */
package jtcpfwd.util;

import java.io.IOException;
import java.io.OutputStream;

public class SplitOutputStream extends OutputStream {

	private final OutputStream[] streams;

	public SplitOutputStream(OutputStream[] streams) {
		this.streams = streams;
	}

	public void write(int b) throws IOException {
		for (int i = 0; i < streams.length; i++) {
			streams[i].write(b);
		}
	}

	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < streams.length; i++) {
			streams[i].write(b, off, len);
		}
	}

	public void close() throws IOException {
		for (int i = 0; i < streams.length; i++) {
			streams[i].close();
		}
	}
}