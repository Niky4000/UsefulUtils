package ru.ibs.testmultithreading;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import ru.ibs.sortings.BucketSort;

public class TestMultithreadingStart {

	public static void main(String[] args) throws Exception {
//		new LocksTests().testReadWriteLock();
//		int[] arr = new int[]{7, 8, 4, 5, 2, 1, 6, 3};
//		new QuickSort().quickSort(arr, 0, arr.length - 1);
//		int[] arr = new int[]{1, 2, 3, 4};
//		new QuickSort().quickSort(arr, 0, arr.length - 1);
//		int[] arr = new int[]{2, 24, 45, 66, 75, 90, 170, 802};
//		int[] arr = new int[]{75, 98, 174, 802};
//		int[] arr = new int[]{802, 178, 55, 22};
//		new RadixSort().radixSort(arr, arr.length);
		int[] arr = new int[]{25, 30, 28, 22};
//		new CountingSort().countSort(arr);
//		float[] arr = {(float) 0.42, (float) 0.32, (float) 0.33, (float) 0.52, (float) 0.37, (float) 0.47, (float) 0.51};
//		new BucketSort().bucketSort(arr, arr.length);
		new BucketSort().bucketSort(arr);
		System.out.println(IntStream.of(arr).mapToObj(i -> Integer.valueOf(i)).collect(Collectors.toList()));
//		new LocksTests().stopAnyNumberOfThreadsOnLock();
	}
}
