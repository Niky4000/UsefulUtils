package ru.kiokle.leetcode.memoization;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class DifferentWaysToAddParentheses {

    public List<Integer> diffWaysToCompute2(String expression) {
        List<Integer> res = new LinkedList<>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '-' || c == '+' || c == '*') {
                List<Integer> left = diffWaysToCompute2(expression.substring(0, i));
                List<Integer> right = diffWaysToCompute2(expression.substring(i + 1));
                for (int a : left) {
                    for (int b : right) {
                        if (c == '-') {
                            res.add(a - b);
                        } else if (c == '+') {
                            res.add(a + b);
                        } else {
                            res.add(a * b);
                        }
                    }
                }
            }
        }
        if (res.isEmpty()) {
            res.add(Integer.parseInt(expression));
        }
        return res;
    }

    public List<Integer> diffWaysToCompute(String expression) {
        BiFunction<Integer, Integer, Integer> minus = (d1, d2) -> d1 - d2;
        BiFunction<Integer, Integer, Integer> plus = (d1, d2) -> d1 + d2;
        BiFunction<Integer, Integer, Integer> mul = (d1, d2) -> d1 * d2;
        List<Integer> integerList = new ArrayList<>();
        List<BiFunction<Integer, Integer, Integer>> operationList = new ArrayList<>();
        return null;
    }

    public void getAllCombinations(List<Integer> digits, List<String> operationList, List<String> temp, List<String> out, int left, int right, int index) {
        if (left < 0 || right < 0) {
            return;
        }
        if (left == 0 && right == 0) {
            out.add(temp.stream().reduce("", (s1, s2) -> s1 + s2));
        }
        if (left > 0) {
            if (temp.isEmpty()) {
                temp.add(digits.get(index * 2) + operationList.get(index) + digits.get(index * 2 + 1));
            } else if (temp.size() > index) {
                temp.set(index, operationList.get(index) + digits.get(index + 1));
            } else if (index + 1 < digits.size()) {
                temp.add(operationList.get(index) + digits.get(index + 1));
            }
            getAllCombinations(digits, operationList, temp, out, left - 1, right, index + 1);
        }
        if (right > left) {
            temp.set(index - 1, temp.get(index - 1) + digits.get(index));
            getAllCombinations(digits, operationList, temp, out, left, right - 1, index + 1);
        }
    }
}
