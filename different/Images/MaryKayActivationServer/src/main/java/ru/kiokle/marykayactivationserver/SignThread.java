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

    private final static String schema = "mk";
    private Long id = 0L;
    private final String h2DbLocation;
    private Connection conn;
    private final ArrayBlockingQueue<MailBean> queue;
    private final ArrayBlockingQueue<MailBean> queueForSendBack;
    private final AtomicBoolean interrupted = new AtomicBoolean(false);
    private final Keys keys;
    private final KeyPair keyPair;

    public SignThread(ArrayBlockingQueue<MailBean> queue, ArrayBlockingQueue<MailBean> queueForSendBack) throws Exception {
        File pathToSaveFolder = getPathToSaveFolder(SignThread.class);
        keys = new Keys(pathToSaveFolder);
        keyPair = keys.loadKeysFromResources(SignThread.class);
        File dirFile = new File(pathToSaveFolder.getParentFile().getAbsolutePath() + s + "db");
        this.queue = queue;
        this.queueForSendBack = queueForSendBack;
        h2DbLocation = dirFile.getAbsolutePath().replace("\\", "/") + "mary_kay_users";
        System.setProperty("h2.db.location", h2DbLocation);
        boolean dbExisted = new File(h2DbLocation + ".mv.db").exists();
        try {
            conn = DriverManager.getConnection("jdbc:h2:file:" + h2DbLocation + ";DB_CLOSE_DELAY=-1;MODE=Oracle;TRACE_LEVEL_SYSTEM_OUT=1;INIT=CREATE SCHEMA IF NOT EXISTS " + schema + "\\;SET SCHEMA " + schema + "", "sa", "");
        } catch (Exception ex) {
            Logger.getLogger(SignThread.class.getName()).log(Level.SEVERE, null, ex);
            closeConnection();
            throw new RuntimeException(ex);
        }
        try {
            if (dbExisted) {
                try (PreparedStatement prepareStatement = conn.prepareStatement("select max(u.id) as max_ from " + schema + ".users u")) {
                    ResultSet executeQuery = prepareStatement.executeQuery();
                    if (executeQuery.next()) {
                        id = executeQuery.getLong("max_");
                        id++;
                    }
                    executeQuery.close();
                }
            } else {
                createTable("CREATE TABLE " + schema + ".users(id bigint primary key, user_name varchar2(128), cpu_id varchar2(128), created date)");
            }
        } catch (Exception ex) {
            Logger.getLogger(SignThread.class.getName()).log(Level.SEVERE, null, ex);
            closeConnection();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                MailBean mailBean = queue.take();
                Properties data = new Properties();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mailBean.getText().getBytes());
                data.load(byteArrayInputStream);
                final String userName = (String) data.get(USER_NAME);
                final String cpuId = (String) data.get(CPU_ID);
                List<Object[]> select = select("select id,cpu_id from users where user_name = ?", resultSet -> {
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
                        mailBeanForSend = new MailBean(null, null, mailBean.getSubject(), ACCEPTED + "=false\n" + REASON + "=Данная программа уже зарегистрирована на другом компьютере");
                    } else {
                        String stringToSign = userName + "_" + cpuId;
                        String sign = keys.sign(stringToSign, keyPair.getPrivate());
                        update(rowId, cpuId);
                        mailBeanForSend = new MailBean(null, null, mailBean.getSubject(), ACCEPTED + "=true\"" + SIGN + "=" + sign);
                    }
                } else {
                    mailBeanForSend = new MailBean(null, null, mailBean.getSubject(), ACCEPTED + "=false\n" + REASON + "=Неверные регистрационные данные");
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

    public <T> List<T> select(String selectClause, Function<ResultSet, T> mapper, List<Consumer<PreparedStatement>> whereParameters) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectClause)) {
            whereParameters.forEach(obj -> obj.accept(stmt));
            List<T> list = new ArrayList<>();
            ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                list.add(mapper.apply(rset));
            }
            return list;
        }
    }

    public void update(Long id, String cpuId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("update users set cpu_id = ?, created = ? where id = ?")) {
            stmt.setString(1, cpuId);
            stmt.setObject(2, new java.sql.Timestamp(new Date().getTime()));
            stmt.setLong(3, id);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    private void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SignThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createTable(String createSQLQuery) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.execute(createSQLQuery);
            conn.commit();
        } catch (Exception ex) {
            try {
                conn.rollback();
            } catch (Exception ee) {
                throw new RuntimeException("statement close Fatal Error!", ex);
            }
            Logger.getLogger(SignThread.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("createTable Fatal Error!", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ee) {
                    throw new RuntimeException("statement close Fatal Error!", ee);
                }
            }
        }
    }

    public void interruptThisThread() {
        this.interrupted.set(true);
    }
}
