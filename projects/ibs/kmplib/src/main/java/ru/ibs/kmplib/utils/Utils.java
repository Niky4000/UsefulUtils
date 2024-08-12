package ru.ibs.kmplib.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * General Utils
 *
 * @author me
 */
public class Utils {

	/**
	 *
	 * Разбиение коллекции на подколлекции не более определённого размера!
	 * Нужно, чтобы не тащить лишние зависимости!
	 *
	 * @param <T> - типизированная коллекция объектов
	 * @param list - входящая коллекция
	 * @param slice - кусочек
	 * @return - разбитую колеекцию объектов на подколлекции
	 */
	public static <T> List<List<T>> partition(List<T> list, int slice) {
		List<List<T>> list2 = new ArrayList<>();
		for (int i = 0; i < (list.size() / slice) + 1; i++) {
			list2.add(new ArrayList<>(list.subList(i * slice, Math.min(list.size(), (i + 1) * slice))));
		}
		return list2;
	}
}
