package ru.kiokle.leetcode.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuildingH2O {

	public void startThreads() throws InterruptedException {
		H2O h2O = new H2O();
		Runnable first = () -> {
			try {
				h2O.hydrogen(() -> System.out.println(Thread.currentThread().getName() + " H"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		Runnable second = () -> {
			try {
				h2O.oxygen(() -> System.out.println(Thread.currentThread().getName() + " O"));
			} catch (InterruptedException ex) {
				Logger.getLogger(PrintInOrder.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		};
		int count = 50;
		List<Thread> threadList = new ArrayList<>(count * 3);
		for (int i = 0; i < count; i++) {
			Thread thread2 = new Thread(second, "oxygen-" + i);
			thread2.start();
			threadList.add(thread2);
		}
		for (int i = 0; i < count * 2; i++) {
			Thread thread1 = new Thread(first, "hydrogen-" + i);
			thread1.start();
			threadList.add(thread1);
		}
		for (Thread thread : threadList) {
			thread.join();
		}
	}

	class H2O {

		public H2O() {
		}

		int hydrogen = 0;
		int oxygen = 0;

		public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
			synchronized (this) {
				while (hydrogen >= 2) {
					this.wait();
				}
				// releaseHydrogen.run() outputs "H". Do not change or remove this line.
				releaseHydrogen.run();
				hydrogen++;
				skipAmountOfMoleculas();
				this.notifyAll();
			}
		}

		void skipAmountOfMoleculas() {
			if (hydrogen == 2 && oxygen == 1) {
				hydrogen = 0;
				oxygen = 0;
			}
		}

		public void oxygen(Runnable releaseOxygen) throws InterruptedException {
			synchronized (this) {
				while (oxygen > 0 && hydrogen < 2) {
					this.wait();
				}
				// releaseOxygen.run() outputs "O". Do not change or remove this line.
				releaseOxygen.run();
				oxygen++;
				skipAmountOfMoleculas();
				this.notifyAll();
			}
		}
	}
}
