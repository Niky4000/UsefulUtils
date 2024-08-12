package ru.kiokle.leetcode.sorting;

import ru.kiokle.leetcode.sorting.MinimizeMaximumPairSumInArray;
import org.junit.Test;

public class MinimizeMaximumPairSumInArrayTest {

	@Test
	public void test() {
		int minPairSum = new MinimizeMaximumPairSumInArray().minPairSum(new int[]{3, 5, 2, 3});
		System.out.println(minPairSum);
		int minPairSum2 = new MinimizeMaximumPairSumInArray().minPairSum(new int[]{3, 5, 4, 2, 4, 6});
		System.out.println(minPairSum2);
	}
}
