package ru.kiokle.leetcode.concurrency;

import org.junit.Test;

public class TheDiningPhilosophersTest {

	@Test
	public void test() throws InterruptedException {
		new TheDiningPhilosophers().startThreads();
	}
}
