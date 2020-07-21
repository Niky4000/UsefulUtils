/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter;

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
import ru.kiokle.filetransmitter.bean.FileSizeBean;

/**
 *
 * @author Me
 */
public class H2Handler {

    private static final MyLogger LOG = new MyLogger();
    private Long id = 0L;
    private static final String schema = "KK";
    private static final String files = schema + ".files";
    private Connection connection;

    public void createDatabase(String h2DbLocation) {
        if (!new File(h2DbLocation).getParentFile().exists()) {
            new File(h2DbLocation).getParentFile().mkdirs();
        }
        boolean dbExisted = new File(h2DbLocation + ".mv.db").exists();
        try {
            connection = DriverManager.getConnection("jdbc:h2:file:" + h2DbLocation + ";DB_CLOSE_DELAY=-1;MODE=Oracle;TRACE_LEVEL_SYSTEM_OUT=1;INIT=CREATE SCHEMA IF NOT EXISTS " + schema + "\\;SET SCHEMA " + schema + "", "sa", "");
        } catch (Exception e) {
            LOG.log("PatientH2Thread start failed because open connection was failed!", e);
            closeConnection();
            throw new RuntimeException(e);
        }
//        System.setProperty("h2.db.location", h2DbLocation);
        try {
            if (dbExisted) {
                PreparedStatement prepareStatement = connection.prepareStatement("select max(id) as max_ from " + files);
                ResultSet executeQuery = prepareStatement.executeQuery();
                if (executeQuery.next()) {
                    id = executeQuery.getLong("max_");
                    id++;
                }
                executeQuery.close();
                prepareStatement.close();
            } else {
                executeScript("CREATE TABLE " + files + "(id bigint primary key, file_name varchar2, md5sum varchar2, created timestamp, file_size bigint, status bigint)");
                executeScript("CREATE INDEX ON " + files + "(file_name)");
                executeScript("CREATE INDEX ON " + files + "(status)");
            }
        } catch (Exception e) {
            LOG.log("PatientH2Thread start failed!", e);
            closeConnection();
            throw new RuntimeException(e);
        }
    }

    public void save(String fileName, String md5Sum, Long fileSize, boolean status) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("insert into " + files + " (id,file_name,md5sum,created,file_size,status) values(?,?,?,?,?,?) ");
            statement.setLong(1, id);
            statement.setString(2, fileName);
            statement.setString(3, md5Sum);
            statement.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
            statement.setLong(5, fileSize);
            statement.setLong(6, status ? 1L : 0L);
            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (Exception ee) {
                throw new RuntimeException("statement close Fatal Error!", e);
            }
            LOG.log("PatientH2Thread save failed!", e);
            throw new RuntimeException("save Fatal Error!", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ee) {
                    throw new RuntimeException("statement close Fatal Error!", ee);
                }
            }
        }
        id++;
    }

    public FileSizeBean exists(String fileName) {
        Statement statement = null;
        String query = "select file_name,status,file_size from " + files + " where file_name='" + fileName + "' and status=1";
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            List<Long> results = new ArrayList<>();
            while (resultSet.next()) {
                String fileNameResult = resultSet.getString("file_name");
                Long status = resultSet.getLong("status");
                Long fileSize = resultSet.getLong("file_size");
                results.add(fileSize);
            }
            return results.size() == 1 ? new FileSizeBean(true, results.get(0)) : new FileSizeBean(false, 0L);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ee) {
                    throw new RuntimeException(ee);
                }
            }
        }
    }

    private void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            LOG.log("PatientH2Thread connection closing failed!", ex);
        }
    }

    private void executeScript(String createSQLQuery) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(createSQLQuery);
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ee) {
                throw new RuntimeException("statement close Fatal Error!", e);
            }
            LOG.log("PatientH2Thread createTable failed!", e);
            throw new RuntimeException("createTable Fatal Error!", e);
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
}
