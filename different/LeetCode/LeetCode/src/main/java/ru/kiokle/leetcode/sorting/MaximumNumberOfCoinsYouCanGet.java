package ru.kiokle.leetcode.sorting;

import java.util.Arrays;

public class MaximumNumberOfCoinsYouCanGet {

	public int maxCoins(int[] piles) {
		Arrays.sort(piles);
		int sum = 0;
		int turn = piles.length / 3;
		for (int i = piles.length - 2; turn-- > 0; i -= 2) {
			sum += piles[i];
		}
		return sum;
	}

	public int maxCoins2(int[] piles) {
		int max = 0;
		Arrays.sort(piles);
		int temp = piles.length / 3;
		for (int i = piles.length - 2; temp-- > 0; i -= 2) {
			max += piles[i];
		}
		return max;
	}
}
