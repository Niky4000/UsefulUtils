package ru.ibs.kmplib.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author me
 */
public class Utils {

	public static <T> List<List<T>> partition(List<T> list, int slice) {
		List<List<T>> list2 = new ArrayList<>();
		for (int i = 0; i < (list.size() / slice) + 1; i++) {
			list2.add(new ArrayList<>(list.subList(i * slice, (i + 1) * slice)));
		}
		return list2;
	}
}
