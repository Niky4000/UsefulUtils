package jtcpfwd.forwarder.filter;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

import jtcpfwd.util.Hamming16Code;

public class AddCRCFilter extends Filter {

	public static final String SYNTAX = "AddCRC";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Hamming16Code.class,
				CRCOutputStream.class };
	}

	public AddCRCFilter(String rule) throws Exception {
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		return new BufferedOutputStream(new CRCOutputStream(new BufferedOutputStream(sameDirection)), 2048);
	}

	public static class CRCOutputStream extends OutputStream {

		private final DataOutputStream out;

		CRCOutputStream(OutputStream out) {
			this.out = new DataOutputStream(out);
		}

		public void write(int b) throws IOException {
			// should not be called anyway
			write(new byte[] { (byte) b });
		}

		public void write(byte[] b, int off, int len) throws IOException {
			while (len > 2048) {
				write(b, off, 2048);
				off += 2048;
				len -= 2048;
			}
			if (len == 0)
				return;
			out.writeShort(Hamming16Code.generate(len - 1));
			out.write(b, off, len);
			CRC32 crc = new CRC32();
			crc.update(b, off, len);
			out.writeInt((int) crc.getValue());
		}

		public void flush() throws IOException {
			out.flush();
		}

		public void close() throws IOException {
			out.close();
		}
	}
}
