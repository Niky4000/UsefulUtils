package ru.kiokle.leetcode.concurrency;

import java.util.function.IntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FizzBuzzMultithreaded {

	public void startThreads() throws InterruptedException {
		FizzBuzz fizzBuzz = new FizzBuzz(15);
		Runnable first = () -> {
			try {
				fizzBuzz.fizz(() -> System.out.println("fizz"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable second = () -> {
			try {
				fizzBuzz.buzz(() -> System.out.println("buzz"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable third = () -> {
			try {
				fizzBuzz.fizzbuzz(() -> System.out.println("fizzbuzz"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable fourth = () -> {
			try {
				fizzBuzz.number(k -> System.out.println(k + ""));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Thread thread3 = new Thread(third, "third");
		thread3.start();
		Thread thread2 = new Thread(second, "second");
		thread2.start();
		Thread thread1 = new Thread(first, "first");
		thread1.start();
		Thread thread4 = new Thread(fourth, "fourth");
		thread4.start();
		thread1.join();
		thread2.join();
		thread3.join();
		thread4.join();
	}

	class FizzBuzz {

		private volatile int n;
		private volatile int i;

		public FizzBuzz(int n) {
			this.n = n;
			this.i = 1;
		}

		// printFizz.run() outputs "fizz".
		public void fizz(Runnable printFizz) throws InterruptedException {
			while (i <= n) {
				synchronized (this) {
					while (i <= n && !(i % 3 == 0 && i % 5 != 0)) {
						this.wait();
					}
					if (i > n) {
						break;
					}
					printFizz.run();
					i++;
					this.notifyAll();
				}
			}
		}

		// printBuzz.run() outputs "buzz".
		public void buzz(Runnable printBuzz) throws InterruptedException {
			while (i <= n) {
				synchronized (this) {
					while (i <= n && !(i % 3 != 0 && i % 5 == 0)) {
						this.wait();
					}
					if (i > n) {
						break;
					}
					printBuzz.run();
					i++;
					this.notifyAll();
				}
			}
		}

		// printFizzBuzz.run() outputs "fizzbuzz".
		public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
			while (i <= n) {
				synchronized (this) {
					while (i <= n && !(i % 3 == 0 && i % 5 == 0)) {
						this.wait();
					}
					if (i > n) {
						break;
					}
					printFizzBuzz.run();
					i++;
					this.notifyAll();
				}
			}
		}

		// printNumber.accept(x) outputs "x", where x is an integer.
		public void number(IntConsumer printNumber) throws InterruptedException {
			while (i <= n) {
				synchronized (this) {
					while (i <= n && (i % 3 == 0 || i % 5 == 0)) {
						this.wait();
					}
					if (i > n) {
						break;
					}
					printNumber.accept(i);
					i++;
					this.notifyAll();
				}
			}
		}
	}
}
