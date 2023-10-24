package ru.kiokle.leetcode;

import java.util.stream.IntStream;
import org.junit.Test;

public class FindSubsequenceOfLengthKWithTheLargestSumTest {

	@Test
	public void test() {
		int[] maxSubsequence = new FindSubsequenceOfLengthKWithTheLargestSum().maxSubsequence(new int[]{2, 1, 3, 3}, 2);
		IntStream.of(maxSubsequence).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] maxSubsequence2 = new FindSubsequenceOfLengthKWithTheLargestSum().maxSubsequence(new int[]{-1, -2, 3, 4}, 3);
		IntStream.of(maxSubsequence2).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] maxSubsequence3 = new FindSubsequenceOfLengthKWithTheLargestSum().maxSubsequence(new int[]{50, -75}, 2);
		IntStream.of(maxSubsequence3).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] maxSubsequence4 = new FindSubsequenceOfLengthKWithTheLargestSum().maxSubsequence(new int[]{3,4,3,3}, 2);
		IntStream.of(maxSubsequence4).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
	}
}
