package ru.ibs.pmp.zzztestapplication.interfaces;

@FunctionalInterface
public interface SecondInterface {

	public String some(String hello, String hello2);

	default String someMethod(String str) {
		return "Hello " + str + "!";
	}

	default String someMethod2(String str) {
		return "Hello2 " + str + "!";
	}
}
