package ru.ibs.testpumputils;

import com.github.fluent.hibernate.cfg.scanner.EntityScanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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
        EntityScanner.scanPackages("ru.ibs.pmp.api.model", "ru.ibs.pmp.auth.model", "ru.ibs.testpumputils.bean").addTo(configuration);
        configuration.configure();
//        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
//        return configuration.buildSessionFactory(serviceRegistry);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        return sessionFactory;
    }

    public static SessionFactory buildPractSessionFactory() throws FileNotFoundException, IOException {
        // Create the SessionFactory from hibernate.cfg.xml
        Properties p = new Properties();
        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.pmp.db.url"));
        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.pmp.db.username"));
        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.pmp.db.password"));
        EntityScanner.scanPackages("ru.ibs.pmp.api.practitioners.model.practitioner", "ru.ibs.pmp.api.practitioners.model.audit.practitioner", "ru.ibs.pmp.api.practitioners.model.practitioner.aud", "ru.ibs.pmp.auth.model").addTo(configuration);
        configuration.configure();
//        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
//        return configuration.buildSessionFactory(serviceRegistry);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        return sessionFactory;
    }

    public static SessionFactory buildNsiSessionFactory() throws FileNotFoundException, IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.nsi.db.url"));
        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.nsi.db.username"));
        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.nsi.db.password"));
        EntityScanner.scanPackages("ru.ibs.pmp.auth.model", "ru.ibs.pmp.api.nsi.model").addTo(configuration);
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        return sessionFactory;
    }

    public static SessionFactory buildSmoSessionFactory() throws FileNotFoundException, IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.smo.db.url"));
        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.smo.db.username"));
        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.smo.db.password"));
        EntityScanner.scanPackages("ru.ibs.pmp.api.model.dbf.parcel", "ru.ibs.pmp.api.smo.model").addTo(configuration);
        configuration.configure();
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
//            List<String> argList = Arrays.asList(args);
//            LogAnalizer.analizeLogs(argList.get(0), new ArrayList<>(argList.subList(1, argList.size())).toArray(new String[1]));
//            PmpWsImplTest.test();
//            AccountingPeriodServiceImplTest.test();
//            BillStatisticsDAOHibernateTest.test();
//            RequirementControllerTest.test();
//            String pocket = "(.+?)\\s";
//            String pocket2 = "(.+)$";
//            Pattern parsePattern = Pattern.compile("ErrorCode=" + pocket + "MedicalCase.id=" + pocket + "patient.id=" + pocket + "patient.type=" + pocket + "Service.id=" + pocket + "price=" + pocket + "detail=" + pocket2, Pattern.DOTALL);
//
//            String row = "ErrorCode=CREATE.B4 MedicalCase.id=229274434 patient.id=5281196412 patient.type=0 Service.id=236000 price=2059820 detail=???? ???????????? ????? ??? ????????? 1000 ????!";
//            if (parsePattern.matcher(row).matches()) {
//                System.out.println();
//            }
//            ListBillsServiceImplTest.test();
//            PdfWatermarkTest.test3();
//            MoDepartmentSaveTest.test();
//            CheckPmpDir11Test.test();
//            CheckInvoiceNUTest.test();
//            CheckNLTest.test();
//            CheckH65Test.test();
//            DbTest.test();
//            Barcode128Test.test();
//            new PdfBirtTest().executeReport();
//            BirtOutputStreamTest.test3();
//            HttpTest.test();
//            TestVD.test2();
//            ReportServiceImplTest.test();
//            FeatureImplsTest.test();
//            FeatureImplsTest.test2();
//            ExpertiseDAOHibernateTest.test();
//            BarcodeTest.test();
//            ErrorsFileExporterTest.test();
//            UpAndUdFileExporterTest.test();
//            UpAndUdFileExporterTest.test2();
//            IntSmoAktMekFileExporterTest.test();
//            IntSmoAktTableFormFileExporterTest.test();
//            IntSmoAktPfFileExporterTest.test();
//            UnloadDbfs.unload();
//            EmergencyCtrlFileExporterTest.test();
        new DownloadService("https://cf.mosmedzdrav.ru/documentService/v1/getHtml?url=30583913-0-C16.2-60075-20201202-01-20201202", new File("C:\\tmp")).download();
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
