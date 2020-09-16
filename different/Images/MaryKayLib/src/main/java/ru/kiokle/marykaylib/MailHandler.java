/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib;

import com.sun.mail.imap.IMAPMessage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

/**
 *
 * @author me
 */
public class MailHandler {

//    private final Properties properties = new Properties();
//    private final Properties properties2 = new Properties();
    public MailHandler() {
//        try {
//            properties.load(getClass().getClassLoader().getResourceAsStream("mailConfig.conf"));
//            properties2.load(getClass().getClassLoader().getResourceAsStream("mailConfig2.conf"));
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
    }

    public void sendMail(Properties properties, String mail, String user, String password, String sendMail) {
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendMail));
            message.setSubject("Mail Subject");
            String msg = "This is my first email using JavaMailer";
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMail2(Properties properties, String mail, String user, String password, String sendMail) {
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendMail));
            message.setSubject("Subject");
            message.setText("Text");
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readYandexMailbox(String login, String password, String imapServer) throws MessagingException, IOException {
        Properties props = new Properties();
//        props.setProperty("mail.store.protocol", "imaps");
//        props.setProperty("mail.imaps.ssl.trust", "imap.yandex.ru");
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.ssl.trust", imapServer);
        Session session = Session.getDefaultInstance(props, null);
        Store store;
        store = session.getStore();
//        store.connect("imap.yandex.ru", 993, login, password);
        store.connect(imapServer, 993, login, password);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE); // even though we only want to read, write persmission will allow us to set SEEN flag
        try {
            int messageCount = inbox.getMessageCount();
            for (int i = 0; i < messageCount; i++) {
                Message lastMessage = inbox.getMessage(inbox.getMessageCount());
                ((IMAPMessage) lastMessage).setPeek(true); // this is how you prevent automatic SEEN flag
                MimeMessage cmsg = new MimeMessage((MimeMessage) lastMessage); // this is how you deal with exception "Unable to load BODYSTRUCTURE"
                printMessage(cmsg);
                markMessageAsSeen(lastMessage, inbox);
                deleteMessage(lastMessage, inbox);

                FlagTerm unreadFlag = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
                Message unreadMessages[] = inbox.search(unreadFlag);
                for (Message unreadMessage : unreadMessages) {
                    // SEEN flag here will be set automatically.
                    printMessage(unreadMessage);
                }
            }
        } finally {
            inbox.close(true);
        }
    }

    private void printMessage(Message message) throws MessagingException, IOException {
        Address[] addresses = message.getFrom();
        System.out.println("Received Date:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getReceivedDate()));
        for (Address address : addresses) {
            System.out.println("FROM: " + address.toString());
        }
        System.out.println("SUBJECT:" + message.getSubject());
        System.out.println("TEXT:" + getText(message).trim());
    }

    private void markMessageAsSeen(Message message, Folder folder) throws MessagingException {
        folder.setFlags(new Message[]{message}, new Flags(Flags.Flag.SEEN), true);
    }

    private void deleteMessage(Message message, Folder folder) throws MessagingException {
        folder.setFlags(new Message[]{message}, new Flags(Flags.Flag.DELETED), true);
    }

    private boolean textIsHtml = false; // if test is html, use html parser (e.g. org.jsoup)

// copy-paste from http://www.oracle.com/technetwork/java/javamail/faq/index.html#mainbody
    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }
        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getText(bp);
                    }
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null) {
                        return s;
                    }
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null) {
                    return s;
                }
            }
        }
        return null;
    }

    public void readEmail(Properties properties, String mail, String user, String password) {
//        Properties properties = new Properties();
//        properties.put("mail.debug", "false");
//        properties.put("mail.store.protocol", "imaps");
//        properties.put("mail.imap.ssl.enable", "true");
//        properties.put("mail.imap.port", 993);

//        Authenticator auth = new EmailAuthenticator(IMAP_AUTH_EMAIL,
//                                                    IMAP_AUTH_PWD);
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        session.setDebug(false);
        try {
            Store store = session.getStore();

            // Подключение к почтовому серверу
//            store.connect(IMAP_Server, IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);
            store.connect("imap.yandex.ru", 993, user, password);

            // Папка входящих сообщений
            Folder inbox = store.getFolder("INBOX");

            // Открываем папку в режиме только для чтения
            inbox.open(Folder.READ_ONLY);

            System.out.println("Количество сообщений : "
                    + String.valueOf(inbox.getMessageCount()));
            if (inbox.getMessageCount() == 0) {
                return;
            }
            // Последнее сообщение; первое сообщение под номером 1
            Message message = inbox.getMessage(inbox.getMessageCount());
            Multipart mp = (Multipart) message.getContent();
            // Вывод содержимого в консоль
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                if (bp.getFileName() == null) {
                    System.out.println("    " + i + ". сообщение : '"
                            + bp.getContent() + "'");
                } else {
                    System.out.println("    " + i + ". файл : '"
                            + bp.getFileName() + "'");
                }
            }
        } catch (NoSuchProviderException e) {
            System.err.println(e.getMessage());
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
