package ru.kiokle.leetcode.sorting;

import ru.kiokle.leetcode.sorting.RevealCardsInIncreasingOrder;
import java.util.stream.IntStream;
import org.junit.Test;

public class RevealCardsInIncreasingOrderTest {

	@Test
	public void test() {
		int[] deckRevealedIncreasing = new RevealCardsInIncreasingOrder().deckRevealedIncreasing2(new int[]{17, 13, 11, 2, 3, 5, 7});
		IntStream.of(deckRevealedIncreasing).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] deckRevealedIncreasing2 = new RevealCardsInIncreasingOrder().deckRevealedIncreasing2(new int[]{1, 1000});
		IntStream.of(deckRevealedIncreasing2).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		int[] deckRevealedIncreasing3 = new RevealCardsInIncreasingOrder().deckRevealedIncreasing2(new int[]{1, 2, 3, 4, 5, 6});
		IntStream.of(deckRevealedIncreasing3).forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
	}
}
