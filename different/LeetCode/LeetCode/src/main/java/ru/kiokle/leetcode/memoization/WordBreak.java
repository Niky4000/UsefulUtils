package ru.kiokle.leetcode.memoization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordBreak {

    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> set = new HashSet<>(wordDict);
        char[] charArray = s.toCharArray();
        int[] byteArray = new int[charArray.length + 1];
        Arrays.fill(byteArray, -1);
        return checkArray(charArray, set, 0, byteArray);
    }

    private boolean checkArray(char[] charArray, Set<String> set, int startingIndex, int[] byteArray) {
        if (startingIndex == charArray.length) {
            byteArray[startingIndex] = 1;
            return true;
        }
        if (byteArray[startingIndex] != -1) {
            return byteArray[startingIndex] == 1;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = startingIndex; i < charArray.length; i++) {
            sb.append(charArray[i]);
            if (set.contains(sb.toString()) && byteArray[startingIndex] == -1 && checkArray(charArray, set, i + 1, byteArray)) {
                byteArray[startingIndex] = 1;
                return true;
            }
        }
        byteArray[startingIndex] = 0;
        return false;
    }

    private static int[] dp;

    public boolean wordBreak2(String s, List<String> wordDict) {
        Set<String> set = new HashSet<>(wordDict);
        dp = new int[s.length()];
        Arrays.fill(dp, -1);
        return solve(set, s.toCharArray(), 0) == 1 ? true : false;
    }

    private static int solve(Set<String> set, char[] s, int ptr) {
        if (ptr == s.length) {
            return 1;
        }
        if (dp[ptr] != -1) {
            return dp[ptr];
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = ptr; i < s.length; i++) {
            sb.append(String.valueOf(s[i]));
            if (set.contains(sb.toString())) {
                if (solve(set, s, i + 1) == 1) {
                    return dp[i] = 1;
                }
            }
        }
        return dp[ptr] = 0;
    }
}
