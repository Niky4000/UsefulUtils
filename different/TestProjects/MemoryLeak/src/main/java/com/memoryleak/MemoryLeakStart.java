package com.memoryleak;

import java.util.ArrayList;
import java.util.List;

public class MemoryLeakStart {

	public static void main(String[] args) {
		System.out.println("Memory leakage emulation!");
		List<String> list = new ArrayList<>();
		Long i = 0L;
		while (true) {
			list.add("Hello" + (++i));
		}
	}
}
