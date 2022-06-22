package ru.ibs.rate.limiter;

import java.util.Locale;
import static ru.ibs.rate.limiter.Strings.lenientFormat;

/**
 *
 * @author me
 */
public class RateLimiterUtils {

	@SuppressWarnings("GoodTime")
	public static long systemNanoTime() {
		return System.nanoTime();
	}

	public static String formatCompact4Digits(double value) {
		return String.format(Locale.ROOT, "%.4g", value);
	}

	public static <T> T checkNotNull(T reference, Object errorMessage) {
		if (reference == null) {
			throw new NullPointerException(String.valueOf(errorMessage));
		}
		return reference;
	}

	public static void checkState(boolean expression, Object errorMessage) {
		if (!expression) {
			throw new IllegalStateException(String.valueOf(errorMessage));
		}
	}

	public static void checkArgument(boolean b, String errorMessageTemplate, long p1) {
		if (!b) {
			throw new IllegalArgumentException(lenientFormat(errorMessageTemplate, p1));
		}
	}

	public static void checkArgument(boolean expression, Object errorMessage) {
		if (!expression) {
			throw new IllegalArgumentException(String.valueOf(errorMessage));
		}
	}

	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}
}
