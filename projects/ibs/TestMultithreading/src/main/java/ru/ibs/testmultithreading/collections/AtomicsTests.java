package ru.ibs.testmultithreading.collections;

import java.util.concurrent.atomic.AtomicReference;
import ru.ibs.testmultithreading.utils.ThreadUtils;

public class AtomicsTests {

	public void testAtomicReference() {
		AtomicReference<Integer> reference = new AtomicReference<>();
		Thread thread1 = ThreadUtils.createStoppedThread(() -> {
			Integer updateAndGet = reference.updateAndGet(d -> {
				System.out.println("Thread1: d: " + d);
				int value = d != null ? d * 2 : 8;
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread1: " + value);
				ThreadUtils.waitSomeTime(4);
				return value;
			});
		});
		Thread thread2 = ThreadUtils.createStoppedThread(() -> {
			Integer updateAndGet = reference.updateAndGet(d -> {
				System.out.println("Thread2: d: " + d);
				int value = d != null ? d * 4 : 4;
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread2: " + value);
				ThreadUtils.waitSomeTime(4);
				return value;
			});
		});
		Thread thread3 = ThreadUtils.createStoppedThread(() -> {
			Integer updateAndGet = reference.updateAndGet(d -> {
				System.out.println("Thread3: d: " + d);
				int value = d != null ? d * 10 : 2;
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread3: " + value);
				ThreadUtils.waitSomeTime(4);
				return value;
			});
		});
		Thread thread4 = ThreadUtils.createStoppedThread(() -> {
			Integer updateAndGet = reference.updateAndGet(d -> {
				System.out.println("Thread4: d: " + d);
				int value = d != null ? d * 8 : 12;
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread4: " + value);
				ThreadUtils.waitSomeTime(4);
				return value;
			});
		});
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		ThreadUtils.join(thread1);
		ThreadUtils.join(thread2);
		ThreadUtils.join(thread3);
		ThreadUtils.join(thread4);
		System.out.println(reference.get());
	}

	public void testAtomicReference2() {
		AtomicReference<Integer> reference = new AtomicReference<>(4);
		Thread thread1 = ThreadUtils.createStoppedThread(() -> {
			Integer updateAndGet = reference.accumulateAndGet(8, (d1, d2) -> {
				System.out.println("Thread1: d1: " + d1 + " d2: " + d2);
				int value = d1 != null && d2 != null ? d1 + d2 * 2 : 8;
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread1: " + value);
				ThreadUtils.waitSomeTime(4);
				return value;
			});
		});
		Thread thread2 = ThreadUtils.createStoppedThread(() -> {
			Integer updateAndGet = reference.accumulateAndGet(8, (d1, d2) -> {
				System.out.println("Thread2: d1: " + d1 + " d2: " + d2);
				int value = d1 != null && d2 != null ? d1 + d2 * 4 : 4;
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread2: " + value);
				ThreadUtils.waitSomeTime(4);
				return value;
			});
		});
		Thread thread3 = ThreadUtils.createStoppedThread(() -> {
			Integer updateAndGet = reference.accumulateAndGet(8, (d1, d2) -> {
				System.out.println("Thread3: d1: " + d1 + " d2: " + d2);
				int value = d1 != null && d2 != null ? d1 + d2 * 10 : 2;
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread3: " + value);
				ThreadUtils.waitSomeTime(4);
				return value;
			});
		});
		Thread thread4 = ThreadUtils.createStoppedThread(() -> {
			Integer updateAndGet = reference.accumulateAndGet(8, (d1, d2) -> {
				System.out.println("Thread4: d1: " + d1 + " d2: " + d2);
				int value = d1 != null && d2 != null ? d1 + d2 * 8 : 12;
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread4: " + value);
				ThreadUtils.waitSomeTime(4);
				return value;
			});
		});
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		ThreadUtils.join(thread1);
		ThreadUtils.join(thread2);
		ThreadUtils.join(thread3);
		ThreadUtils.join(thread4);
		System.out.println(reference.get());
	}
}
