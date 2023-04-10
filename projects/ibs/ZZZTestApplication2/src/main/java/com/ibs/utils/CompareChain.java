package com.ibs.utils;

import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface CompareChain {

	public static <T> int compare(T obj1, T obj2, Function<T, Comparable>... fields) {
		return Stream.of(fields).map(field -> compareObjects(field.apply(obj1), field.apply(obj2))).filter(r -> r != 0).findFirst().orElse(0);
	}

	public static int compare(Supplier<Entry<Comparable, Comparable>>... supplier) {
		return Stream.of(supplier).map(s -> compareObjects(s.get().getKey(), s.get().getValue())).filter(r -> r != 0).findFirst().orElse(0);
	}

	private static int compareObjects(Comparable obj1, Comparable obj2) {
		if (obj1 != null && obj2 != null) {
			return obj1.compareTo(obj2);
		} else if (obj1 == null && obj2 != null) {
			return -1;
		} else if (obj1 != null && obj2 == null) {
			return 1;
		} else {
			return 0;
		}
	}
}
