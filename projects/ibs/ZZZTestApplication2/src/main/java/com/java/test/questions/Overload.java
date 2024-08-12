package com.java.test.questions;

import java.util.Arrays;
import java.util.List;

public class Overload {

    public void method(Object o) {
        System.out.println("Object");
    }

    public void method(java.io.FileNotFoundException f) {
        System.out.println("FileNotFoundException");
    }

    public void method(java.io.IOException i) {
        System.out.println("IOException");
    }

//    public void method(String i) {
//        System.out.println(i);
//    }
    public static void call() {
        Overload test = new Overload();
        test.method(null);
    }

    public static void call2() {
        List<StringBuilder> list = Arrays.asList(new StringBuilder("Java "), new StringBuilder("Hello "));
        list.stream().map((x) -> x.append("World  "));
        list.forEach(System.out::print);
    }
}
