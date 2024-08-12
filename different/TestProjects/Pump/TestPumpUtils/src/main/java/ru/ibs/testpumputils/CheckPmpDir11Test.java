/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.dao.hibernate.MedicalCaseDAOHibernate;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.service.check.msk.CheckPmpDir11;
import ru.ibs.pmp.service.flk.ErrorMarker;
import ru.ibs.pmp.service.flk.ErrorMarkerImpl;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;


/**
 *
 * @author Me
 */
public class CheckPmpDir11Test {

    static SessionFactoryInterface sessionFactory;
    static SessionFactory nsiSessionFactoryProxy;

    public static void test() throws Exception {
        MedicalCaseDAOHibernate medicalCaseDAOHibernate=new MedicalCaseDAOHibernate();
        int countCasesForYearByCardNumber = medicalCaseDAOHibernate.countCasesForYearByCardNumber(2L, "3", "4", 5L, new Date());
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        nsiSessionFactoryProxy = TestPumpUtilsMain.buildNsiSessionFactory();
        try {
            CheckPmpDir11 checkPmpDir11 = new CheckPmpDir11();
            FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
            ru.ibs.pmp.nsi.service.NsiServiceImpl nsiServiceImpl = new ru.ibs.pmp.nsi.service.NsiServiceImpl();
            FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
            FieldUtil.setField(checkPmpDir11, findNsiEntries, "findNsiEntries");
            FieldUtil.setField(nsiServiceImpl, nsiSessionFactoryProxy, "sessionFactory");
            ApplicationContext appContext = (ApplicationContext) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{ApplicationContext.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("getBean")) {
                        Class cl = (Class) args[0];
                        if (cl.equals(NsiService.class)) {
                            return nsiServiceImpl;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            });
            FieldUtil.setField(nsiServiceImpl, appContext, "appContext");
            
            ErrorMarker errorMarker=new ErrorMarkerImpl("", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            MedicalCase medicalCase = new MedicalCase();
            medicalCase.setDirectionDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-06-01 00:00:00"));
            medicalCase.setDirectionLpuId(1795L);
            medicalCase.setDirectionLpuRfId("771795");
            checkPmpDir11.execute(medicalCase, errorMarker);
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
            nsiSessionFactoryProxy.close();
        }
    }
}
