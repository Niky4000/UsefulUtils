package ru.kiokle.leetcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SetMismatch {

	public int[] findErrorNums(int[] nums) {
		int[] answer = new int[]{-1, -1};
		Set<Integer> set = new HashSet<>(nums.length);
		for (int i = 0; i < nums.length; i++) {
			if (!set.add(nums[i])) {
				answer[0] = nums[i];
			}
		}
		Set<Integer> set2 = IntStream.rangeClosed(1, nums.length).mapToObj(i -> i).collect(Collectors.toSet());
		set2.removeAll(set);
		answer[1] = new ArrayList<>(set2).get(0);
		return answer;
	}
}
