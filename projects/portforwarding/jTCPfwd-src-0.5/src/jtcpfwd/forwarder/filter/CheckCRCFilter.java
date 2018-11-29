package jtcpfwd.forwarder.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

import jtcpfwd.util.Hamming16Code;

public class CheckCRCFilter extends Filter {

	public static final String SYNTAX = "CheckCRC";

	public CheckCRCFilter(String rule) throws Exception {
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		return new CRCOutputStream(sameDirection);
	}

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Hamming16Code.class,
				CRCOutputStream.class };
	}

	private static class CRCOutputStream extends OutputStream {

		private final OutputStream out;
		private int state = STATE_READLEN_1;
		private long buffer = 0;
		private byte[] data = null;

		private static final int
				STATE_READLEN_1 = -1,
				STATE_READLEN_2 = -2,
				STATE_READCRC_1 = -3,
				STATE_READCRC_2 = -4,
				STATE_READCRC_3 = -5,
				STATE_READCRC_4 = -6,
				STATE_INVALID = -7;

		public CRCOutputStream(OutputStream out) {
			this.out = out;
		}

		public void write(int b) throws IOException {
			b &= 0xff;
			if (state >= 0) {
				data[state] = (byte) b;
				state++;
				if (state == data.length)
					state = STATE_READCRC_1;
			} else {
				switch (state) {
				case STATE_INVALID:
					break;
				case STATE_READLEN_1:
					buffer = b;
					state = STATE_READLEN_2;
					break;
				case STATE_READLEN_2:
					buffer = (buffer << 8) + b;
					if (Hamming16Code.isValid((int) buffer)) {
						data = new byte[((int) buffer & 0x7ff) + 1];
						buffer = 0;
						state = 0;
					} else {
						out.close();
						state = STATE_INVALID;
					}
					break;
				case STATE_READCRC_1:
				case STATE_READCRC_2:
				case STATE_READCRC_3:
				case STATE_READCRC_4:
					buffer = (buffer << 8) + b;
					if (state == STATE_READCRC_4) {
						CRC32 crc = new CRC32();
						crc.update(data);
						if (crc.getValue() == buffer) {
							out.write(data);
							state = STATE_READLEN_1;
						} else {
							out.close();
							state = STATE_INVALID;
						}
					} else {
						state--;
					}
					break;
				}
			}
		}

		public void flush() throws IOException {
			out.flush();
		}

		public void close() throws IOException {
			out.close();
		}
	}
}
