package ru.kiokle.leetcode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CheckIfNAndItsDoubleExist {

	public boolean checkIfExist(int[] arr) {
		Map<Integer, List<Integer>> map = IntStream.range(0, arr.length).mapToObj(i -> i).collect(Collectors.groupingBy(i -> arr[i] * 2));
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] % 2 == 0) {
				List<Integer> index = map.get(arr[i]);
				if (index != null && (index.size() > 1 || !index.get(0).equals(i))) {
					return true;
				}
			}
		}
		return false;
	}
}
