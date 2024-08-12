package ru.ibs.pmp.zzztestapplication.threads;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NAnishhenko
 */
public class TreadTest extends Thread {

    Thread extThread;

    public TreadTest(Thread extThread) {
        this.setDaemon(true);
        this.extThread = extThread;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(4 * 1000);
                printStackTrace(extThread.getStackTrace());
            }
        } catch (InterruptedException ex) {
//                Logger.getLogger(TreadTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println();
            System.out.println();
            System.out.println("Interrapted!!!");
            System.out.println();
            System.out.println();
            printStackTrace(extThread.getStackTrace());
        }
    }

    private void printStackTrace(StackTraceElement[] stackTrace) {
        Arrays.stream(stackTrace).forEach(el -> {
            System.out.println(el.toString());
        });
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }
}
