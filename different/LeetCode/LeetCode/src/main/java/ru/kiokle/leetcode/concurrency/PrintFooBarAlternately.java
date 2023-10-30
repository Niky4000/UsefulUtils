package ru.kiokle.leetcode.concurrency;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintFooBarAlternately {

	public void startThreads() throws InterruptedException {
		FooBar fooBar = new FooBar(50);
		Runnable first = () -> {
			try {
				fooBar.foo(() -> System.out.println("foo"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable second = () -> {
			try {
				fooBar.bar(() -> System.out.println("bar"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Thread thread2 = new Thread(second, "second");
		thread2.start();
		Thread thread1 = new Thread(first, "first");
		thread1.start();
		thread1.join();
		thread2.join();
	}

	class FooBar {

		private int n;
		private int foo;
		private int bar;

		public FooBar(int n) {
			this.n = n;
			foo = 0;
			bar = 0;
		}

		public void foo(Runnable printFoo) throws InterruptedException {
			for (int i = 0; i < n; i++) {
				synchronized (this) {
					while (foo - bar != 0) {
						this.wait();
					}
					// printFoo.run() outputs "foo". Do not change or remove this line.
					printFoo.run();
					foo++;
					this.notifyAll();
				}
			}
		}

		public void bar(Runnable printBar) throws InterruptedException {
			for (int i = 0; i < n; i++) {
				synchronized (this) {
					while (foo - bar != 1) {
						this.wait();
					}
					// printBar.run() outputs "bar". Do not change or remove this line.
					printBar.run();
					bar++;
					this.notifyAll();
				}
			}
		}
	}
}
