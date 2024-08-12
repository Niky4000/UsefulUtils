package com.ibs.zzztestapplication2;

public class ClassAB implements InterfaceA, InterfaceB, InterfaceC {

	public void doSomething() {
		printSome2("str");
		printSome3("str");
	}

	@Override
	public void printSome(String str) {
		InterfaceA.super.printSome(str);
	}

	@Override
	public void runSomething() {
		returnSomeString();
	}
}
