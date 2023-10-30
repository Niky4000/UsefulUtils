package ru.kiokle.leetcode.concurrency;

import org.junit.Test;

public class PrintZeroEvenOddTest {

	@Test
	public void test() throws InterruptedException {
		new PrintZeroEvenOdd().startThreads();
	}
}
