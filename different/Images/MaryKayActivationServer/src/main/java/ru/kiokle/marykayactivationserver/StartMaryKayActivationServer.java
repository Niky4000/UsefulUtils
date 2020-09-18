/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykayactivationserver;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import javax.mail.MessagingException;
import static ru.kiokle.marykaylib.MailHandlerImpl.QUEUE_SIZE;
import ru.kiokle.marykaylib.bean.MailBean;
import ru.kiokle.marykaylib.threads.MailReaderThread;
import ru.kiokle.marykaylib.threads.MailSenderThread;

/**
 *
 * @author me
 */
public class StartMaryKayActivationServer {

    private final Properties properties = new Properties();
    private final Properties properties2 = new Properties();
    private final String user;
    private final String password;
    private final String imapServer;
    private final String userForSending;
    private final String passwordForSending;
    private final String fromMail;
    private final String toMail;

    public StartMaryKayActivationServer() {
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("mailConfig.conf"));
            properties2.load(getClass().getClassLoader().getResourceAsStream("mailConfig3.conf"));
            user = properties2.getProperty("mail.read2.user");
            password = properties2.getProperty("mail.read2.password");
            imapServer = properties2.getProperty("mail.read2.imap.server");
            userForSending = properties2.getProperty("mail.send2.user");
            passwordForSending = properties2.getProperty("mail.send2.password");
            fromMail = properties2.getProperty("mail.send2.box");
            toMail = properties2.getProperty("mail.box2.for.sending");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void handleMail() throws MessagingException, IOException, Exception {
        ArrayBlockingQueue<MailBean> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ArrayBlockingQueue<MailBean> queueForSendBack = new ArrayBlockingQueue<>(QUEUE_SIZE);
        SignThread signThread = new SignThread(queue, queueForSendBack);
        MailReaderThread mailReaderThread = new MailReaderThread(user, password, imapServer, mailBean -> true, queue);
        MailSenderThread mailSenderThread = new MailSenderThread(userForSending, passwordForSending, properties, fromMail, toMail, queueForSendBack);
        mailReaderThread.start();
        signThread.start();
        mailSenderThread.start();
        System.out.println("Hello World!!!");
    }

    public static void main(String[] args) throws Exception {
        StartMaryKayActivationServer server = new StartMaryKayActivationServer();
        server.handleMail();
    }
}
