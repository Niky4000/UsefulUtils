package ru.kiokle.leetcode.sorting;

import java.util.Arrays;

public class MinimizeMaximumPairSumInArray {

	public int minPairSum(int[] nums) {
		Arrays.sort(nums);
		int pairSum = 0;
		for (int i = 0; i < nums.length / 2; i++) {
			pairSum = Math.max(pairSum, nums[i] + nums[nums.length - i - 1]);
		}
		return pairSum;
	}
}
