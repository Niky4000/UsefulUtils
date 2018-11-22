package ru.kiokle.module.automatic.formatter.debug;

import java.text.ParseException;

public class ClassForTest {

    public void testDebugException(boolean b) throws ParseException, RuntimeException {
        String hello = (String) gg();
    }

    private Object gg() {
        return "fg";
    }
}
