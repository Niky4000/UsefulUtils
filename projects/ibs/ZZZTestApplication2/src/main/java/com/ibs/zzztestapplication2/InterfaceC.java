package com.ibs.zzztestapplication2;

@FunctionalInterface
public interface InterfaceC {

	void runSomething();

	default String returnSomeString() {
		return "Hello!";
	}

	default String returnSomeString2() {
		return "Hello!";
	}

	default String returnSomeString3() {
		return "Hello!";
	}

	default String returnSomeString4() {
		return "Hello!";
	}
}
