/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykayactivationserver;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import static ru.kiokle.marykayactivationserver.Database.table;
import static ru.kiokle.marykaylib.CommonUtil.getPathToSaveFolder;
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
        File pathToSaveFolder = getPathToSaveFolder(StartMaryKayActivationServer.class);
        Database database = new Database(pathToSaveFolder);
        SignThread signThread = new SignThread(pathToSaveFolder, database, queue, queueForSendBack);
        MailReaderThread mailReaderThread = new MailReaderThread(user, password, imapServer, null, null, mailBean -> true, queue);
        MailSenderThread mailSenderThread = new MailSenderThread(userForSending, passwordForSending, properties, fromMail, toMail, queueForSendBack);
        mailReaderThread.start();
        signThread.start();
        mailSenderThread.start();
        System.out.println("Hello World!!!");
    }

    public static void main(String[] args) throws Exception {
        List<String> argList = Arrays.asList(args).stream().collect(Collectors.toList());
        if (argList.contains("-user")) {
            String userName = argList.get(argList.indexOf("-user") + 1);
            File pathToSaveFolder = getPathToSaveFolder(StartMaryKayActivationServer.class);
            Database database = new Database(pathToSaveFolder);
            List<Object[]> name = database.select("select id, user_name from " + table + " where user_name = ?", resultSet -> {
                try {
                    return new Object[]{resultSet.getLong(1), resultSet.getString(2)};
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }, Arrays.asList(statement -> {
                try {
                    statement.setString(1, userName);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }));
            if (name.isEmpty()) {
                database.insert(userName);
            }
            show(database);
        } else if (argList.contains("-delete")) {
            Long id = Long.valueOf(argList.get(argList.indexOf("-delete") + 1));
            File pathToSaveFolder = getPathToSaveFolder(StartMaryKayActivationServer.class);
            Database database = new Database(pathToSaveFolder);
            database.delete(id);
            show(database);
        } else if (argList.contains("-show")) {
            File pathToSaveFolder = getPathToSaveFolder(StartMaryKayActivationServer.class);
            Database database = new Database(pathToSaveFolder);
            show(database);
        } else {
            StartMaryKayActivationServer server = new StartMaryKayActivationServer();
            server.handleMail();
        }
    }

    private static void show(Database database) throws SQLException {
        List<Object[]> select = database.select("select id, user_name, cpu_id from " + table, resultSet -> {
            try {
                return new Object[]{resultSet.getLong(1), resultSet.getString(2), resultSet.getString(3)};
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }, new ArrayList<>(1));
        for (Object[] obj : select) {
            System.out.println("id = " + obj[0] + " name = " + obj[1] + " cpu_id = " + obj[2]);
        }
    }
}
