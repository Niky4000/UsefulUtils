/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.yandex.empty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author me
 */
public class CustomThreadPool {

    private final int poolSize;
    private final LinkedList<Runnable> taskQueue;
    private final List<Worker> workerPool;
    private volatile boolean isShutdown = false;
    private CountDownLatch latch;

    public CustomThreadPool(int poolSize) {
        this.poolSize = poolSize;
        this.taskQueue = new LinkedList();
        this.workerPool = new ArrayList<>();
        latch = new CountDownLatch(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker("Worker-" + i);
            workerPool.add(worker);
            worker.start();
        }
    }

    public void execute(Runnable task) {
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notify();
        }
    }

    public void shutdown() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ie) {
//                    ie.printStackTrace();
        }
        isShutdown = true;
        synchronized (taskQueue) {
            for (Worker r : workerPool) {
                r.interrupt();
            }
            taskQueue.notifyAll();
        }
    }

    public void awaitTermination() {
        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }
        try {
            latch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private class Worker extends Thread {

        public Worker(String name) {
            super(name);
        }

        @Override
        public void run() {
            boolean shuttedDown = false;
            while (true) {
                Runnable runnableTask;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() && !isShutdown) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException ex) {
//                            ex.printStackTrace();
                        }
                    }
                    runnableTask = taskQueue.pollFirst();
                }
                if (runnableTask != null) {
                    runnableTask.run();
                }
                if (isShutdown && !shuttedDown) {
                    latch.countDown();
                    shuttedDown = true;
                    System.out.println(Thread.currentThread().getName() + " finished!");
                    break;
                }
            }
        }
    }

    public static void start() {
        CustomThreadPool pool = new CustomThreadPool(3);
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " executing task " + taskId);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
//                    ie.printStackTrace();
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination();
        System.out.println("All tasks completed and pool terminated!");
    }

    static class Some<T extends Number> {

        List<T> list = new ArrayList();

        public void add(T n) {
            list.add(n);
        }

        public List<T> getList() {
            return list;
        }
    }

    static class A {
    }

    static class B extends A {
    }

    static class C {
    }

    private void some() {
        Some<Number> some = new Some<Number>();
        some.add(8L);
        some.add(BigDecimal.ZERO);
        List<Number> list = some.getList();
        List<Number> list2 = new ArrayList<>();
        list2.add(8L);
        List<Collection> list3 = new ArrayList<>();
        list3.add(new HashSet());
        list3.add(new LinkedList());
        List<A> list4 = new ArrayList<>();
        list4.add(new B());
        someMethod(list4);
        List<B> list5 = new ArrayList<>();
        list5.add(new B());
        someMethod(list5);
        int k1 = 88888;
        int k2 = 88884 + 4;
        boolean b = k1 == k2;
        long l1 = 88888888L;
        long l2 = 88888884L + 4L;
        boolean b2 = l1 == l2;
        Integer kk1 = 127;
        Integer kk2 = 125 + 2;
        boolean b3 = kk1 == kk2;
        Integer kkk1 = 128;
        Integer kkk2 = 126 + 2;
        boolean b4 = kkk1 == kkk2;
        FutureTask<String> task = new FutureTask<>(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            throw new RuntimeException();
//            return "Hello World!";
        });
        Thread thread = new Thread(task);
        thread.start();
        try {
            String result = task.get();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Thread thread = new Thread(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException ie) {
//                ie.printStackTrace();
//            }
//            throw new RuntimeException();
//        });
//        thread.start();
//        thread.join();
        System.out.println("Hello World!!!");
    }

    static void someMethod(List<? extends A> list) {
    }
}
