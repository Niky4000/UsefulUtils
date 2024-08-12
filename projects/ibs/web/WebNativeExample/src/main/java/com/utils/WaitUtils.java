package com.utils;

public class WaitUtils {

	public static void waitSomeTime(int timeToWait) {
		while (true) {
			try {
				Thread.sleep(timeToWait * 1000);
				break;
			} catch (InterruptedException ex) {
				continue;
			}
		}
	}
}
