package ru.ibs.pmp.zzztestapplication.interfaces;

@FunctionalInterface
public interface FirstInterface {

	public String some(String hello, String hello2);
	
	default String someMethod(String str) {
		return "Hello " + str + "!";
	}
}
