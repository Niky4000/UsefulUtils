package ru.kiokle.leetcode.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SortVowelsInAString {

	Set<Character> set = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U'));

	public String sortVowels(String s) {
		List<Integer> characterIndexList = new ArrayList<>();
		List<Character> characterList = new ArrayList<>();
		for (int i = 0; i < s.length(); i++) {
			char charAt = s.charAt(i);
			if (set.contains(charAt)) {
				characterIndexList.add(i);
				characterList.add(charAt);
			}
		}
		Collections.sort(characterList);
		StringBuilder sb = new StringBuilder(s);
		for (int i = 0; i < characterList.size(); i++) {
			sb.setCharAt(characterIndexList.get(i), characterList.get(i));
		}
		return sb.toString();
	}
}
