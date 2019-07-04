package ru.ibs.pmp.zzztestapplication.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author NAnishhenko
 */
public class TreadTest2 {

    public static void testService() {
        ExecutorService service = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 10; i++) {
            final int k = i;
            service.execute(new Thread(() -> {
                boolean interrapted = false;
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                    System.out.println("Interrapted! " + k + " !");
                    interrapted = true;
                }
                if (!interrapted) {
                    System.out.println("Hello! " + k + " !");
                } else {
                    try {
                        Thread.sleep(16000);
                    } catch (InterruptedException ex) {
                        System.out.println("Interrapted in any case! " + k + " !");
                        interrapted = true;
                    }
                    System.out.println("Delayed! " + k + " !");
                }
            }));
            System.out.println("Executed! " + k + " !");
        }
        try {
            Thread.sleep(7000);
        } catch (InterruptedException ex) {
            System.out.println("Main Thread Interrapted!");
        }
        service.shutdown();
    }
}
