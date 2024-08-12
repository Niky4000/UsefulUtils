package ru.kiokle.threads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NAnishhenko
 */
public class CompletebleFutureTest {

    public void test() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture<Long> supplyAsync = CompletableFuture.supplyAsync(() -> calculate(), executorService);
        Long join = supplyAsync.join();
        System.out.println(join.toString());
        executorService.shutdown();
    }

    private Long calculate() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CompletebleFutureTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 10L;
    }
}
