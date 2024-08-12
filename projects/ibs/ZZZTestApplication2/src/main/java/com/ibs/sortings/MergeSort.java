package com.ibs.sortings;

public class MergeSort {

	public static int[] merge(int[] arr, int[] arr2) {
		int[] res = new int[arr.length + arr2.length];
		int arrIndex = 0;
		int arr2Index = 0;
		int resIndex = 0;
		while (arrIndex < arr.length) {
			if (arr2Index >= arr2.length) {
				res[resIndex] = arr[arrIndex];
				arrIndex++;
			} else if (arr[arrIndex] < arr2[arr2Index]) {
				res[resIndex] = arr[arrIndex];
				arrIndex++;
			} else if (arr2.length > arrIndex) {
				res[resIndex] = arr2[arr2Index];
				arr2Index++;
			}
			resIndex++;
		}
		for (int j = arr2Index; j < arr2.length; j++) {
			res[resIndex] = arr2[j];
			resIndex++;
		}
		return res;
	}
}
