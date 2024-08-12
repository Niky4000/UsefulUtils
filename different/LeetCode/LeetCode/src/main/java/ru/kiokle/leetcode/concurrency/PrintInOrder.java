package ru.kiokle.leetcode.concurrency;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintInOrder {

	public void startThreads() throws InterruptedException {
		Foo foo = new Foo();
		Runnable first = () -> {
			try {
				foo.first(() -> System.out.println("first"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable second = () -> {
			try {
				foo.second(() -> System.out.println("second"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable third = () -> {
			try {
				foo.third(() -> System.out.println("third"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Thread thread3 = new Thread(third);
		thread3.start();
		Thread thread2 = new Thread(second);
		thread2.start();
		Thread thread1 = new Thread(first);
		thread1.start();
		thread1.join();
		thread2.join();
		thread3.join();
	}

	class Foo {

		public Foo() {
			arr = new int[]{3, 2, 1};
		}
		final int[] arr;

		public void first(Runnable printFirst) throws InterruptedException {
			synchronized (arr) {
				while (arr[0] > 1) {
					arr.wait();
				}
				// printFirst.run() outputs "first". Do not change or remove this line.
				printFirst.run();
				decrementAll();
				arr.notifyAll();
			}
		}

		public void second(Runnable printSecond) throws InterruptedException {
			synchronized (arr) {
				while (arr[1] > 1) {
					arr.wait();
				}
				// printSecond.run() outputs "second". Do not change or remove this line.
				printSecond.run();
				decrementAll();
				arr.notifyAll();
			}
		}

		public void third(Runnable printThird) throws InterruptedException {
			synchronized (arr) {
				while (arr[2] > 1) {
					arr.wait();
				}
				// printThird.run() outputs "third". Do not change or remove this line.
				printThird.run();
				decrementAll();
				arr.notifyAll();
			}
		}

		void decrementAll() {
			for (int i = 0; i < arr.length; i++) {
				arr[i]--;
			}
		}
	}
}
