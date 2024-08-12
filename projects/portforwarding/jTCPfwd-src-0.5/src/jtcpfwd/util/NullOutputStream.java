package jtcpfwd.util;

import java.io.IOException;
import java.io.OutputStream;

public final class NullOutputStream extends OutputStream {
	public void write(byte[] b, int off, int len) throws IOException {
	}

	public void write(int b) throws IOException {
	}

	public void write(byte[] b) throws IOException {
	}
}