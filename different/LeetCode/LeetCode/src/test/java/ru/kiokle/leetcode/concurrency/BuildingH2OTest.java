package ru.kiokle.leetcode.concurrency;

import org.junit.Test;

public class BuildingH2OTest {

	@Test
	public void test() throws InterruptedException {
		new BuildingH2O().startThreads();
	}
}
