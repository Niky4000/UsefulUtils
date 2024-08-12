package com.ibs.zzztestapplication2;

public interface InterfaceB {

	default void printSome(String str) {
		System.out.println(str);
	}

	default void printSome2(String str) {
		System.out.println(str);
	}
}
