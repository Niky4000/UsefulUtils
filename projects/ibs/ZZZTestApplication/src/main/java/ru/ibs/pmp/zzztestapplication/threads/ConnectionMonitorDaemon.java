package ru.ibs.pmp.zzztestapplication.threads;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import ru.ibs.pmp.zzztestapplication.SomeClass;
import ru.ibs.pmp.zzztestapplication.threads.bean.MonitorBean;
import ru.ibs.pmp.zzztestapplication.threads.bean.MonitorBean.MonitorBeanEnum;

/**
 * @author NAnishhenko
 */
public class ConnectionMonitorDaemon extends Thread {

    private final Connection connection;
    private final BlockingQueue<MonitorBean> queue;

    public ConnectionMonitorDaemon(BlockingQueue<MonitorBean> queue) throws SQLException {
        super("ConnectionMonitorDaemon");
        this.setDaemon(true);
        this.queue = queue;
        connection = DriverManager.getConnection(SomeClass.connectionString, SomeClass.user, SomeClass.password);
    }

    @Override
    public void run() {
        while (true) {
            try {
                MonitorBean monitorBean = queue.take();
                if (monitorBean.getMonitorBeanEnum().equals(MonitorBeanEnum.COUNT)) {
                    getCount(monitorBean);
                } else {
                    close();
                    break;
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            }
        }
    }

    private void getCount(MonitorBean monitorBean) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("select count(*) as cc from v$open_cursor where user_name = 'PMP_PROD'");
            while (rs.next()) {
                Long id = rs.getLong("CC");
                System.out.println(monitorBean.getMessage() + ": count = " + id.toString() + "!");
                Thread.sleep(20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
