package com.java.test.questions;

public class Test10 {

    interface One {

        default void method1() {
            System.out.println("One-method1");
        }

        void method2();
    }

    @FunctionalInterface
    interface Two extends One {

        @Override
        default void method1() {
            System.out.println("Two-method1");
        }
    }

    @FunctionalInterface
    interface Three {

        default void method1() {
            System.out.println("Two-method1");
        }

        public void method3();
    }

    public class TwoImpl implements Two {

        @Override
        public void method2() {
            System.out.println("TwoImpl-method2");
        }
    }

    public class ThreeImpl implements Two, Three {

        @Override
        public void method1() {
            Three.super.method1();
        }

        @Override
        public void method2() {
            System.out.println("ThreeImpl-method2");
        }

        @Override
        public void method3() {
            System.out.println("ThreeImpl-method3");
        }
    }
}
