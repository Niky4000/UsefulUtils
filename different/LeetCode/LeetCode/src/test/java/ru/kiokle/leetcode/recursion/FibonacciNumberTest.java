package ru.kiokle.leetcode.recursion;

import java.math.BigInteger;
import java.util.Date;
import org.junit.Test;

public class FibonacciNumberTest {

    @Test
    public void test() {
        for (long i = 0; i <= 20; i++) {
            long time = new Date().getTime();
            BigInteger digit = new FibonacciNumber().fib(BigInteger.valueOf(i));
            long time2 = new Date().getTime();
            System.out.println(i + " " + (time2 - time) + " " + digit.toString());
        }
        for (long i = 0; i <= 100000; i++) {
            long time = new Date().getTime();
            BigInteger digit = new FibonacciNumber().fib2(BigInteger.valueOf(i));
            long time2 = new Date().getTime();
            System.out.println(i + " " + (time2 - time) + " " + digit.toString());
        }
    }
}
