package ru.ibs.rate.limiter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author me
 */
public class RateLimiterStart {

	public static void main(String[] args) {
		RateLimiter rateLimiter = RateLimiter.create(4);
		for (int i = 0; i < 40; i++) {
			rateLimiter.acquire();
			System.out.println(i + " - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()));
		}
		rateLimiter.setRate(10);
		System.out.println("--------------------------");
		System.out.println("--------------------------");
		for (int i = 0; i < 40; i++) {
			rateLimiter.acquire();
			System.out.println(i + " - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()));
		}
	}
}
