package jtcpfwd.forwarder.filter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import jtcpfwd.util.BurstLimitedRandom;

public class FlipBitFilter extends Filter {

	public static final String SYNTAX = "FlipBit,<numerator>,<denominator>[[[,<min=0>],<max=1>],<burstLength=1>]";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				BurstLimitedRandom.class,
				FlipBitOutputStream.class };
	}

	private static final Random rnd = new Random();
	private final String rule;

	public FlipBitFilter(String rule) throws Exception {
		this.rule = rule;
		// just verify the rule
		BurstLimitedRandom.parse(new Random(), rule);
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		final BurstLimitedRandom blr = BurstLimitedRandom.parse(rnd, rule);
		return new FlipBitOutputStream(sameDirection, blr);
	}

	public static final class FlipBitOutputStream extends FilterOutputStream {
		private final BurstLimitedRandom blr;

		FlipBitOutputStream(OutputStream out, BurstLimitedRandom blr) {
			super(out);
			this.blr = blr;
		}

		public void write(int b) throws IOException {
			b = flip((byte) b) & 0xff;
			out.write(b);
		}

		public void write(byte[] b, int off, int len) throws IOException {
			byte[] tmp = new byte[b.length];
			for (int i = off; i < len; i++) {
				tmp[i] = flip(b[i]);
			}
			out.write(tmp, off, len);
		}

		private byte flip(byte b) {
			for (int i = 0; i < 8; i++) {
				if (blr.nextBoolean()) {
					b ^= (1 << i);
				}
			}
			return b;
		}
	}
}