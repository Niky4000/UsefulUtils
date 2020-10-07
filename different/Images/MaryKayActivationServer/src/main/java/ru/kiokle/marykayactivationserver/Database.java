/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykayactivationserver;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import static ru.kiokle.marykaylib.CommonUtil.s;

/**
 *
 * @author me
 */
public class Database {

    private String h2DbLocation;
    private Connection conn;
    private final static String schema = "mk";
    public final static String table = "users";
    private AtomicLong id = new AtomicLong(0L);

    public Database(File pathToSaveFolder) {
        this.h2DbLocation = h2DbLocation;
        File dirFile = new File(pathToSaveFolder.getParentFile().getAbsolutePath() + s + "db");
        dirFile.mkdirs();
        h2DbLocation = dirFile.getAbsolutePath().replace("\\", "/") + s + "mary_kay_users";
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
                try (PreparedStatement prepareStatement = conn.prepareStatement("select max(u.id) as max_ from " + schema + "." + table + " u")) {
                    ResultSet executeQuery = prepareStatement.executeQuery();
                    if (executeQuery.next()) {
                        id.set(executeQuery.getLong("max_"));
                    }
                    executeQuery.close();
                }
            } else {
                createTable("CREATE TABLE " + schema + "." + table + "(id bigint primary key, user_name varchar2(128), cpu_id varchar2(128), created date)");
            }
        } catch (Exception ex) {
            Logger.getLogger(SignThread.class.getName()).log(Level.SEVERE, null, ex);
            closeConnection();
            throw new RuntimeException(ex);
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
        try (PreparedStatement stmt = conn.prepareStatement("update " + table + " set cpu_id = ?, created = ? where id = ?")) {
            stmt.setString(1, cpuId);
            stmt.setObject(2, new java.sql.Timestamp(new Date().getTime()));
            stmt.setLong(3, id);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public void insert(String username) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("insert into " + table + " (id, user_name) values(?, ?)")) {
            stmt.setLong(1, id.incrementAndGet());
            stmt.setObject(2, username);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public void delete(Long id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("delete from " + table + " where id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            conn.commit();
        }
    }
}
