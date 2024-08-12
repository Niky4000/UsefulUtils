package ru.kiokle.leetcode.concurrency;

import org.junit.Test;

public class PrintInOrderTest {

	@Test
	public void test() throws InterruptedException {
		new PrintInOrder().startThreads();
	}
}
