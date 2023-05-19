package com.ibs.zzztestapplication2;

public interface InterfaceA {

	default void printSome(String str) {
		System.out.println(str);
	}

	default void printSome3(String str) {
		System.out.println(str);
	}
}
