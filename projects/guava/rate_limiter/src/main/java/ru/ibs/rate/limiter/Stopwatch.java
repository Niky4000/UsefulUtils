package ru.ibs.rate.limiter;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("GoodTime")
public final class Stopwatch {

	private final Ticker ticker;
	private boolean isRunning;
	private long elapsedNanos;
	private long startTick;

	public static Stopwatch createUnstarted() {
		return new Stopwatch();
	}

	public static Stopwatch createUnstarted(Ticker ticker) {
		return new Stopwatch(ticker);
	}

	public static Stopwatch createStarted() {
		return new Stopwatch().start();
	}

	public static Stopwatch createStarted(Ticker ticker) {
		return new Stopwatch(ticker).start();
	}

	Stopwatch() {
		this.ticker = Ticker.systemTicker();
	}

	Stopwatch(Ticker ticker) {
		this.ticker = RateLimiterUtils.checkNotNull(ticker, "ticker");
	}

	public boolean isRunning() {
		return isRunning;
	}

	public Stopwatch start() {
		RateLimiterUtils.checkState(!isRunning, "This stopwatch is already running.");
		isRunning = true;
		startTick = ticker.read();
		return this;
	}

	public Stopwatch stop() {
		long tick = ticker.read();
		RateLimiterUtils.checkState(isRunning, "This stopwatch is already stopped.");
		isRunning = false;
		elapsedNanos += tick - startTick;
		return this;
	}

	public Stopwatch reset() {
		elapsedNanos = 0;
		isRunning = false;
		return this;
	}

	private long elapsedNanos() {
		return isRunning ? ticker.read() - startTick + elapsedNanos : elapsedNanos;
	}

	public long elapsed(TimeUnit desiredUnit) {
		return desiredUnit.convert(elapsedNanos(), NANOSECONDS);
	}

	public Duration elapsed() {
		return Duration.ofNanos(elapsedNanos());
	}

	@Override
	public String toString() {
		long nanos = elapsedNanos();
		TimeUnit unit = chooseUnit(nanos);
		double value = (double) nanos / NANOSECONDS.convert(1, unit);
		return RateLimiterUtils.formatCompact4Digits(value) + " " + abbreviate(unit);
	}

	private static TimeUnit chooseUnit(long nanos) {
		if (DAYS.convert(nanos, NANOSECONDS) > 0) {
			return DAYS;
		}
		if (HOURS.convert(nanos, NANOSECONDS) > 0) {
			return HOURS;
		}
		if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
			return MINUTES;
		}
		if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
			return SECONDS;
		}
		if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
			return MILLISECONDS;
		}
		if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
			return MICROSECONDS;
		}
		return NANOSECONDS;
	}

	private static String abbreviate(TimeUnit unit) {
		switch (unit) {
			case NANOSECONDS:
				return "ns";
			case MICROSECONDS:
				return "\u03bcs";
			case MILLISECONDS:
				return "ms";
			case SECONDS:
				return "s";
			case MINUTES:
				return "min";
			case HOURS:
				return "h";
			case DAYS:
				return "d";
			default:
				throw new AssertionError();
		}
	}
}
