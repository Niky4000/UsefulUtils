package ru.kiokle.leetcode.memoization;

import java.util.List;
import org.junit.Test;

public class DifferentWaysToAddParenthesesTest {

    @Test
    public void test() throws InterruptedException {
        List<Integer> diffWaysToCompute2 = new DifferentWaysToAddParentheses().diffWaysToCompute2("12*2+8+4");
        System.out.println(diffWaysToCompute2);
//        List<String> out = new ArrayList<>();
//        new DifferentWaysToAddParentheses().getAllCombinations(Arrays.asList(1, 2, 3), Arrays.asList("+", "-"), new ArrayList<>(), out, 2, 2, 0);
//        System.out.println(out);
    }
}
