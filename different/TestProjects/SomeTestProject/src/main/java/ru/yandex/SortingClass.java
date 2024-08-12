/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.yandex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author me
 */
public class SortingClass {

	public static void main(String[] args) {
		System.out.println(new SortingClass().getSquareArray(new int[]{-3, 2, 4, 5}));
	}

//Дан массив целых чисел x длиной N. Массив упорядочен по возрастанию. Написать функцию, которая из этого массива получит массив квадратов чисел, упорядоченный по возрастанию.
//Пример
//[-3, 2, 4] -> [4, 9, 16]
	private List<Integer> getSquareArray(int[] arr) {
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		for (int k : arr) {
			if (k < 0) {
				list.add(k * k);
			} else {
				list2.add(k * k);
			}
		}
		List<Integer> resultList = merge(list, list2);
		return resultList;
	}

//[-3, -2, -1, 2, 4] -> [4, 9, 16]
//
//listNegative = [81,25,1]
//listPositive = [4,16,100,121]
//
//[1,4,16,25,81,100,121]
	private List<Integer> merge(List<Integer> listNegative, List<Integer> listPositive) {
		List<Integer> list = new ArrayList<>(listNegative.size() + listPositive.size());
		int indexOfPositiveList = 0;
		for (int i = listNegative.size() - 1; i >= 0; i--) {
			Integer negativeInt = listNegative.get(i);
			Integer positiveInt = listPositive.size() > indexOfPositiveList ? listPositive.get(indexOfPositiveList) : null;
			if (positiveInt != null) {
				if (negativeInt < positiveInt) {
					list.add(negativeInt);
				} else {
					while (positiveInt != null && negativeInt >= positiveInt) {
						list.add(positiveInt);
						indexOfPositiveList++;
						positiveInt = listPositive.size() > indexOfPositiveList ? listPositive.get(indexOfPositiveList) : null;
					}
					list.add(negativeInt);
				}
			} else {
				list.add(negativeInt);
			}
		}
		for (int k = indexOfPositiveList; k < listPositive.size(); k++) {
			list.add(listPositive.get(k));
		}
		return list;
	}
}
