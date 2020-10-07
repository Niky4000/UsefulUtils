/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykayactivationserver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.KeyPair;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import static ru.kiokle.marykayactivationserver.Database.table;
import static ru.kiokle.marykaylib.CommonUtil.decodeValue;
import static ru.kiokle.marykaylib.CommonUtil.encodeValue;
import static ru.kiokle.marykaylib.CommonUtil.getPathToSaveFolder;
import static ru.kiokle.marykaylib.CommonUtil.s;
import ru.kiokle.marykaylib.Keys;
import ru.kiokle.marykaylib.bean.MailBean;
import static ru.kiokle.marykaylib.bean.MailBean.ACCEPTED;
import static ru.kiokle.marykaylib.bean.MailBean.CPU_ID;
import static ru.kiokle.marykaylib.bean.MailBean.REASON;
import static ru.kiokle.marykaylib.bean.MailBean.SIGN;
import static ru.kiokle.marykaylib.bean.MailBean.USER_NAME;

/**
 *
 * @author me
 */
public class SignThread extends Thread {

    private final ArrayBlockingQueue<MailBean> queue;
    private final ArrayBlockingQueue<MailBean> queueForSendBack;
    private final AtomicBoolean interrupted = new AtomicBoolean(false);
    private final Keys keys;
    private final KeyPair keyPair;
    private final Database database;

    public SignThread(File pathToSaveFolder, Database database, ArrayBlockingQueue<MailBean> queue, ArrayBlockingQueue<MailBean> queueForSendBack) throws Exception {
        super("SignThread");
        this.queue = queue;
        this.queueForSendBack = queueForSendBack;
        keys = new Keys(pathToSaveFolder);
        keyPair = keys.loadKeysFromResources(SignThread.class);
        this.database = database;
    }

    @Override
    public void run() {
        while (true) {
            try {
                MailBean mailBean = queue.take();
                Properties data = new Properties();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodeValue(mailBean.getText()).getBytes());
                data.load(byteArrayInputStream);
                final String userName = (String) data.get(USER_NAME);
                final String cpuId = (String) data.get(CPU_ID);
                if (cpuId == null) {
                    continue;
                }
                List<Object[]> select = database.select("select id,cpu_id from " + table + " where user_name = ?", resultSet -> {
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
                MailBean mailBeanForSend;
                if (select.size() == 1) {
                    Long rowId = (Long) select.get(0)[0];
                    String cpuIdFromTable = (String) select.get(0)[1];
                    if (cpuIdFromTable != null && !cpuIdFromTable.equals(cpuId)) {
                        mailBeanForSend = new MailBean(null, null, cpuIdFromTable, false, mailBean.getSubject(), encodeValue(ACCEPTED + "=false\n" + REASON + "=Данная программа уже зарегистрирована на другом компьютере"));
                    } else {
                        String stringToSign = cpuId + "_" + userName;
                        String sign = keys.sign(stringToSign, keyPair.getPrivate());
                        database.update(rowId, cpuId);
                        mailBeanForSend = new MailBean(null, null, cpuIdFromTable, false, mailBean.getSubject(), encodeValue(ACCEPTED + "=true\n" + SIGN + "=" + sign));
                    }
                } else {
                    mailBeanForSend = new MailBean(null, null, cpuId, false, mailBean.getSubject(), encodeValue(ACCEPTED + "=false\n" + REASON + "=Неверные регистрационные данные"));
                }
                queueForSendBack.offer(mailBeanForSend);
            } catch (InterruptedException ie) {
                if (interrupted.get()) {
                    break;
                } else {
                    Logger.getLogger(SignThread.class.getName()).log(Level.SEVERE, null, ie);
                }
            } catch (Exception ex) {
                Logger.getLogger(SignThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void interruptThisThread() {
        this.interrupted.set(true);
    }
}
