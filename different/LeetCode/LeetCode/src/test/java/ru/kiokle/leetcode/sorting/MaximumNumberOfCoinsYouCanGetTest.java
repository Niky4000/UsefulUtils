package ru.kiokle.leetcode.sorting;

import ru.kiokle.leetcode.sorting.MaximumNumberOfCoinsYouCanGet;
import org.junit.Test;

public class MaximumNumberOfCoinsYouCanGetTest {

	@Test
	public void test() {
		int maxCoins = new MaximumNumberOfCoinsYouCanGet().maxCoins(new int[]{2, 4, 1, 2, 7, 8});
		System.out.println(maxCoins);
		int maxCoins2 = new MaximumNumberOfCoinsYouCanGet().maxCoins(new int[]{2, 4, 5});
		System.out.println(maxCoins2);
		int maxCoins3 = new MaximumNumberOfCoinsYouCanGet().maxCoins(new int[]{9, 8, 7, 6, 5, 1, 2, 3, 4});
		System.out.println(maxCoins3);
	}
}
