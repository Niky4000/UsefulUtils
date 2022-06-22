package com.memoryleak;

public class MemoryLeakStart {

	public static void main(String[] args) {
		new SomeMemoryLeakClass().pruduceLeakage();
	}
}
