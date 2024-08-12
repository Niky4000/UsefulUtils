package com.memoryleak;

public class MemoryLeakStart {

	public static void main(String[] args) throws Exception {
//		new SomeMemoryLeakClass().pruduceLeakage();
		System.out.println("NsiControllerTest!");
		NsiControllerTest nsiControllerTest = new NsiControllerTest();
		int iterationCount = args.length == 1 ? Integer.valueOf(args[0]) : 100;
		for (int i = 0; i < iterationCount; i++) {
			nsiControllerTest.testPoi();
		}
	}
}
