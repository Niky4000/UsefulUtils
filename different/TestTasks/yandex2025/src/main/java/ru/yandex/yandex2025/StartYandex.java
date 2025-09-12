package ru.yandex.yandex2025;

/**
 *
 * @author me
 */
public class StartYandex {

    public static void main(String[] args) {
        new StartYandex().printSomething();
    }

    public boolean printSomething() {
        System.out.println("Hello World!!!");
        printSomethingElse();
        return true;
    }

    boolean printSomethingElse() {
        System.out.println("Hello World Again!!!");
        return false;
    }
}
