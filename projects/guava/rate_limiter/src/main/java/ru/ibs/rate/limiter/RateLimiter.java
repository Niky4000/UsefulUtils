package ru.ibs.rate.limiter;

import static ru.ibs.rate.limiter.Internal.toNanosSaturated;
import static java.lang.Math.max;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import ru.ibs.rate.limiter.SmoothRateLimiter.SmoothBursty;
import ru.ibs.rate.limiter.SmoothRateLimiter.SmoothWarmingUp;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class RateLimiter {

	public static RateLimiter create(double permitsPerSecond) {
		return create(permitsPerSecond, SleepingStopwatch.createFromSystemTimer());
	}

	static RateLimiter create(double permitsPerSecond, SleepingStopwatch stopwatch) {
		RateLimiter rateLimiter = new SmoothBursty(stopwatch, 1.0);
		rateLimiter.setRate(permitsPerSecond);
		return rateLimiter;
	}

	public static RateLimiter create(double permitsPerSecond, Duration warmupPeriod) {
		return create(permitsPerSecond, toNanosSaturated(warmupPeriod), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
		RateLimiterUtils.checkArgument(warmupPeriod >= 0, "warmupPeriod must not be negative: %s", warmupPeriod);
		return create(permitsPerSecond, warmupPeriod, unit, 3.0, SleepingStopwatch.createFromSystemTimer());
	}

	static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit, double coldFactor, SleepingStopwatch stopwatch) {
		RateLimiter rateLimiter = new SmoothWarmingUp(stopwatch, warmupPeriod, unit, coldFactor);
		rateLimiter.setRate(permitsPerSecond);
		return rateLimiter;
	}

	private final SleepingStopwatch stopwatch;

	private volatile Object mutexDoNotUseDirectly;

	private Object mutex() {
		Object mutex = mutexDoNotUseDirectly;
		if (mutex == null) {
			synchronized (this) {
				mutex = mutexDoNotUseDirectly;
				if (mutex == null) {
					mutexDoNotUseDirectly = mutex = new Object();
				}
			}
		}
		return mutex;
	}

	RateLimiter(SleepingStopwatch stopwatch) {
		this.stopwatch = RateLimiterUtils.checkNotNull(stopwatch);
	}

	public final void setRate(double permitsPerSecond) {
		RateLimiterUtils.checkArgument(permitsPerSecond > 0.0 && !Double.isNaN(permitsPerSecond), "rate must be positive");
		synchronized (mutex()) {
			doSetRate(permitsPerSecond, stopwatch.readMicros());
		}
	}

	abstract void doSetRate(double permitsPerSecond, long nowMicros);

	public final double getRate() {
		synchronized (mutex()) {
			return doGetRate();
		}
	}

	abstract double doGetRate();

	public double acquire() {
		return acquire(1);
	}

	public double acquire(int permits) {
		long microsToWait = reserve(permits);
		stopwatch.sleepMicrosUninterruptibly(microsToWait);
		return 1.0 * microsToWait / SECONDS.toMicros(1L);
	}

	final long reserve(int permits) {
		checkPermits(permits);
		synchronized (mutex()) {
			return reserveAndGetWaitLength(permits, stopwatch.readMicros());
		}
	}

	public boolean tryAcquire(Duration timeout) {
		return tryAcquire(1, toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public boolean tryAcquire(long timeout, TimeUnit unit) {
		return tryAcquire(1, timeout, unit);
	}

	public boolean tryAcquire(int permits) {
		return tryAcquire(permits, 0, MICROSECONDS);
	}

	public boolean tryAcquire() {
		return tryAcquire(1, 0, MICROSECONDS);
	}

	public boolean tryAcquire(int permits, Duration timeout) {
		return tryAcquire(permits, toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
		long timeoutMicros = max(unit.toMicros(timeout), 0);
		checkPermits(permits);
		long microsToWait;
		synchronized (mutex()) {
			long nowMicros = stopwatch.readMicros();
			if (!canAcquire(nowMicros, timeoutMicros)) {
				return false;
			} else {
				microsToWait = reserveAndGetWaitLength(permits, nowMicros);
			}
		}
		stopwatch.sleepMicrosUninterruptibly(microsToWait);
		return true;
	}

	private boolean canAcquire(long nowMicros, long timeoutMicros) {
		return queryEarliestAvailable(nowMicros) - timeoutMicros <= nowMicros;
	}

	final long reserveAndGetWaitLength(int permits, long nowMicros) {
		long momentAvailable = reserveEarliestAvailable(permits, nowMicros);
		return max(momentAvailable - nowMicros, 0);
	}

	abstract long queryEarliestAvailable(long nowMicros);

	abstract long reserveEarliestAvailable(int permits, long nowMicros);

	@Override
	public String toString() {
		return String.format(Locale.ROOT, "RateLimiter[stableRate=%3.1fqps]", getRate());
	}

	abstract static class SleepingStopwatch {

		protected SleepingStopwatch() {
		}

		protected abstract long readMicros();

		protected abstract void sleepMicrosUninterruptibly(long micros);

		public static SleepingStopwatch createFromSystemTimer() {
			return new SleepingStopwatch() {
				final Stopwatch stopwatch = Stopwatch.createStarted();

				@Override
				protected long readMicros() {
					return stopwatch.elapsed(MICROSECONDS);
				}

				@Override
				protected void sleepMicrosUninterruptibly(long micros) {
					if (micros > 0) {
						Uninterruptibles.sleepUninterruptibly(micros, MICROSECONDS);
					}
				}
			};
		}
	}

	private static void checkPermits(int permits) {
		RateLimiterUtils.checkArgument(permits > 0, "Requested permits (%s) must be positive", permits);
	}
}
