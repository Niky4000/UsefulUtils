package ru.kiokle.leetcode.concurrency;

import java.util.concurrent.Semaphore;

public class TheDiningPhilosophers {

	public void startThreads() throws InterruptedException {
		Runnable[] fork1 = createFork();
		Runnable[] fork2 = createFork();
		Runnable[] fork3 = createFork();
		createPhilosopher(1, fork1, fork2);
		createPhilosopher(2, fork2, fork3);
		createPhilosopher(3, fork3, fork1);
	}

	private Philosopher createPhilosopher(int id, Runnable[] leftFork, Runnable[] rightFork) {
		return new Philosopher(id, leftFork[0], rightFork[0], eat(), leftFork[1], rightFork[1]);
	}

	private Runnable[] createFork() {
		Semaphore semaphore = new Semaphore(1);
		return new Runnable[]{() -> {
			semaphore.acquireUninterruptibly();
		}, () -> {
			semaphore.release();
		}};
	}

	private Runnable eat() {
		return () -> {
			while (true) {
				try {
					System.out.println("Eating started!");
					Thread.sleep(4000);
					System.out.println("Eating finished!");
					break;
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					continue;
				}
			}
		};
	}

	class DiningPhilosophers {

		public DiningPhilosophers() {
		}

		private Object leftFork = new Object();
		private Object rightFork = new Object();

		// call the run() method of any runnable to execute its code
		public void wantsToEat(int philosopher,
				Runnable pickLeftFork,
				Runnable pickRightFork,
				Runnable eat,
				Runnable putLeftFork,
				Runnable putRightFork) throws InterruptedException {
			if (philosopher % 2 == 1) {
				synchronized (leftFork) {
					synchronized (rightFork) {
						pickLeftFork.run();
						pickRightFork.run();
						eat.run();
						putRightFork.run();
						putLeftFork.run();
					}
				}
			} else {
				synchronized (leftFork) {
					synchronized (rightFork) {
						pickRightFork.run();
						pickLeftFork.run();
						eat.run();
						putLeftFork.run();
						putRightFork.run();
					}
				}
			}
		}
	}

	class Philosopher {

		final int philosopher;
		final Runnable pickLeftFork;
		final Runnable pickRightFork;
		final Runnable eat;
		final Runnable putLeftFork;
		final Runnable putRightFork;

		public Philosopher(int philosopher, Runnable pickLeftFork, Runnable pickRightFork, Runnable eat, Runnable putLeftFork, Runnable putRightFork) {
			this.philosopher = philosopher;
			this.pickLeftFork = pickLeftFork;
			this.pickRightFork = pickRightFork;
			this.eat = eat;
			this.putLeftFork = putLeftFork;
			this.putRightFork = putRightFork;
		}

		public int getPhilosopher() {
			return philosopher;
		}

		public Runnable getPickLeftFork() {
			return pickLeftFork;
		}

		public Runnable getPickRightFork() {
			return pickRightFork;
		}

		public Runnable getEat() {
			return eat;
		}

		public Runnable getPutLeftFork() {
			return putLeftFork;
		}

		public Runnable getPutRightFork() {
			return putRightFork;
		}
	}
}
