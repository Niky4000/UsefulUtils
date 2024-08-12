package com.ibs.sortings;

public class MergeSort2 {

    public static int[] sort(int[] arr, int begin, int end) {
        if (end - begin < 1) {
            return arr;
        }
        int middle = (end - begin + 1) / 2;
        int[] left = copyArray(arr, begin, middle);
        int[] right = copyArray(arr, middle, end + 1);
        int[] sortLeft = sort(left, 0, left.length - 1);
        int[] sortRight = sort(right, 0, right.length - 1);
        return merge(sortLeft, sortRight);
    }

    private static int[] merge(int[] left, int[] right) {
        if (left.length == 0) {
            return right;
        } else if (right.length == 0) {
            return left;
        }
        int[] arr = new int[left.length + right.length];
        int leftIndex = 0;
        int rightIndex = 0;
        for (int i = 0; i < left.length + right.length; i++) {
            if (leftIndex == left.length) {
                arr[i] = right[rightIndex++];
            } else if (rightIndex == right.length) {
                arr[i] = left[leftIndex++];
            } else if (left[leftIndex] <= right[rightIndex]) {
                arr[i] = left[leftIndex++];
            } else {
                arr[i] = right[rightIndex++];
            }
        }
        return arr;
    }

    private static int[] copyArray(int[] arr, int begin, int end) {
        int[] array = new int[end - begin];
        for (int i = begin; i < end; i++) {
            array[i - begin] = arr[i];
        }
        return array;
    }
}
