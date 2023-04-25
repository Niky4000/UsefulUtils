package com.ibs.sortings;

public class QuickSort {

	public static void quickSort(int[] arr, int begin, int end) {
		if (begin < end) {
			int index = sort(arr, begin, end);
			quickSort(arr, begin, index - 1);
			quickSort(arr, index + 1, end);
		}
	}

	private static int sort(int[] arr, int begin, int end) {
		int pivotal = arr[end];
		int i = begin - 1;
		for (int j = begin; j < end; j++) {
			if (arr[j] <= pivotal) {
				i++;
				int temp = arr[j];
				arr[i] = arr[j];
				arr[j] = temp;
			}
		}
		int temp = arr[i + 1];
		arr[i + 1] = arr[end];
		arr[end] = temp;
		return i + 1;
	}
}
