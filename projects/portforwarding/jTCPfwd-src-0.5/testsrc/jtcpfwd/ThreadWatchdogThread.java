package jtcpfwd;

import java.util.Iterator;
import java.util.Map;

/**
 * A watchdog thread that watches if all threads terminate properly.
 */
public class ThreadWatchdogThread extends Thread {

	private final int delay;

	public ThreadWatchdogThread(int delay) {
		super("Thread Watchdog Thread");
		this.delay = delay;
		setDaemon(true);
	}

	public void run() {
		try {
			Thread.sleep(delay);
			Map stackTraces = (Map) Thread.class.getMethod("getAllStackTraces", new Class[0]).invoke(null, new Object[0]);
			for (Iterator it = stackTraces.keySet().iterator(); it.hasNext();) {
				Thread t = (Thread) it.next();
				if (!t.isDaemon()) {
					System.err.println("Thread " + t.getName() + " [" + t.getThreadGroup().getName() + "] still alive!");
					StackTraceElement[] stackTrace = (StackTraceElement[]) stackTraces.get(t);
					for (int i = 0; i < stackTrace.length; i++) {
						System.err.println("\tat " + stackTrace[i]);
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(5);
	};
}
