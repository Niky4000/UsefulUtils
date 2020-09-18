/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib.threads;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.kiokle.marykaylib.MailHandler;
import ru.kiokle.marykaylib.MailHandlerFactory;
import ru.kiokle.marykaylib.bean.MailBean;

/**
 *
 * @author me
 */
public class MailSenderThread extends Thread {

    private final String user;
    private final String password;
    private final Properties properties;
    private final String mail;
    private final String sendMail;
    private final MailHandler mailHandler;
    private final ArrayBlockingQueue<MailBean> queue;
    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    public MailSenderThread(String user, String password, Properties properties, String mail, String sendMail, ArrayBlockingQueue<MailBean> queue) {
        this.user = user;
        this.password = password;
        this.properties = properties;
        this.mail = mail;
        this.sendMail = sendMail;
        this.queue = queue;
        mailHandler = MailHandlerFactory.createInstance();
    }

    @Override
    public void run() {
        while (true) {
            try {
                MailBean mailBean = queue.take();
                sendMail(mailBean);
            } catch (InterruptedException ie) {
                if (interrupted.get()) {
                    break;
                } else {
                    Logger.getLogger(MailSenderThread.class.getName()).log(Level.SEVERE, null, ie);
                }
            } catch (Exception ex) {
                Logger.getLogger(MailSenderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendMail(MailBean mailBean) {
        mailHandler.sendMail2(properties, mail, user, password, sendMail, mailBean);
    }

    public void interruptThisThread() {
        this.interrupted.set(true);
    }
}
