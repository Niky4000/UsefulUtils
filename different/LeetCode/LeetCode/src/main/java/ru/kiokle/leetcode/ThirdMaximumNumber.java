package ru.kiokle.leetcode;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ThirdMaximumNumber {

	public int thirdMax(int[] nums) {
		int[] sortedList = IntStream.of(nums).distinct().toArray();
		Arrays.sort(sortedList);
		return sortedList.length >= 3 ? sortedList[sortedList.length - 3] : sortedList[sortedList.length - 1];
	}
}
