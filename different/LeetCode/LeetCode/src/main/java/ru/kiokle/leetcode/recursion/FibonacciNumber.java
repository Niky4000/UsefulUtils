package ru.kiokle.leetcode.recursion;

import java.math.BigInteger;

public class FibonacciNumber {

    public int fib(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return fib(n - 1) + fib(n - 2);
        }
    }

    public int fib2(int n) {
        if (n <= 1) {
            return 1;
        } else {
            int[] arr = new int[]{1, 1};
            for (int i = 0; i < n - 1; i++) {
                int fib = arr[0] + arr[1];
                arr[0] = arr[1];
                arr[1] = fib;
            }
            return arr[1];
        }
    }

    public BigInteger fib(BigInteger n) {
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return BigInteger.ONE;
        } else {
            return fib(n.add(BigInteger.ONE.negate())).add(fib(n.add(BigInteger.TWO.negate())));
        }
    }

    public BigInteger fib2(BigInteger n) {
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return BigInteger.ONE;
        } else {
            BigInteger[] arr = new BigInteger[]{BigInteger.ONE, BigInteger.ONE};
            for (BigInteger i = BigInteger.ZERO; i.compareTo(n.add(BigInteger.ONE.negate())) < 0; i = i.add(BigInteger.ONE)) {
                BigInteger fib = arr[0].add(arr[1]);
                arr[0] = arr[1];
                arr[1] = fib;
            }
            return arr[1];
        }
    }
}
