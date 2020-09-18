/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
import javax.mail.MessagingException;
import ru.kiokle.marykaylib.bean.MailBean;

/**
 *
 * @author me
 */
public interface MailHandler {

    int QUEUE_SIZE = 1024;

    void readEmail(Properties properties, String mail, String user, String password);

    void readYandexMailbox(String login, String password, String imapServer, Predicate<MailBean> deleteCondition, ArrayBlockingQueue<MailBean> queue) throws MessagingException, IOException;

    void sendMail(Properties properties, String mail, String user, String password, String sendMail);

    void sendMail2(Properties properties, String mail, String user, String password, String sendMail, MailBean mailBean);
    
}
