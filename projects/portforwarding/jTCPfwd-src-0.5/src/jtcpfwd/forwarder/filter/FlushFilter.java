package jtcpfwd.forwarder.filter;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FlushFilter extends Filter {

	public static final String SYNTAX = "Flush[,[<marker>[<escape>[<close>]]]]";

	public static final Class[] getRequiredClasses() {
		return new Class[] { FlushOutputStream.class, };
	}

	private byte marker, escape, close;

	public FlushFilter(String rule) throws Exception {
		if (rule == null || rule.length() == 0)
			rule = "#-.";
		else if (rule.length() == 1)
			rule += "-.";
		else if (rule.length() == 2)
			rule += ".";
		marker = (byte) rule.charAt(0);
		escape = (byte) rule.charAt(1);
		close = (byte) rule.charAt(2);
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		return new FlushOutputStream(marker, escape, close, sameDirection);
	}

	public static class FlushOutputStream extends FilterOutputStream {

		private final byte marker;
		private final byte escape;
		private final byte close;
		private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		private boolean markerSeen = false;

		FlushOutputStream(byte marker, byte escape, byte close, OutputStream out) {
			super(out);
			this.marker = marker;
			this.escape = escape;
			this.close = close;
		}

		public void write(int b) throws IOException {
			byte bb = (byte) b;
			if (markerSeen) {
				if (bb == marker) {
					out.write(buffer.toByteArray());
					buffer.reset();
				} else if (bb == escape) {
					buffer.write(marker);
				} else if (bb == close) {
					out.close();
				} else {
					buffer.write(marker);
					buffer.write(bb);
				}
				markerSeen = false;
			} else if (bb == marker) {
				markerSeen = true;
			} else {
				buffer.write(bb);
			}
		}
	}
}
