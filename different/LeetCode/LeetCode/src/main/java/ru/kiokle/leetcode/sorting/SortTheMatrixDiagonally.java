package ru.kiokle.leetcode.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SortTheMatrixDiagonally {

	public int[][] diagonalSort(int[][] mat) {
		List<List<int[]>> diagonalCoordinates = new ArrayList<>();
		for (int i = 0; i < mat.length; i++) {
			diagonalCoordinates.addAll(getDiagonal(i, 0, mat));
		}
		for (int i = 1; i < mat[0].length; i++) {
			diagonalCoordinates.addAll(getDiagonal(0, i, mat));
		}
		List<List<Integer>> listOfLists = new ArrayList<>();
		for (List<int[]> diag : diagonalCoordinates) {
			List<Integer> listToSort = new ArrayList<>();
			for (int[] d : diag) {
				int digit = mat[d[0]][d[1]];
				listToSort.add(digit);
			}
			Collections.sort(listToSort);
			listOfLists.add(listToSort);
		}
		Iterator<List<Integer>> iterator = listOfLists.iterator();
		int[][] mat2 = new int[mat.length][mat[0].length];
		for (List<int[]> diag : diagonalCoordinates) {
			List<Integer> next = iterator.next();
			Iterator<Integer> iterator2 = next.iterator();
			for (int[] d : diag) {
				mat2[d[0]][d[1]] = iterator2.next();
			}
		}
		return mat2;
	}

	private List<List<int[]>> getDiagonal(int rowIndex, int colIndex, int[][] mat) {
		int j = colIndex;
		List<List<int[]>> result = new ArrayList<>(mat.length);
		List<int[]> list = new ArrayList<>();
		for (int i = rowIndex; i < mat.length; i++) {
			int[] row = mat[rowIndex];
			if (j + 1 > row.length) {
				result.add(list);
				list = new ArrayList<>();
				break;
			}
			list.add(new int[]{i, j});
			j++;
			if (i == mat.length - 1) {
				result.add(list);
				list = new ArrayList<>();
			}
		}
		return result;
	}
}
