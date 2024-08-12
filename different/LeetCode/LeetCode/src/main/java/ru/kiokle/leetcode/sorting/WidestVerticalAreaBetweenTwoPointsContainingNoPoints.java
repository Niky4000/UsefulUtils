package ru.kiokle.leetcode.sorting;

import java.util.Arrays;

public class WidestVerticalAreaBetweenTwoPointsContainingNoPoints {

	public int maxWidthOfVerticalArea2(int[][] points) {
		int n = points.length;
		int[] tmp = new int[n];
		for (int i = 0; i < n; i++) {
			tmp[i] = points[i][0];
		}
		Arrays.sort(tmp);
		int d = 0;
		for (int i = 1; i < n; i++) {
			d = Math.max(d, tmp[i] - tmp[i - 1]);
		}
		return d;
	}
}
