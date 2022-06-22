package com.memoryleak;

import java.util.ArrayList;
import java.util.List;

public class SomeMemoryLeakClass {

	List<String> list = new ArrayList<>();

	public void pruduceLeakage() {
		System.out.println("Memory leakage emulation!");
		Long i = 0L;
		while (true) {
			list.add("Hello" + (++i));
		}
	}
}
