package ru.kiokle.leetcode;

import java.util.Arrays;

public class SortTheStudentsByTheirKthScore {

	public int[][] sortTheStudents(int[][] score, int k) {
		Arrays.sort(score, (n1, n2) -> n2[k] - n1[k]);
		return score;
	}
}
