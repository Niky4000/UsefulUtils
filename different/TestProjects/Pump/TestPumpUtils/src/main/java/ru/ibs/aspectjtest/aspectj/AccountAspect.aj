package org.baeldung.aspectj;

import ru.ibs.aspectjtest.TestClass;

public aspect AccountAspect {

    pointcut callInterception(): call(public * TestClass.*(..));

    before() : callInterception() {
        System.out.println("Before: Hello World from aspect!");
    }

    after() : callInterception() {
        System.out.println("After: Hello World from aspect!");
    }
}
