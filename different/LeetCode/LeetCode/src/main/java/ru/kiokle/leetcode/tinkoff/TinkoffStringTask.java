package ru.kiokle.leetcode.tinkoff;

public class TinkoffStringTask {

    public int solve(String str) {
        String[] splitedString = str.split("0");
        int max = 0;
        int currentValue = 0;
        int i = 0;
        while (i < splitedString.length) {
            if (splitedString[i].length() > 0) {
                currentValue = splitedString[i].length();
                if (i + 1 < splitedString.length && splitedString[i + 1].length() > 0) {
                    currentValue += splitedString[i + 1].length();
                }
                max = currentValue > max ? currentValue : max;
            } else {
                currentValue = 0;
            }
            i++;
        }
        return max;
    }
}
