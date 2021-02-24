///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.ibs.testpumputils;
//
//import java.lang.reflect.Proxy;
//import javax.sql.DataSource;
//import org.hibernate.SessionFactory;
//import ru.ibs.pmp.api.smo.model.SmoSyncRequest;
//import ru.ibs.pmp.auth.reps.AuditEntryRepositoryImpl;
//import ru.ibs.pmp.common.lib.Db;
//import ru.ibs.pmp.service.PmpSettings;
//import ru.ibs.pmp.service.impl.PmpSettingsImpl;
//import ru.ibs.pmp.smo.services.tasks.QueueServiceImpl;
//import static ru.ibs.testpumputils.TestPumpUtilsMain.buildAuthSessionFactory;
//import static ru.ibs.testpumputils.TestPumpUtilsMain.buildSmoSessionFactory;
//import static ru.ibs.testpumputils.TestPumpUtilsMain.getAuthDataSource;
//import ru.ibs.testpumputils.interceptors.SQLInterceptor;
//import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
//import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler2;
//import ru.ibs.testpumputils.utils.FieldUtil;
//
///**
// *
// * @author me
// */
//public class QueueServiceImplTest {
//
//    static SessionFactory smoSessionFactory;
//    static SessionFactory authSessionFactory;
//
//    public static void test() throws Exception {
//        smoSessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler2(buildSmoSessionFactory(), new SQLInterceptor(sql -> {
//            return sql.replaceAll("PMP_PROD", "PMP_SMO");
//        })));
//        authSessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler2(buildAuthSessionFactory(), new SQLInterceptor(sql -> {
//            return sql.replaceAll("PMP_PROD", "PMP_AUTH");
//        })));
//        DataSource authDataSource = getAuthDataSource();
////        LocalContainerEntityManagerFactoryBean entityManagerFactory = getSmoEntityManagerFactory();
////        EntityManagerFactory entityManagerFactory_ = entityManagerFactory.getNativeEntityManagerFactory();
////        EntityManager entityManager = entityManagerFactory_.createEntityManager();
//        try {
//            QueueServiceImpl queueService = new QueueServiceImpl();
//            PmpSettings pmpSettings = new PmpSettingsImpl();
////            FieldUtil.setField(queueService, entityManager, "em");
//            FieldUtil.setField(queueService, smoSessionFactory, "smoSessionFactory");
//            FieldUtil.setField(queueService, authSessionFactory, "authSessionFactory");
//            FieldUtil.setField(pmpSettings, "PMP_PROD", "PMP_SCHEMA");
//            FieldUtil.setField(pmpSettings, "PMP_NSI_NEW", "NSI_SCHEMA");
//            FieldUtil.setField(pmpSettings, "PMP_AUTH", "AUTH_SCHEMA");
//            FieldUtil.setField(pmpSettings, "PMP_SMO1", "SMO_SCHEMA");
//            FieldUtil.setField(queueService, pmpSettings, "pmpSettings");
//            AuditEntryRepositoryImpl auditEntryRepositoryImpl=new AuditEntryRepositoryImpl();
//            FieldUtil.setField(auditEntryRepositoryImpl, authDataSource, "dataSource");
//            FieldUtil.setField(queueService, auditEntryRepositoryImpl, "auditEntryRepository");
//
////            SmoSyncDao appContext = (SmoSyncDao) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SmoSyncDao.class}, new InvocationHandler() {
////                @Override
////                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
////                    String sqlQueryStr = method.getAnnotation(Query.class).value();
////                    Parameter[] parameters = method.getParameters();
////                    List<Function<SQLQuery, org.hibernate.Query>> parameterList = new ArrayList<>(parameters.length);
////                    for (int i = 0; i < parameters.length; i++) {
////                        final Parameter parameter = parameters[i];
////                        final Object value = args[i];
////                        parameterList.add((sqlQuery) -> {
////                            return sqlQuery.setParameter(parameter.getAnnotation(Param.class).value(), value);
////                        });
////                    }
////                    Db.select(smoSessionFactory, session -> {
////                        SQLQuery sqlQuery = session.createSQLQuery(sqlQueryStr);
////                        parameterList.forEach(parameter->parameter.apply(sqlQuery));
////                        sqlQuery.addEntity(entityType);
////                        return null;
////                    });
////                    return null;
////                }
////            });
////            queueService.handleSmoSyncList();
//            SmoSyncRequest smoRequest = (SmoSyncRequest) Db.select(smoSessionFactory, session -> session.get(SmoSyncRequest.class, 816L));
////            SmoSyncRequest smoRequest = (SmoSyncRequest) Db.select(smoSessionFactory, session -> session.createSQLQuery("select ROWNUM rn, s.* from smo_request_sync s where id=:id").addEntity(SmoSyncRequest.class).setParameter("id", 816L).uniqueResult());
//            Runnable task = queueService.toTask(smoRequest);
//            task.run();
//        } finally {
////            entityManager.close();
////            entityManagerFactory_.close();
//            authSessionFactory.close();
//            smoSessionFactory.close();
//        }
//    }
//}
