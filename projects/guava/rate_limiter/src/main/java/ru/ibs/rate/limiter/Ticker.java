package ru.ibs.rate.limiter;

public abstract class Ticker {

	protected Ticker() {
	}

	public abstract long read();

	public static Ticker systemTicker() {
		return SYSTEM_TICKER;
	}

	private static final Ticker SYSTEM_TICKER = new Ticker() {
		@Override
		public long read() {
			return RateLimiterUtils.systemNanoTime();
		}
	};
}
