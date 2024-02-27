package com.java.test.questions;

public class Test1 {

    private int id;

    public Test1(int i) {
        id = i;
    }

    public static boolean test(Test1 t, int id) {
        return t.id == id;
    }
}
