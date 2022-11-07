package ru.ibs.testmultithreading.collections;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import ru.ibs.testmultithreading.utils.ThreadUtils;
import static ru.ibs.testmultithreading.utils.ThreadUtils.waitSomeTime;

public class LocksTests {

	public void testClassicLock() {
		Object lock = new Object();
		AtomicInteger i = new AtomicInteger(0);
		Thread thread1 = ThreadUtils.createThread(() -> {
			synchronized (lock) {
				while (i.get() < 10) {
					try {
						System.out.println("Waiting for " + i.get() + " to be 10...");
						lock.wait();
					} catch (InterruptedException ex) {
						continue;
					}
				}
				System.out.println("Finished!");
			}
		});
		Thread thread2 = ThreadUtils.createThread(() -> {
			for (int j = 0; j < 12; j++) {
//				ThreadUtils.waitSomeTime(4);
//				thread1.interrupt();
				ThreadUtils.waitSomeTime(4);
				i.incrementAndGet();
				synchronized (lock) {
					lock.notify();
				}
			}
		});
	}

	public void testReentrantLock() {
		ReentrantLock lock = new ReentrantLock();
		Thread thread1 = ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 10; i++) {
				lock.lock();
				try {
					ThreadUtils.waitSomeTime(4);
					System.out.println("Thread1!");
				} finally {
					lock.unlock();
				}
			}
		});
		Thread thread2 = ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 10; i++) {
				lock.lock();
				try {
					ThreadUtils.waitSomeTime(4);
					System.out.println("Thread2!");
				} finally {
					lock.unlock();
				}
			}
		});
	}

	public void testReentrantLock2() {
		ReentrantLock lock = new ReentrantLock();
		AtomicInteger i = new AtomicInteger();
		Condition condition = lock.newCondition();
		Thread thread1 = ThreadUtils.createThread(() -> {
			lock.lock();
			try {
				while (i.get() < 10) {
					condition.awaitUninterruptibly();
					System.out.println("Waiting for " + i.get() + " to be 10...");
				}
			} finally {
				lock.unlock();
			}
			System.out.println("Thread1 is finished!");
		});
		Thread thread2 = ThreadUtils.createThread(() -> {
			for (int j = 1; j <= 10; j++) {
				ThreadUtils.waitSomeTime(4);
				i.incrementAndGet();
				System.out.println("Thread2...");
				lock.lock();
				try {
					condition.signal();
				} finally {
					lock.unlock();
				}
			}
		});
	}

	public void testReadWriteLock() {
		ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
		ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
		ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
		List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
		Thread thread1 = ThreadUtils.createThread(() -> {
			for (int j = 1; j <= 10; j++) {
				for (int i = 0; i < list.size(); i++) {
					readLock.lock();
					try {
						System.out.println("list[" + i + "]=" + list.get(i) + "!");
						ThreadUtils.waitSomeTime(1);
					} finally {
						readLock.unlock();
					}
				}
			}
		});
		Thread thread2 = ThreadUtils.createThread(() -> {
			for (int j = 1; j <= 25; j++) {
				writeLock.lock();
				try {
					ThreadUtils.waitSomeTime(4);
					int newValue = list.get(j % 10) * 10;
					list.set(j % 10, newValue);
					System.out.println("Thread2 newValue: " + newValue + "!");
				} finally {
					writeLock.unlock();
				}
				ThreadUtils.waitSomeTime(4);
			}
		});
	}

	Supplier<String> getTime = () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss_SSS").format(new Date());

	public void stopAnyNumberOfThreadsOnLock() {
		final int threadCount = 8;
		final AtomicBoolean unlock = new AtomicBoolean(false);
		class WorkerThread extends Thread {

			public WorkerThread(String string) {
				super(string);
			}

			@Override
			public void run() {
				System.out.println("Work started by " + Thread.currentThread().getName() + " " + getTime.get() + "!");
				synchronized (unlock) {
					do {
						try {
							unlock.wait();
						} catch (InterruptedException ex) {
							continue;
						}
					} while (!unlock.getAndSet(false));
					System.out.println("Work is done by " + Thread.currentThread().getName() + " " + getTime.get() + "!");
				}
			}
		}
		class ManagerThread extends Thread {

			public ManagerThread(String string) {
				super(string);
			}

			@Override
			public void run() {
				System.out.println("ManagerThread started! " + getTime.get() + "!");
				for (int i = 0; i < threadCount; i++) {
					waitSomeTime(4);
					unlock.set(true);
					synchronized (unlock) {
						unlock.notify();
					}
					System.out.println("---");
				}
				System.out.println("ManagerThread finished! " + getTime.get() + "!");
			}
		}
		for (int i = 0; i < threadCount; i++) {
			new WorkerThread("Worker" + (i + 1)).start();
		}
		new ManagerThread("ManagerThread").start();
	}

//	public void testStampedLock() {
//		StampedLock lock = new StampedLock();
//		Date date = new Date();
//		Thread thread1 = ThreadUtils.createThread(() -> {
//			for (int j = 1; j <= 100; j++) {
//				long stamp = lock.tryOptimisticRead();
//				Integer seconds = Long.valueOf((date.getTime() - new Date().getTime()) / 1000).intValue();
//				ThreadUtils.waitSomeTime(1);
//				if (!lock.validate(stamp)) {
//					stamp = lock.readLock();
//					try {
//						get = list.get(i);
//					} finally {
//						lock.unlock(readStamp);
//					}
//				}
//				System.out.println("list[" + i + "]=" + get + "!");
//			}
//		});
//		Thread thread2 = ThreadUtils.createThread(() -> {
//			for (int j = 1; j <= 25; j++) {
//				long readLock = lock.readLock();
//				try {
//					ThreadUtils.waitSomeTime(4);
//					int newValue = list.get(j % 10) * 10;
//					list.set(j % 10, newValue);
//					System.out.println("Thread2 newValue: " + newValue + "!");
//				} finally {
//					lock.unlock(readLock);
//				}
//				ThreadUtils.waitSomeTime(4);
//			}
//		});
//	}
}
