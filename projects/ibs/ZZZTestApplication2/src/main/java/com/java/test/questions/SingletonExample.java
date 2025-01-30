package com.java.test.questions;

public class SingletonExample {

    private static volatile SingletonExample SELF;

    public static SingletonExample getInstance() {
        if (SELF == null) { // volatile read
            synchronized (SingletonExample.class) {
                if (SELF == null) { // Проверяем ещё раз, так как другой поток уже мог проверить первый if и заблокироваться на synchronized!
                    SELF = new SingletonExample();
                }
            }
        }
        return SELF;
    }
}
