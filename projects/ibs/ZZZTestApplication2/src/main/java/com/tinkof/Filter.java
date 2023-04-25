package com.tinkof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Filter {

	public static void test() {
//		List<Integer> firstCollection = new ArrayList<>();
//		List<Integer> firstCollection = Arrays.asList(3, 5, 4, 5, 4);
		List<Integer> firstCollection = Arrays.asList(3, 5, 4, 5, 4, 5, 3, 2, 4, 6, 3, 4, 6, 5, 4);
		List<Integer> secondCollection = Arrays.asList(5, 4);
//		List<Integer> secondCollection = new ArrayList<>();
		List<Integer> filtered = new Filter().filterOneCollectionByAnother(firstCollection, secondCollection);
		System.out.println(filtered);
	}

	public List<Integer> filterOneCollectionByAnother(List<Integer> firstCollection, List<Integer> secondCollection) {
		// пожалуйста добавьте свой код здесь
		Set<Integer> set = new HashSet<>(secondCollection);
		List<Integer> list = firstCollection.stream().filter(obj -> set.contains(obj)).collect(Collectors.toList());
		return list;
	}
}
