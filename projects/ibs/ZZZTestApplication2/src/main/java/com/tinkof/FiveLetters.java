package com.tinkof;

import java.util.HashSet;
import java.util.Set;

public class FiveLetters {

	public static void test() {
		System.out.println(analyze("ДОЖДЬ, ДЗЮДО"));
	}

	private static String analyze(String string) {
		String[] split = string.split(", ");
		String userAnswer = split[0];
		String wrightAnswer = split[1];

		Set<Character> set = new HashSet<>(wrightAnswer.length());
		for (int i = 0; i < wrightAnswer.length(); i++) {
			char charAt = wrightAnswer.charAt(i);
			set.add(charAt);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < userAnswer.length(); i++) {
			if (wrightAnswer.charAt(i) == userAnswer.charAt(i)) {
				sb.append("1");
			} else if (set.contains(userAnswer.charAt(i))) {
				sb.append("0");
			} else {
				sb.append("-1");
			}
			if (i < userAnswer.length() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
