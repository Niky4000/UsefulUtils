package ru.kiokle.leetcode.sorting;

import ru.kiokle.leetcode.sorting.ArithmeticSubarrays;
import java.util.List;
import org.junit.Test;

public class ArithmeticSubarraysTest {

	@Test
	public void test() {
		List<Boolean> checkArithmeticSubarrays = new ArithmeticSubarrays().checkArithmeticSubarrays(new int[]{4, 6, 5, 9, 3, 7}, new int[]{0, 0, 2}, new int[]{2, 3, 5});
		System.out.println("-----&&&&-----");
		checkArithmeticSubarrays.forEach(b -> System.out.print(b + " "));
		System.out.println();
		System.out.println("-----&&&&-----");
	}
}
