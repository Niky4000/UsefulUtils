package jtcpfwd.util;

import java.util.Random;

/**
 * Random generator that generates random booleans with a given probability for
 * <code>true</code> but at the same time limiting the amount of
 * <code>true</code> values in a burst of given length.
 */
public class BurstLimitedRandom {

	private final Random random;

	private final int numerator;
	private final int denominator;
	private final int min;
	private final int max;
	private final boolean[] burst;
	private int burstPos, burstUndefined;

	/**
	 * Create a new burst limited random generator.
	 * 
	 * @param random
	 *            The {@link Random} to use as source
	 * @param numerator
	 *            numerator of the probability for <code>true</code>
	 * @param denonminator
	 *            denominator of the probability for <code>true</code>
	 * @param min
	 *            minimum number of <code>true</code> in the burst
	 * @param max
	 *            maximum number of <code>true</code> in the burst
	 * @param burstLength
	 *            length of the burst
	 */
	public BurstLimitedRandom(Random random, int numerator, int denominator, int min, int max, int burstLength) {
		if (denominator <= 0)
			throw new IllegalArgumentException("denominator must be greater than 0");
		if (numerator < 0 || numerator > denominator)
			throw new IllegalArgumentException("numerator must be between 0 and denominator");
		if (burstLength <= 0)
			throw new IllegalArgumentException("burst length must be greater than 0");
		if (min < 0 || min > burstLength)
			throw new IllegalArgumentException("min must be between 0 and burst length");
		if (max < min || max > burstLength)
			throw new IllegalArgumentException("max must be between min and burst length");
		this.random = random;
		this.numerator = numerator;
		this.denominator = denominator;
		this.min = min;
		this.max = max;
		burst = new boolean[burstLength];
		burstPos = 0;
		burstUndefined = burstLength;
	}

	/**
	 * Return the next <code>boolean</code> value.
	 */
	public boolean nextBoolean() {
		boolean value = random.nextInt(denominator) < numerator;
		burst[burstPos] = value;
		if (burstUndefined > 0)
			burstUndefined--;
		int trueCount = 0;
		for (int i = 0; i < burst.length; i++) {
			if (burst[i])
				trueCount++;
		}
		if (trueCount > max) {
			if (!value)
				throw new RuntimeException("trueCount too large even though current value is false");
			value = false;
		}
		if (trueCount + burstUndefined < min) {
			if (value)
				throw new RuntimeException("trueCount too small even though current value is true");
			value = true;
		}
		burst[burstPos] = value;
		burstPos = (burstPos + 1) % burst.length;
		return value;
	}

	public static BurstLimitedRandom parse(Random random, String parameters) {
		String[] args = parameters.split(",");
		int numerator = 1, denominator, min = 0, max = 1, burstLength = 1;
		if (args.length == 1) {
			denominator = Integer.parseInt(args[0]);
		} else if (args.length == 2) {
			numerator = Integer.parseInt(args[0]);
			denominator = Integer.parseInt(args[1]);
		} else if (args.length == 3) {
			numerator = Integer.parseInt(args[0]);
			denominator = Integer.parseInt(args[1]);
			burstLength = Integer.parseInt(args[2]);
		} else if (args.length == 4) {
			numerator = Integer.parseInt(args[0]);
			denominator = Integer.parseInt(args[1]);
			max = Integer.parseInt(args[2]);
			burstLength = Integer.parseInt(args[3]);
		} else if (args.length == 5) {
			numerator = Integer.parseInt(args[0]);
			denominator = Integer.parseInt(args[1]);
			min = Integer.parseInt(args[2]);
			max = Integer.parseInt(args[3]);
			burstLength = Integer.parseInt(args[4]);
		} else {
			throw new IllegalArgumentException("Unsupported format: " + parameters);
		}
		return new BurstLimitedRandom(random, numerator, denominator, min, max, burstLength);
	}
}
