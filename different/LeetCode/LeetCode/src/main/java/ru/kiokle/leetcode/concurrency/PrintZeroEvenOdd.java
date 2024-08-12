package ru.kiokle.leetcode.concurrency;

import java.util.function.IntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintZeroEvenOdd {

	public void startThreads() throws InterruptedException {
		ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(2);
		Runnable first = () -> {
			try {
				zeroEvenOdd.zero(k -> System.out.println(Thread.currentThread().getName() + " " + k));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable second = () -> {
			try {
				zeroEvenOdd.even(k -> System.out.println(Thread.currentThread().getName() + " " + k));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable third = () -> {
			try {
				zeroEvenOdd.odd(k -> System.out.println(Thread.currentThread().getName() + " " + k));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Thread thread3 = new Thread(third, "odd");
		thread3.start();
		Thread thread2 = new Thread(second, "even");
		thread2.start();
		Thread thread1 = new Thread(first, "zero");
		thread1.start();
		thread1.join();
		thread2.join();
		thread3.join();
	}

	class ZeroEvenOdd {

		private int n;
		private volatile int i;
		private boolean zeroWasPrinted;

		public ZeroEvenOdd(int n) {
			this.n = n;
			i = 1;
			zeroWasPrinted = false;
		}

		// printNumber.accept(x) outputs "x", where x is an integer.
		public void zero(IntConsumer printNumber) throws InterruptedException {
			while (i <= n) {
				synchronized (this) {
					while (i <= n && zeroWasPrinted) {
						this.wait();
					}
					if (i > n) {
						break;
					}
					printNumber.accept(0);
					zeroWasPrinted = true;
					this.notifyAll();
				}
			}
		}

		public void even(IntConsumer printNumber) throws InterruptedException {
			while (i <= n) {
				synchronized (this) {
					while (i <= n && (i == 0 || !zeroWasPrinted || i % 2 == 1)) {
						this.wait();
					}
					if (i > n) {
						break;
					}
					printNumber.accept(i);
					zeroWasPrinted = false;
					i++;
					this.notifyAll();
				}
			}
		}

		public void odd(IntConsumer printNumber) throws InterruptedException {
			while (i <= n) {
				synchronized (this) {
					while (i <= n && (i == 0 || !zeroWasPrinted || i % 2 == 0)) {
						this.wait();
					}
					if (i > n) {
						break;
					}
					printNumber.accept(i);
					zeroWasPrinted = false;
					i++;
					this.notifyAll();
				}
			}
		}
	}
}
