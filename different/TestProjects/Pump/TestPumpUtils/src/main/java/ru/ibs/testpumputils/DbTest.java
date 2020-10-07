///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.ibs.testpumputils;
//
//import java.lang.reflect.Proxy;
//import java.util.List;
//import ru.ibs.pmp.common.lib.Db2;
//import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
//import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
//import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
//import org.apache.commons.lang3.tuple.Pair;
//import ru.ibs.pmp.api.model.MedicalCase;
//
///**
// *
// * @author User
// */
//public class DbTest {
//
//    static SessionFactoryInterface sessionFactory;
//
//    public static void test() throws Exception {
//        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
//        try {
//            Db2 db = new Db2();
//            List<MedicalCase> medicalCases = db.select(sessionFactory, MedicalCase.class, "select * from pmp_medical_case where id=:id", new Pair[]{Pair.of("id", 369714403L)});
//        } finally {
//            sessionFactory.cleanSessions();
//            sessionFactory.close();
//        }
//    }
//}
