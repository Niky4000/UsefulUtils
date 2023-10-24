package ru.kiokle.leetcode;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;

public class SortTheStudentsByTheirKthScoreTest {

	@Test
	public void test() {
		int[][] sortTheStudents = new SortTheStudentsByTheirKthScore().sortTheStudents(new int[][]{new int[]{10, 6, 9, 1}, new int[]{7, 5, 11, 2}, new int[]{4, 8, 3, 15}}, 2);
		print(sortTheStudents);
		int[][] sortTheStudents2 = new SortTheStudentsByTheirKthScore().sortTheStudents(new int[][]{new int[]{3, 4}, new int[]{5, 6}}, 0);
		print(sortTheStudents2);
	}

	void print(int[][] sortTheStudents) {
		Stream.of(sortTheStudents).forEach(n -> {
			IntStream.of(n).forEach(k -> System.out.print(k + " "));
			System.out.println();
		});
		System.out.println("-----------");
	}
}
