package jtcpfwd.forwarder.filter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ThroughputFilter extends Filter {

	public static final String SYNTAX = "Throughput,<bytes>,<msec>,<burstbytes>";

	public static final Class[] getRequiredClasses() {
		return new Class[] { LimiterOutputStream.class };
	}
	
	private final int bytes, msec, burstbytes;

	public ThroughputFilter(String rule) throws Exception {
		String[] fields = rule.split(",");
		if (fields.length != 3)
			throw new RuntimeException("Unsupported argument count: " + rule);
		bytes = Integer.parseInt(fields[0]);
		msec = Integer.parseInt(fields[1]);
		burstbytes = Integer.parseInt(fields[2]);
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		return new LimiterOutputStream(sameDirection);
	}

	private class LimiterOutputStream extends FilterOutputStream {

		long timestamp = System.currentTimeMillis();
		int bucket = 0;

		public LimiterOutputStream(OutputStream out) {
			super(out);
		}

		public void write(int b) throws IOException {
			removeFromBucket(1);
			out.write(b);
		}

		public void write(byte[] b, int off, int len) throws IOException {
			while (len > 0) {
				int cnt = removeFromBucket(len);
				out.write(b, off, cnt);
				off += cnt;
				len -= cnt;
			}
		}

		/**
		 * Remove up to a given number of tokens from the bucket. Block until at
		 * least one token can be removed.
		 */
		private synchronized int removeFromBucket(int count) {
			while (true) {
				long now = System.currentTimeMillis();
				if (timestamp + msec < now) {
					long howoften = (now - timestamp) / msec;
					timestamp += howoften * msec;
					bucket += (int) Math.min(burstbytes - bucket, howoften * bytes);
				}
				if (bucket > 0)
					break;
				try {
					Thread.sleep(msec / 2);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			int result = Math.min(count, bucket);
			bucket -= result;
			return result;
		}
	}
}