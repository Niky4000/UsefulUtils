package ru.kiokle.leetcode.memoization;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SortIntegersByThePowerValue {

    public int getKth(int lo, int hi, int k) {
        List<int[]> list = IntStream.rangeClosed(lo, hi).mapToObj(i -> new int[]{i, getPower(i)}).sorted((e1, e2) -> {
            int powerDifference = e1[1] - e2[1];
            if (powerDifference == 0) {
                return e1[0] - e2[0];
            } else {
                return powerDifference;
            }
        }).collect(Collectors.toList());
        return list.get(k - 1)[0];
    }

    private int getPower(int digit) {
        int counter = 0;
        while (digit != 1) {
            if (digit % 2 == 1) {
                digit = digit * 3 + 1;
            } else {
                digit = digit / 2;
            }
            counter++;
        }
        return counter;
    }
}
