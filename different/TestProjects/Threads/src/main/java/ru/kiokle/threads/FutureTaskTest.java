package ru.kiokle.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NAnishhenko
 */
public class FutureTaskTest {

    Exception external;

    public void test() throws InterruptedException, ExecutionException {

        FutureTask<Long> futureTask = new FutureTask<Long>(() -> {
            try {
                Long g=null;
                long longValue = g.longValue();
                Thread.sleep(2000);
                return 20L;
            } catch (Exception ex) {
                external = new RuntimeException("My Exception!", ex);
                boolean interrupted = Thread.interrupted();
                throw external;
            }
        });
        new Thread(futureTask).start();
        int i = 0;
        while (!futureTask.isDone()) {
            Thread.sleep(500);
            i++;
            if (i >= 2) {
                futureTask.cancel(true);
            }
            System.out.println("waiting....");
        }
        System.out.println("--------------");
        Long resultValue = 0L;
        try {
            resultValue = futureTask.get();
        } catch (Exception e) {
            Throwable cause = e.getCause();
            e.printStackTrace();
            System.out.println("--------------");
            System.out.println("--------------");
            System.out.println("--------------");
            System.out.println("--------------");
            external.printStackTrace();
        }
        System.out.println("resultValue = " + resultValue.toString() + "!");
    }
}
