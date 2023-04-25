package com.tinkof;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Compression {

	public static void test() {
		compress("1, 2, 2, 3, 4, 3, 3, 3");
	}

	private static String compress(String string) {
		String[] array = string.split(", ");
		String previous = "";
		int count = 0;
		List<Map.Entry<String, Integer>> list = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			String str = array[i];
			if (previous.equals(str)) {
				count++;
			} else if (!previous.equals(str) && count == 0) {
				count++;
				previous = str;
			} else if (previous.length() > 0) {
				list.add(new AbstractMap.SimpleEntry<>(previous, count));
				count = 1;
				previous = str;
			} else if (previous.length() == 0) {
				previous = str;
				count = 1;
			}
			if (i == array.length - 1) {
				list.add(new AbstractMap.SimpleEntry<>(str, count));
			}
		}
		String str = list.stream().map(entry -> "(" + entry.getKey() + ", " + entry.getValue() + ")").reduce((s1, s2) -> s1 + ", " + s2).get();
		return str;
	}
}
