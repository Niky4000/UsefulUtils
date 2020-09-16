/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykayactivationserver;

import java.io.IOException;
import java.util.Properties;
import javax.mail.MessagingException;
import ru.kiokle.marykaylib.MailHandler;

/**
 *
 * @author me
 */
public class StartMaryKayActivationServer {

    private final Properties properties2 = new Properties();
    private final String user;
    private final String password;
    private final String imapServer;

    public StartMaryKayActivationServer() {
        try {
            properties2.load(getClass().getClassLoader().getResourceAsStream("mailConfig3.conf"));
            user = properties2.getProperty("mail.read2.user");
            password = properties2.getProperty("mail.read2.password");
            imapServer = properties2.getProperty("mail.read2.imap.server");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void readMail() throws MessagingException, IOException {
        MailHandler mailHandler = new MailHandler();
        mailHandler.readYandexMailbox(user, password, imapServer);
//        mailHandler.readEmail();
        System.out.println("Hello World!!!");
    }

    public static void main(String[] args) throws Exception {
        StartMaryKayActivationServer server = new StartMaryKayActivationServer();
        server.readMail();
    }
}
