package ru.kiokle.leetcode;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;

public class SortTheMatrixDiagonallyTest {

	@Test
	public void test() {
		int[][] diagonalSort = new SortTheMatrixDiagonally().diagonalSort(new int[][]{new int[]{3, 3, 1, 1}, new int[]{2, 2, 1, 2}, new int[]{1, 1, 1, 2}});
		print(diagonalSort);
//		int[][] diagonalSort2 = new SortTheMatrixDiagonally().diagonalSort(new int[][]{new int[]{1, 1, 1, 1}, new int[]{1, 2, 2, 2}, new int[]{1, 2, 3, 3}});
//		print(diagonalSort2);
	}

	void print(int[][] sortTheStudents) {
		Stream.of(sortTheStudents).forEach(n -> {
			IntStream.of(n).forEach(k -> System.out.print(k + " "));
			System.out.println();
		});
		System.out.println("-----------");
	}
}
