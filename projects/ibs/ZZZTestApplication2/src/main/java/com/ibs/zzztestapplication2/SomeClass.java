package com.ibs.zzztestapplication2;

import java.text.DecimalFormat;

public class SomeClass {

	public static void main(String[] args) {
		System.out.println(nh(12345678.22d));
		System.out.println(nh(18.22d));
		System.out.println(nh(8.22d));
		System.out.println(nh(2.2d));
		System.out.println(nh(0d));
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
