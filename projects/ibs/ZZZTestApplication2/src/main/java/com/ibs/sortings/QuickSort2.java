package com.ibs.sortings;

public class QuickSort2 {

    public static void sort(int[] arr, int begin, int end) {
        if (begin < end) {
            int index = partition(arr, begin, end);
            sort(arr, begin, index - 1);
            sort(arr, index + 1, end);
        }
    }

    private static int partition(int[] arr, int begin, int end) {
        int i = begin - 1;
        int pivot = arr[end];
        for (int j = begin; j < end; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, end);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
