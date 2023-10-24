package ru.kiokle.leetcode;

import java.util.stream.IntStream;
import org.junit.Test;

public class SetMismatchTest {

	@Test
	public void test() {
		int[] nums = new SetMismatch().findErrorNums(new int[]{1, 2, 2, 4});
		IntStream.of(nums).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] nums2 = new SetMismatch().findErrorNums(new int[]{1, 1});
		IntStream.of(nums2).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] nums3 = new SetMismatch().findErrorNums(new int[]{2, 2});
		IntStream.of(nums3).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] nums4 = new SetMismatch().findErrorNums(new int[]{3, 2, 2});
		IntStream.of(nums4).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] nums5 = new SetMismatch().findErrorNums(new int[]{2, 3, 2});
		IntStream.of(nums5).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] nums6 = new SetMismatch().findErrorNums(new int[]{3, 3, 1});
		IntStream.of(nums6).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
	}
}
