package ru.ibs.pmp.zzztestapplication.bean;

/**
 * @author NAnishhenko
 */
public class TestBean {

    private static int i = 0;
    private static int k = 0;
    private static int m = 0;

    static {
        System.out.println("Hello World from TestBean static section!!!");
        i = 5;
        String gg = null;
        gg.length();
    }

    {
        System.out.println("Hello World from TestBean section!!!");
        k = 8;
        m = 10;
        String gg = null;
        gg.length();
    }

    public static void test2() {
        System.out.println("Hello World from static TestBean.test!");
    }

    public void test() {
        System.out.println("Hello World from TestBean.test!");
    }
}
