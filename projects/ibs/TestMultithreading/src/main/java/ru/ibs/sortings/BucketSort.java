package ru.ibs.sortings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BucketSort {

//user-defined method to sort array  
	public void bucketSort(int[] array, int length, int bucketsCount) {
//creating a list of buckets for storing lists  
		List<Integer>[] buckets = new List[bucketsCount];
// Linked list with each bucket array index  
// as there may be hash collision   
		for (int i = 0; i < bucketsCount; i++) {
			buckets[i] = new LinkedList<>();
		}
//calculate the hash and assigns elements to the proper bucket  
		for (int num : array) {
			buckets[hash(num, bucketsCount)].add(num);
		}
//iterate over the buckets and sorts the elements  
		for (List<Integer> bucket : buckets) {
//sorts the bucket      
			Collections.sort(bucket);
		}
		int index = 0;
//gethered the buckets after sorting  
		for (List<Integer> bucket : buckets) {
			for (int num : bucket) {
				array[index++] = num;
			}
		}
	}

//distributing elements   
	private int hash(int num, int bucketSize) {
		return num / bucketSize;
	}

//------------------------------------------------------------------------------
//user-defined method that sorts the array elements      
	public int[] bucketSort(int[] array) {
		int maxValue = getMax(array, array.length);
//createsa buckets array  
		int[] bucket = new int[maxValue + 1];
//creating an array for storing the sorted elements  
		int[] sorted_array = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			bucket[array[i]]++;
		}
		int outPos = 0;
		for (int i = 0; i < bucket.length; i++) {
			for (int j = 0; j < bucket[i]; j++) {
				sorted_array[outPos++] = i;
			}
		}
//returns sorted array  
		return sorted_array;
	}
//method that prins the array elements   

	static void printArray(int[] sorted_array) {
		for (int i = 0; i < sorted_array.length; i++) {
			System.out.print(sorted_array[i] + " ");
		}
	}
//method finds the maximum elements of the array and returns the same  

	static int maxValue(int[] array) {
		int maxValue = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > maxValue) {
				maxValue = array[i];
			}
		}
		return maxValue;
	}
//------------------------------------------------------------------------------

	public void bucketSort(float[] arr, int n) {
		if (n <= 0) {
			return;
		}
		@SuppressWarnings("unchecked")
		ArrayList<Float>[] bucket = new ArrayList[n];

		// Create empty buckets
		for (int i = 0; i < n; i++) {
			bucket[i] = new ArrayList<Float>();
		}

		// Add elements into the buckets
		for (int i = 0; i < n; i++) {
			int bucketIndex = (int) arr[i] * n;
			bucket[bucketIndex].add(arr[i]);
		}

		// Sort the elements of each bucket
		for (int i = 0; i < n; i++) {
			Collections.sort((bucket[i]));
		}

		// Get the sorted array
		int index = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0, size = bucket[i].size(); j < size; j++) {
				arr[index++] = bucket[i].get(j);
			}
		}
	}

	// A utility function to get maximum value in arr[]
	private int getMax(int arr[], int n) {
		int mx = arr[0];
		for (int i = 1; i < n; i++) {
			if (arr[i] > mx) {
				mx = arr[i];
			}
		}
		return mx;
	}
}
