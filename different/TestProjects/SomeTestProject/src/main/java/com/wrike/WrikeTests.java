/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wrike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author me
 */
public class WrikeTests {

	public static void main(String[] args) {
//		System.out.println(getSteak(7, Arrays.asList("YYY", "YYY", "YYY", "YYY", "YNN", "YYY", "YY")));
//		System.out.println(getAnagrams(Arrays.asList("poke", "okpe", "pokes", "sekop", "ekop")));
//		System.out.println(getAnagrams(Arrays.asList("code", "aaagmnrs", "anagrams", "doce")));
//		System.out.println(getPairs(Arrays.asList(1, 2, 3, 4, 5, 6), 2));
//		System.out.println(getPairs(Arrays.asList(1, 1, 2, 2, 3, 3), 1));
//		System.out.println(null != null);
//		System.out.println(false | true);

//		int k = 1;
//		int i = ++k + k++ + +k;
//		System.out.println(1 / 2 + 3 / 2 + 0.1);
//		int a, b, c;
//		b = 10;
//		a = b = c = 20;
//		System.out.println(a);
		String a, b, c;
		a = new String("dog");
		b = new String("cat");
		c = new String("ccc");
		b = a;
		a = new String("ggg");
		System.out.println(b);
	}

	public static int getPairs(List<Integer> numbers, int k) {
		int counter = 0;
		for (int i = 0; i < numbers.size(); i++) {
			int number = numbers.get(i);
			for (int j = i; j < numbers.size(); j++) {
				if (j == i) {
					continue;
				} else {
					int number2 = numbers.get(j);
					if (number + k == number2) {
						counter++;
					}
				}
			}
		}
		return counter;
	}

	public static int getSteak(int m, List<String> data) {
		String previousOne = "Y";
		int counter = 0;
		List<Integer> max = new ArrayList<>();
		for (String d : data) {
			if (!previousOne.contains("N") && !d.contains("N")) {
				counter++;
			} else {
				max.add(counter);
				counter = 0;
			}
		}
		max.add(counter);
		Integer ret = max.stream().max(Integer::compareTo).orElse(0);
		return ret;
	}

	/* // */
	public static List<String> getAnagrams(List<String> text) {
		if (!text.isEmpty()) {

			LinkedHashMap<String, String> map = new LinkedHashMap<>();
			for (String s : text) {
				map.putIfAbsent(getLettersSet(s).stream().sorted().reduce("", (s1, s2) -> s1 + s2), s);
			}
			ArrayList<String> list = new ArrayList<>(map.values());
			Collections.sort(list);
			return list;
//			Set<String> resultSet = new HashSet<>();
//			Set<String> skippedSet = new HashSet<>();
//			for (int step = 0; step < text.size(); step++) {
//				String stringToCompareWith = text.get(step);
//				if (resultSet.contains(stringToCompareWith) || skippedSet.contains(stringToCompareWith)) {
//					continue;
//				} else {
//					Set<String> setToCompareWith = getLettersSet(stringToCompareWith);
//					for (int i = step + 1; i < text.size(); i++) {
//						String nextString = text.get(i);
//						Set<String> nextSet = getLettersSet(nextString);
//						if (stringToCompareWith.length() != nextString.length() || !setToCompareWith.containsAll(nextSet)) {
//							resultSet.add(stringToCompareWith);
//						} else {
//							skippedSet.add(nextString);
//						}
//					}
//				}
//			}
//			List<String> result = resultSet.stream().sorted().collect(Collectors.toList());
//			return result;
		} else {
			return new ArrayList<>();
		}
	}

	private static Set<String> getLettersSet(String str) {
		Set<String> set = new HashSet<>(str.length());
		for (int i = 0; i < str.length(); i++) {
			set.add(str.substring(i, i + 1));
		}
		return set;
	}

}
