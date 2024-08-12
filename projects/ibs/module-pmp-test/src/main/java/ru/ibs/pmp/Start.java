/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp;

import com.google.common.base.Stopwatch;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ReflectionUtils;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.auth.reps.GlueRepository;
import ru.ibs.pmp.zlib.module.pmp.bill.recreate.dao.psevdo.nsi.PsevdoFindNsiEntries;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.persons.features.patients.FindPatientsByBasicInfoImpl;
import ru.ibs.pmp.persons.interfaces.PersonDAO;
import ru.ibs.pmp.ws.ListBillsTest;

/**
 *
 * @author NAnishhenko
 */
public class Start {

    // Web-services
    PersonDAO personDAOImplWS;
    // Transactions
    SessionFactory sessionFactory;
    SessionFactory nsiSessionFactory;
    SessionFactory smoSessionFactory;
    SessionFactory authSessionFactory;
    TransactionTemplate tx;
    TransactionTemplate nsitx;
    TransactionTemplate smotx;
    TransactionTemplate authtx;

//    EntityManager lpuEntityManager;
    ApplicationContext personsApplicationContext;
    ApplicationContext applicationContext;
//    ApplicationContext nsiContext;

    public Start() {
        try {
            init();
        } catch (Exception ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public TransactionTemplate getTx() {
        return tx;
    }

    public SessionFactory getNsiSessionFactory() {
        return nsiSessionFactory;
    }

    public SessionFactory getAuthSessionFactory() {
        return authSessionFactory;
    }

    public TransactionTemplate getAuthtx() {
        return authtx;
    }

    protected void init() throws BeansException, InterruptedException, ExecutionException {
        // Этап 0
        // ------------- Инициализация из контекста ---------------------------
        // String currentDir = "/src/main/resources";
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        stopwatch.start();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<ApplicationContext> personsApplicationContextFuture = executorService.submit(new Callable<ApplicationContext>() {

            @Override
            public ApplicationContext call() throws Exception {
                return new ClassPathXmlApplicationContext("module_persons.xml");
            }
        });

        Future<ApplicationContext> applicationContextFuture = executorService.submit(new Callable<ApplicationContext>() {

            @Override
            public ApplicationContext call() throws Exception {
                return new ClassPathXmlApplicationContext("module_test.xml");
            }
        });
//        Future<ApplicationContext> nsiContextFuture = executorService.submit(new Callable<ApplicationContext>() {
//
//            @Override
//            public ApplicationContext call() throws Exception {
//                return new ClassPathXmlApplicationContext("nsi-read-context.xml");
//            }
//        });
//        applicationContext = new ClassPathXmlApplicationContext("module_test.xml");
//        ru.ibs.pmp.config.PmpProperties propertyConfigurer = (ru.ibs.pmp.config.PmpProperties) applicationContext.getBean("propertyConfigurer");
        personsApplicationContext = personsApplicationContextFuture.get();
        applicationContext = applicationContextFuture.get();
//        nsiContext = nsiContextFuture.get();

        executorService.shutdown();
        stopwatch.stop();
        long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
        stopwatch.reset();
        System.out.println("Context started in " + elapsed + " seconds!");

        ru.ibs.pmp.config.PmpProperties propertyConfigurer = (ru.ibs.pmp.config.PmpProperties) applicationContext.getBean("propertyConfigurer");

        personDAOImplWS = personsApplicationContext.getBean(PersonDAO.class);
        setTransactionBeans(applicationContext);
    }

    protected void setTransactionBeans(ApplicationContext applicationContext) throws BeansException {
        sessionFactory = applicationContext.getBean("sessionFactory", SessionFactory.class);
        nsiSessionFactory = applicationContext.getBean("nsiSessionFactory", SessionFactory.class);
        smoSessionFactory = applicationContext.getBean("smoSessionFactory", SessionFactory.class);
        authSessionFactory = applicationContext.getBean("authSessionFactory", SessionFactory.class);

        tx = applicationContext.getBean("transactionTemplate", TransactionTemplate.class);
        nsitx = applicationContext.getBean("nsiTransactionTemplate", TransactionTemplate.class);
        smotx = applicationContext.getBean("smoTransactionTemplate", TransactionTemplate.class);
        authtx = applicationContext.getBean("authTransactionTemplate", TransactionTemplate.class);
    }

    public static void main(String[] args) throws Exception {
        Start start = new Start();
        FindPatientsByBasicInfoImpl df = new FindPatientsByBasicInfoImpl();
        PsevdoFindNsiEntries dfd = new PsevdoFindNsiEntries();
        System.out.println("Inited!");
        FindNsiEntries findNsiEntries = start.getApplicationContext().getBean(FindNsiEntries.class);
        NsiService nsiService = start.getApplicationContext().getBean(NsiService.class);
        Field nsiServiceField = findNsiEntries.getClass().getDeclaredField("nsiService");
        nsiServiceField.setAccessible(true);
        ReflectionUtils.setField(nsiServiceField, findNsiEntries, nsiService);
        ListBillsTest listBillsTest = start.getApplicationContext().getBean(ListBillsTest.class);
//        ListBillsTest listBillsTest = new ListBillsTest();
        listBillsTest.testListBills(findNsiEntries, start.getSessionFactory(), start.getNsiSessionFactory(),
                start.getTx(), start.getAuthSessionFactory(), start.getAuthtx());
    }

}
