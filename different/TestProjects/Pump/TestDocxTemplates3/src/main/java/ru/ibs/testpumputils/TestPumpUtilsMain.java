//package ru.ibs.testpumputils;
//
//import com.github.fluent.hibernate.cfg.scanner.EntityScanner;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Properties;
//import javax.sql.DataSource;
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//
///**
// * @author NAnishhenko
// */
//public class TestPumpUtilsMain {
//
//    public static SessionFactory buildSessionFactory() throws FileNotFoundException, IOException {
//        // Create the SessionFactory from hibernate.cfg.xml
//        Properties p = new Properties();
//        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
//        Configuration configuration = new Configuration();
//        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.pmp.db.url"));
//        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.pmp.db.username"));
//        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.pmp.db.password"));
//        EntityScanner.scanPackages("ru.ibs.pmp.api.model", "ru.ibs.pmp.auth.model", "ru.ibs.testpumputils.bean", "ru.ibs.pmp.lpu.model.mo").addTo(configuration);
//        configuration.configure();
////        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
////        return configuration.buildSessionFactory(serviceRegistry);
//        SessionFactory sessionFactory = configuration.buildSessionFactory();
//        return sessionFactory;
//    }
//
//    public static SessionFactory buildPractSessionFactory() throws FileNotFoundException, IOException {
//        // Create the SessionFactory from hibernate.cfg.xml
//        Properties p = new Properties();
//        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
//        Configuration configuration = new Configuration();
//        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.pmp.db.url"));
//        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.pmp.db.username"));
//        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.pmp.db.password"));
//        EntityScanner.scanPackages("ru.ibs.pmp.api.practitioners.model.practitioner", "ru.ibs.pmp.api.practitioners.model.audit.practitioner", "ru.ibs.pmp.api.practitioners.model.practitioner.aud", "ru.ibs.pmp.auth.model").addTo(configuration);
//        configuration.configure();
////        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
////        return configuration.buildSessionFactory(serviceRegistry);
//        SessionFactory sessionFactory = configuration.buildSessionFactory();
//        return sessionFactory;
//    }
//
//    public static SessionFactory buildNsiSessionFactory() throws FileNotFoundException, IOException {
//        Properties p = new Properties();
//        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
//        Configuration configuration = new Configuration();
//        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.nsi.db.url"));
//        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.nsi.db.username"));
//        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.nsi.db.password"));
//        EntityScanner.scanPackages("ru.ibs.pmp.auth.model", "ru.ibs.pmp.api.nsi.model").addTo(configuration);
//        configuration.configure();
//        SessionFactory sessionFactory = configuration.buildSessionFactory();
//        return sessionFactory;
//    }
//
//    public static SessionFactory buildSmoSessionFactory() throws FileNotFoundException, IOException {
//        Properties p = new Properties();
//        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
//        Configuration configuration = new Configuration();
//        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.smo.db.url"));
//        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.smo.db.username"));
//        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.smo.db.password"));
//        EntityScanner.scanPackages("ru.ibs.pmp.api.smo.model", "ru.ibs.pmp.api.model", "ru.ibs.pmp.auth.model").addTo(configuration);
//        configuration.configure();
//        SessionFactory sessionFactory = configuration.buildSessionFactory();
//        return sessionFactory;
//    }
//
//    public static SessionFactory buildAuthSessionFactory() throws FileNotFoundException, IOException {
//        Properties p = new Properties();
//        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
//        Configuration configuration = new Configuration();
//        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.auth.db.url"));
//        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.auth.db.username"));
//        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.auth.db.password"));
//        EntityScanner.scanPackages("ru.ibs.pmp.auth.model").addTo(configuration);
//        configuration.configure();
//        SessionFactory sessionFactory = configuration.buildSessionFactory();
//        return sessionFactory;
//    }
//
//    public static DataSource getAuthDataSource() {
//        try {
//            Properties p = new Properties();
//            p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
//            return getDataSourceBuilder(p.getProperty("runtime.auth.db.url"), p.getProperty("runtime.auth.db.username"), p.getProperty("runtime.auth.db.password"), p.getProperty("db.driver"));
//        } catch (FileNotFoundException ex) {
//            throw new RuntimeException(ex);
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    private static DataSource getDataSourceBuilder(String url, String user, String password, String driver) {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName(driver);
//        dataSource.setUrl(url);
//        dataSource.setUsername(user);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//}
