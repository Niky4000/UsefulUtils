package ru.ibs.rate.limiter;

import java.time.Duration;

final class Internal {

	static long toNanosSaturated(Duration duration) {
		try {
			return duration.toNanos();
		} catch (ArithmeticException tooBig) {
			return duration.isNegative() ? Long.MIN_VALUE : Long.MAX_VALUE;
		}
	}

	private Internal() {
	}
}
