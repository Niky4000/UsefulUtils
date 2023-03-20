package com.ibs.zzztestapplication2;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;

public class SomeClass {

	public static void main(String[] args) {
//		System.out.println(nh(12345678.22d));
//		System.out.println(nh(18.22d));
//		System.out.println(nh(8.22d));
//		System.out.println(nh(2.2d));
//		System.out.println(nh(0d));
//		printPar(3);
		System.out.println(makeChange(50, 25));
	}

	public static int makeChange(int n, int denom) {
		int next_denom = 0;
		switch (denom) {
			case 25:
				next_denom = 10;
				break;
			case 10:
				next_denom = 5;
				break;
			case 5:
				next_denom = 1;
				break;
			case 1:
				return 1;
		}
		int ways = 0;
		for (int i = 0; i * denom <= n; i++) {
			ways += makeChange(n - i * denom, next_denom);
		}
		return ways;
	}

	public static void printPar(int l, int r, char[] str, int count) {
		if (l < 0 || r < l) {
			return; // invalid state
		}
		if (l == 0 && r == 0) {
			System.out.println(str); // found one, so print it
		} else {
			if (l > 0) { // try a left paren, if there are some available
				str[count] = '(';
				printPar(l - 1, r, str, count + 1);
			}
			if (r > l) { // try a right paren, if there’s a matching left
				str[count] = ')';
				printPar(l, r - 1, str, count + 1);
			}
		}
	}

	public static void printPar(int count) {
		char[] str = new char[count * 2];
		printPar(count, count, str, 0);
	}

	private static final String zeroEnding = ".0";
	private static final String dot = ".";
	private static final DecimalFormat df = new DecimalFormat("#0.00");

	public static String nh(Number n) {
		if (n != null) {
			String str = df.format(n);
			if (str.endsWith(zeroEnding)) {
				return addSpacesBetweenThreeDigits(str.substring(0, str.length() - zeroEnding.length()));
			} else if (str.contains(dot)) {
				if (str.substring(str.indexOf(dot), str.length()).length() < 3) {
					return addSpacesBetweenThreeDigits(str + "0");
				} else {
					return addSpacesBetweenThreeDigits(str);
				}
			} else {
				return addSpacesBetweenThreeDigits(str);
			}
		} else {
			return "";
		}
	}

	public static String addSpacesBetweenThreeDigits(String digitAsString) {
		String digitAsStringWithoutSpaces = removeSpaces(digitAsString);
		StringBuilder sb = new StringBuilder();
		String addLater = "";
		int indexFrom = digitAsStringWithoutSpaces.length() - 1;
		if (digitAsStringWithoutSpaces.contains(".")) {
			addLater = digitAsStringWithoutSpaces.substring(digitAsStringWithoutSpaces.indexOf("."));
			indexFrom = digitAsStringWithoutSpaces.indexOf(".") - 1;
		}
		for (int i = 0; i <= indexFrom; i++) {
			sb.append(digitAsStringWithoutSpaces.charAt(indexFrom - i));
			if ((i + 1) % 3 == 0 && i != indexFrom) {
				sb.append(" ");
			}
		}
		sb.reverse();
		sb.append(addLater);
		return sb.toString();
	}

	private static String removeSpaces(String digitAsString) {
		return digitAsString.replace(" ", "");
	}
}
