package ru.kiokle.leetcode.concurrency;

import org.junit.Test;

public class PrintFooBarAlternatelyTest {

	@Test
	public void test() throws InterruptedException {
		new PrintFooBarAlternately().startThreads();
	}
}
