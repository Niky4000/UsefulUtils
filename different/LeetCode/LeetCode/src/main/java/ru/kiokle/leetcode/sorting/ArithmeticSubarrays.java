package ru.kiokle.leetcode.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ArithmeticSubarrays {

	public List<Boolean> checkArithmeticSubarrays(int[] nums, int[] l, int[] r) {
		List<Boolean> list = new ArrayList<>(l.length);
		for (int i = 0; i < l.length; i++) {
			int[] tmp = IntStream.rangeClosed(l[i], r[i]).map(k -> nums[k]).toArray();
			Arrays.sort(tmp);
			boolean arithmic = isArithmic(tmp);
			list.add(arithmic);
		}
		return list;
	}

	private boolean isArithmic(int[] arr) {
		if (arr.length <= 1) {
			return false;
		} else if (arr.length == 2) {
			return true;
		} else {
			int difference = arr[1] - arr[0];
			for (int i = 1; i < arr.length; i++) {
				int diff = arr[i] - arr[i - 1];
				if (difference != diff) {
					return false;
				}
			}
			return true;
		}
	}
}
