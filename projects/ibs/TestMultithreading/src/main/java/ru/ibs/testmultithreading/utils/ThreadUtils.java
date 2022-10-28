package ru.ibs.testmultithreading.utils;

public class ThreadUtils {

	public static Thread createThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
		return thread;
	}

	public static Thread createStoppedThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		return thread;
	}

	public static void waitSomeTime(int seconds) {
		while (true) {
			try {
				Thread.sleep(seconds * 1000);
				break;
			} catch (InterruptedException ex) {
				continue;
			}
		}
	}

	public static void join(Thread thread) {
		while (true) {
			try {
				thread.join();
				break;
			} catch (InterruptedException ex) {
				continue;
			}
		}
	}
}
