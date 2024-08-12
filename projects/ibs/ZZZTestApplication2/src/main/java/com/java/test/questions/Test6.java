package com.java.test.questions;

public class Test6 {

    static void perform() {
        System.out.println("perform");
    }

    private Test6 x;

    public static void call() {
//        x.perform();
        Test6 xx = new Test6();
        xx.perform();
    }

    public static void say(int digit) {
        System.out.println(digit + " ----");
        switch (digit) {
            case 1:
                System.out.println("ONE ");
                break;
            case 2:
                System.out.println("TWO ");
            case 3:
                System.out.println("THREE ");
            default:
                System.out.println("Unknown value ");
        }
    }
}
