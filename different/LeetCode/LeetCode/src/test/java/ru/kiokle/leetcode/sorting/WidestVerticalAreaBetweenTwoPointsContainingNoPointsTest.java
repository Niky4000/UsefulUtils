package ru.kiokle.leetcode.sorting;

import ru.kiokle.leetcode.sorting.WidestVerticalAreaBetweenTwoPointsContainingNoPoints;
import org.junit.Test;

public class WidestVerticalAreaBetweenTwoPointsContainingNoPointsTest {

	@Test
	public void test() {
		int maxWidthOfVerticalArea = new WidestVerticalAreaBetweenTwoPointsContainingNoPoints().maxWidthOfVerticalArea2(new int[][]{new int[]{8, 7}, new int[]{9, 9}, new int[]{7, 4}, new int[]{9, 7}});
		System.out.println(maxWidthOfVerticalArea);
		int maxWidthOfVerticalArea2 = new WidestVerticalAreaBetweenTwoPointsContainingNoPoints().maxWidthOfVerticalArea2(new int[][]{new int[]{3, 1}, new int[]{9, 0}, new int[]{1, 0}, new int[]{1, 4}, new int[]{5, 3}, new int[]{8, 8}});
		System.out.println(maxWidthOfVerticalArea2);
	}
}
