/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib.threads;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import ru.kiokle.marykaylib.MailHandler;
import ru.kiokle.marykaylib.MailHandlerFactory;
import ru.kiokle.marykaylib.bean.MailBean;

/**
 *
 * @author me
 */
public class MailReaderThread extends Thread {

    private final String user;
    private final String password;
    private final String imapServer;
    private final MailHandler mailHandler;
    private final ArrayBlockingQueue<MailBean> queue;
    private final Predicate<MailBean> deleteCondition;
    private final AtomicBoolean interrupted = new AtomicBoolean(false);
    private static final int TIME_TO_WAIT = 20 * 1000;

    public MailReaderThread(String user, String password, String imapServer, Predicate<MailBean> deleteCondition, ArrayBlockingQueue<MailBean> queue) {
        this.user = user;
        this.password = password;
        this.imapServer = imapServer;
        this.queue = queue;
        this.deleteCondition = deleteCondition;
        mailHandler = MailHandlerFactory.createInstance();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(TIME_TO_WAIT);
                readMail();
            } catch (InterruptedException ie) {
                if (interrupted.get()) {
                    break;
                } else {
                    Logger.getLogger(MailSenderThread.class.getName()).log(Level.SEVERE, null, ie);
                }
            } catch (Exception ex) {
                Logger.getLogger(MailReaderThread.class.getName()).log(Level.SEVERE, null, ex);
                queue.offer(new MailBean(null, null, null, null));
            }
        }
    }

    public void readMail() throws IOException, MessagingException {
        mailHandler.readYandexMailbox(user, password, imapServer, deleteCondition, queue);
    }

    public void interruptThisThread() {
        this.interrupted.set(true);
    }
}
