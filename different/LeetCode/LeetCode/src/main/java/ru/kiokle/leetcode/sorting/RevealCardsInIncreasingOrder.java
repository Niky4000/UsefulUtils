package ru.kiokle.leetcode.sorting;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class RevealCardsInIncreasingOrder {

	public int[] deckRevealedIncreasing(int[] deck) {
		Arrays.sort(deck);
		int[] result = new int[deck.length];
		int i;
		int pointer = 0;
		int pointer2 = (deck.length + 1) / 2;
		LinkedList<Integer> linkedList = new LinkedList<>();
		for (i = 0; i < deck.length; i += 2) {
			result[i] = deck[pointer++];
			if (pointer2 < deck.length) {
				linkedList.push(deck[pointer2++]);
			}
			Integer poll = linkedList.pollFirst();
			linkedList.addLast(poll);
		}
		Integer poll = linkedList.pollFirst();
		linkedList.addLast(poll);
		for (i = 1; i < deck.length; i += 2) {
			result[i] = linkedList.pollLast();
		}
		return result;
	}

	public int[] deckRevealedIncreasing2(int[] deck) {
		int n = deck.length;
		Deque<Integer> index = new LinkedList();
		for (int i = 0; i < n; ++i) {
			index.add(i);
		}
		int[] ans = new int[n];
		Arrays.sort(deck);
		for (int card : deck) {
			ans[index.pollFirst()] = card;
			if (!index.isEmpty()) {
				index.add(index.pollFirst());
			}
		}
		return ans;
	}
}
