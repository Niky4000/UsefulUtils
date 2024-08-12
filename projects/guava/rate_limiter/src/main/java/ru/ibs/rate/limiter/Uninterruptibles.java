package ru.ibs.rate.limiter;

import static ru.ibs.rate.limiter.Internal.toNanosSaturated;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class Uninterruptibles {

	public static void awaitUninterruptibly(CountDownLatch latch) {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					latch.await();
					return;
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static boolean awaitUninterruptibly(CountDownLatch latch, Duration timeout) {
		return awaitUninterruptibly(latch, toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static boolean awaitUninterruptibly(CountDownLatch latch, long timeout, TimeUnit unit) {
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(timeout);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					return latch.await(remainingNanos, NANOSECONDS);
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static boolean awaitUninterruptibly(Condition condition, Duration timeout) {
		return awaitUninterruptibly(condition, toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static boolean awaitUninterruptibly(Condition condition, long timeout, TimeUnit unit) {
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(timeout);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					return condition.await(remainingNanos, NANOSECONDS);
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void joinUninterruptibly(Thread toJoin) {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					toJoin.join();
					return;
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void joinUninterruptibly(Thread toJoin, Duration timeout) {
		joinUninterruptibly(toJoin, toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static void joinUninterruptibly(Thread toJoin, long timeout, TimeUnit unit) {
		RateLimiterUtils.checkNotNull(toJoin);
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(timeout);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					NANOSECONDS.timedJoin(toJoin, remainingNanos);
					return;
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static <E> E takeUninterruptibly(BlockingQueue<E> queue) {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					return queue.take();
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static <E> void putUninterruptibly(BlockingQueue<E> queue, E element) {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					queue.put(element);
					return;
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void sleepUninterruptibly(Duration sleepFor) {
		sleepUninterruptibly(toNanosSaturated(sleepFor), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(sleepFor);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					NANOSECONDS.sleep(remainingNanos);
					return;
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static boolean tryAcquireUninterruptibly(Semaphore semaphore, Duration timeout) {
		return tryAcquireUninterruptibly(semaphore, toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static boolean tryAcquireUninterruptibly(
		Semaphore semaphore, long timeout, TimeUnit unit) {
		return tryAcquireUninterruptibly(semaphore, 1, timeout, unit);
	}

	public static boolean tryAcquireUninterruptibly(Semaphore semaphore, int permits, Duration timeout) {
		return tryAcquireUninterruptibly(semaphore, permits, toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static boolean tryAcquireUninterruptibly(Semaphore semaphore, int permits, long timeout, TimeUnit unit) {
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(timeout);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					return semaphore.tryAcquire(permits, remainingNanos, NANOSECONDS);
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static boolean tryLockUninterruptibly(Lock lock, Duration timeout) {
		return tryLockUninterruptibly(lock, toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static boolean tryLockUninterruptibly(Lock lock, long timeout, TimeUnit unit) {
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(timeout);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					return lock.tryLock(remainingNanos, NANOSECONDS);
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static boolean awaitTerminationUninterruptibly(ExecutorService executor, Duration timeout) {
		return awaitTerminationUninterruptibly(executor, toNanosSaturated(timeout), NANOSECONDS);
	}

	@SuppressWarnings("GoodTime")
	public static boolean awaitTerminationUninterruptibly(ExecutorService executor, long timeout, TimeUnit unit) {
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(timeout);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					return executor.awaitTermination(remainingNanos, NANOSECONDS);
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private Uninterruptibles() {
	}
}
