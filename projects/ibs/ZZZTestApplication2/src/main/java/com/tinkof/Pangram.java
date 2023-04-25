package com.tinkof;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Pangram {

	public static void testPangram() {
//		boolean pangram = isPangram("Съешь же ещё этих мягких французских булок, да выпей чаю.");
		boolean pangram = isPangram("АЯаяъЪ-+");
		System.out.println(pangram);
	}

	private static boolean isPangram(String string) {
		List<Character> list = new ArrayList<>(string.length());
		for (int i = 0; i < string.length(); i++) {
			char charAt = string.charAt(i);
			if ((int) charAt >= 1040 && (int) charAt <= 1103) {
				list.add(charAt);
			}
		}
		HashSet<Character> set = new HashSet<>(list);
		return set.size() == list.size();
	}
}
