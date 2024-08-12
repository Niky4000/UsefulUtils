package com.tinkof;

public class RecursivePrinter {

	public static void test() {
		print(10);
	}

	private static void print(int n) {
		if (n < 1) {
			return;
		} else {
			print(n - 1);
			System.out.println(n);
		}
	}
}
