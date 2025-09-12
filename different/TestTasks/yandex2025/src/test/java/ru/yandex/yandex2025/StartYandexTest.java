package ru.yandex.yandex2025;

import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class StartYandexTest {

    StartYandex startYandex;

    @Test
    public void testPrintSomething() {
        StartYandex startYandex = spy(StartYandex.class);
        doReturn(true).when(startYandex).printSomethingElse();
        boolean result = startYandex.printSomething();
        Assert.assertTrue(result);
        verify(startYandex).printSomething();
        verify(startYandex).printSomethingElse();
        verifyNoMoreInteractions(startYandex);
    }
}
