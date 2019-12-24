package ru.ibs.testpumputils;

import com.github.fluent.hibernate.cfg.scanner.EntityScanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 * @author NAnishhenko
 */
public class TestPumpUtilsMain {

    public static SessionFactory buildSessionFactory() throws FileNotFoundException, IOException {
        // Create the SessionFactory from hibernate.cfg.xml
        Properties p = new Properties();
        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.pmp.db.url"));
        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.pmp.db.username"));
        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.pmp.db.password"));
        EntityScanner.scanPackages("ru.ibs.pmp.api.model", "ru.ibs.pmp.auth.model").addTo(configuration);
        configuration.configure();
//        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
//        return configuration.buildSessionFactory(serviceRegistry);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        return sessionFactory;
    }

    public static void main(String args[]) throws Exception {
        System.out.println("Hello!!!");
//        SessionFactoryInterface sessionFactoryProxy = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
//        TestUtils.testSyncServiceImpl(sessionFactoryProxy);
//        testBillStatisticsDAOImpl(sessionFactoryProxy);
//            Utils10733.start(sessionFactoryProxy);
//            PmpWsImplTest.test(sessionFactoryProxy);
            PmpWsImplTest.test();
        } finally {
//            sessionFactoryProxy.cleanSessions();
//            sessionFactoryProxy.close();
            System.out.println("Goodbye!!!");
        }
    }

//            <plugin>
//                <artifactId>maven-war-plugin</artifactId>
//                <configuration>
//                    <attachClasses>true</attachClasses>
//                    <classesClassifier>classes</classesClassifier>
//                </configuration>
//            </plugin>
//        <dependency>
//            <groupId>ru.ibs.pmp</groupId>
//            <artifactId>module-nsi</artifactId>
//            <type>jar</type>
//            <classifier>classes</classifier>
//            <scope>test</scope>
//        </dependency>
}
