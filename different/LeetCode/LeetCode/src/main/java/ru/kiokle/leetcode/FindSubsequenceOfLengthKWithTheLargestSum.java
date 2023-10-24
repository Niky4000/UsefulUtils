package ru.kiokle.leetcode;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FindSubsequenceOfLengthKWithTheLargestSum {

	public int[] maxSubsequence(int[] nums, int k) {
//		Arrays.sort(nums);
//		int[] array = IntStream.range(nums.length - k, nums.length).map(i -> nums[i]).limit(k).toArray();
//		return array;
		List<int[]> entryList = IntStream.range(0, nums.length).mapToObj(i -> new int[]{i, nums[i]}).sorted((e1, e2) -> e2[1] - e1[1]).collect(Collectors.toList());
		List<int[]> kList = IntStream.range(0, k).mapToObj(i -> entryList.get(i)).sorted((e1, e2) -> e1[0] - e2[0]).collect(Collectors.toList());
		return kList.stream().mapToInt(o -> o[1]).toArray();
	}
}
