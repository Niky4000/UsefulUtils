package ru.my.db2project.config;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import liquibase.exception.LiquibaseException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.ibs.pmp.api.model.BillStatistics;
import ru.ibs.pmp.api.model.BillStatisticsTime;
//import ru.ibs.pmp.module.pmp.bill.recreate.RecreateImpl;
//import ru.ibs.pmp.module.pmp.bill.recreate.RecreateUtils;
//import ru.ibs.pmp.module.pmp.bill.recreate.stages.aspects.RecreateUtilsAspect;
//import ru.ibs.pmp.module.pmp.bill.recreate.utils.SerializeService;

/**
 * @author NAnishhenko
 */
//@Configuration
@Component
public class H2Config {

//    @Autowired
//    SerializeService serializeUtils;
//    @Autowired
//    RecreateUtilsAspect recreateUtilsAspect;
    @Autowired
    ApplicationContext applicationContext;

    private String databaseFilePath;

    private String path;

    // Constants
    public static final String H2_OBJECT_NAME = "h2";
    private static final String H2_CONFIG_FILE = "h2ddl.xml";
    public static final String H2_DATABASE_FILE_EXTENSION = ".mv.db";
    public static final String H2_TRACE_DATABASE_FILE_EXTENSION = ".trace.db";

    // Main H2 Objects
    private JdbcConnectionPool h2DataSource;
    private liquibase.integration.spring.SpringLiquibase liquibase;
    private org.springframework.orm.hibernate4.LocalSessionFactoryBean h2SessionFactory;
    private org.springframework.orm.hibernate4.HibernateTransactionManager h2HibernateTransactionManager;
    private org.springframework.transaction.support.TransactionTemplate h2TransactionTemplate;

    public H2Config() {
    }

    public H2Config(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected String getAndInitDatabaseFilePath() {
//        File serFile = serializeUtils.getSerFile(H2_OBJECT_NAME);
        File serFile = new File(getPath() + H2_OBJECT_NAME);
        return serFile.getAbsolutePath() + "__" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
    }

//    @Bean
//    @Lazy
    public void init() {
        databaseFilePath = getAndInitDatabaseFilePath();
        h2DataSource = JdbcConnectionPool.create("jdbc:h2:" + databaseFilePath + ";DB_CLOSE_DELAY=-1;MODE=Oracle;TRACE_LEVEL_SYSTEM_OUT=0;INIT=CREATE SCHEMA IF NOT EXISTS PMP_PROD\\;SET SCHEMA PMP_PROD", "sa", "");
        liquibase = new liquibase.integration.spring.SpringLiquibase();
        liquibase.setChangeLog(H2_CONFIG_FILE);
        liquibase.setDataSource(h2DataSource);
        liquibase.setResourceLoader(applicationContext);
        try {
            liquibase.afterPropertiesSet();
        } catch (LiquibaseException ex) {
            Logger.getLogger(H2Config.class.getName()).log(Level.SEVERE, null, ex);
        }

        Properties hibernateProperties = new Properties();
        ImmutableMap<Object, Object> hibernatePropertiesMap = ImmutableMap.builder()
                .put("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .put("hibernate.format_sql", false)
                .put("hibernate.show_sql", false)
                .put("hibernate.use_sql_comments", false)
                .put("hibernate.connection.release_mode", "on_close")
                .put("hibernate.connection.autocommit", false)
                .put("hibernate.cache.use_second_level_cache", false)
                .put("hibernate.generate_statistics", false)
                .put("hibernate.cache.use_structured_entries", true)
                .put("hibernate.jdbc.batch_size", 100)
                .put("hibernate.order_inserts", true)
                .put("hibernate.jdbc.batch_versioned_data", true)
                .put("hibernate.order_updates", true)
                .build();
        hibernateProperties.putAll(hibernatePropertiesMap);
        h2SessionFactory = new org.springframework.orm.hibernate4.LocalSessionFactoryBean();
        h2SessionFactory.setHibernateProperties(hibernateProperties);
        h2SessionFactory.setPackagesToScan("ru.ibs.pmp.api.model");
        h2SessionFactory.setDataSource(h2DataSource);
        try {
            h2SessionFactory.afterPropertiesSet();
        } catch (IOException ex) {
            Logger.getLogger(H2Config.class.getName()).log(Level.SEVERE, null, ex);
        }

        h2HibernateTransactionManager = new org.springframework.orm.hibernate4.HibernateTransactionManager();
        h2HibernateTransactionManager.setSessionFactory(h2SessionFactory.getObject());
        h2HibernateTransactionManager.afterPropertiesSet();

        h2TransactionTemplate = new org.springframework.transaction.support.TransactionTemplate();
        h2TransactionTemplate.setTransactionManager(h2HibernateTransactionManager);
        h2TransactionTemplate.afterPropertiesSet();
//        if (recreateUtilsAspect != null) {
//            recreateUtilsAspect.setSessionFactory(h2SessionFactory.getObject());
//            recreateUtilsAspect.setTx(h2TransactionTemplate);
//        }
    }

    public String getDatabaseFilePath() {
        return databaseFilePath;
    }

    private void destroy() {
        int activeConnections = h2DataSource.getActiveConnections();
        try {
            h2DataSource.getConnection().createStatement().execute("SHUTDOWN");
        } catch (SQLException ex) {
            Logger.getLogger(H2Config.class.getName()).log(Level.SEVERE, null, ex);
        }
        h2SessionFactory.destroy();
        h2DataSource.dispose();
    }

    public void deleteH2Database(boolean deleteAllFiles) {
//        if (recreateUtilsAspect != null) {
//            recreateUtilsAspect.setSessionFactory(null);
//            recreateUtilsAspect.setTx(null);
//        }
        destroy();
        if (deleteAllFiles) {
            DeleteDbFiles.execute(databaseFilePath, null, true);
            File databaseFile = new File(databaseFilePath + H2_DATABASE_FILE_EXTENSION);
            File databaseTraceFile = new File(databaseFilePath + H2_TRACE_DATABASE_FILE_EXTENSION);
            if (databaseFile.exists()) {
                databaseFile.delete();
            }
            if (databaseTraceFile.exists()) {
                databaseTraceFile.delete();
            }
        }
    }

    public void readDatabase() {
//        this.applicationContext = applicationContext;
//        init();
        List<BillStatisticsTime> billStatisticsTimeList = h2TransactionTemplate.execute(status -> {
            Session session = h2SessionFactory.getObject().openSession();
            List<BillStatisticsTime> billStatisticsTimeListDb = session.createCriteria(BillStatisticsTime.class).addOrder(Order.asc("id")).list();
//            for (BillStatisticsTime billStatisticsTime : billStatisticsTimeListDb) {
//                Hibernate.initialize(billStatisticsTime.getBillStatistics());
//            }
            session.close();
            return billStatisticsTimeListDb;
        });
//        deleteH2Database(false);
        System.out.println("--------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------");
//        recreateUtilsImpl.logMessage("--------------------------------------------------------------------", false, null, null, RecreateImpl.LogType.INFO, null);
//        recreateUtilsImpl.logMessage("--------------------------------------------------------------------", false, null, null, RecreateImpl.LogType.INFO, null);
//        recreateUtilsImpl.logMessage("--------------------------------------------------------------------", false, null, null, RecreateImpl.LogType.INFO, null);
//        recreateUtilsImpl.logMessage("--------------------------------------------------------------------", false, null, null, RecreateImpl.LogType.INFO, null);
        for (BillStatisticsTime billStatisticsTime : billStatisticsTimeList) {
//            recreateUtilsImpl.logMessage(billStatisticsTime.getOperationDescription(), false, null, null, RecreateImpl.LogType.INFO, null);
            System.out.println(billStatisticsTime.toString());
        }
    }

    public void insertIntoDatabase(List<BillStatisticsTime> billStatisticsTimeList) {
//        this.applicationContext = applicationContext;
//        init();

        h2TransactionTemplate.execute(status -> {
            Session session = h2SessionFactory.getObject().openSession();
            BillStatistics billStatistics = new BillStatistics();
            billStatistics.setId(0L);
            for (BillStatisticsTime billStatisticsTime : billStatisticsTimeList) {
                billStatisticsTime.setBillStatistics(billStatistics);
                session.save(billStatisticsTime);
            }
            session.flush();
            session.close();
            return null;
        });
//        deleteH2Database(false);
    }

    public String getPath() {
        return path;
    }

}
