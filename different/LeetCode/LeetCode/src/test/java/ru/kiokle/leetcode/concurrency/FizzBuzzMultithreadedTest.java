package ru.kiokle.leetcode.concurrency;

import org.junit.Test;

public class FizzBuzzMultithreadedTest {

	@Test
	public void test() throws InterruptedException {
		new FizzBuzzMultithreaded().startThreads();
	}
}
