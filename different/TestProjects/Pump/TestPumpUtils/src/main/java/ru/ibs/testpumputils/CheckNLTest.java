/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.api.model.msk.TapInfo;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiSplpuEntry;
import ru.ibs.pmp.dao.hibernate.AbstractGenericDAO;
import ru.ibs.pmp.dao.hibernate.TapInfoDAOHibernate;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.nsi.features.impl.FindNsiSplpuFeature;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.service.check.msk.CheckNL;
import ru.ibs.pmp.service.flk.ErrorMarker;
import ru.ibs.pmp.service.flk.ErrorMarkerImpl;
import ru.ibs.pmp.service.flk.check.AbstractFLKCheck;
import ru.ibs.pmp.service.impl.ModulePmpAbstractGenericService;
import ru.ibs.pmp.service.impl.TapInfoServiceImpl;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author User
 */
public class CheckNLTest {

    static SessionFactory sessionFactory;
    static SessionFactory nsiSessionFactoryProxy;

    public static void test() throws Exception {
//        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        sessionFactory = TestPumpUtilsMain.buildSessionFactory();
        nsiSessionFactoryProxy = TestPumpUtilsMain.buildNsiSessionFactory();
        try {
            CheckNL checkNL = new CheckNL();
            FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
            ru.ibs.pmp.nsi.service.NsiServiceImpl nsiServiceImpl = new ru.ibs.pmp.nsi.service.NsiServiceImpl();
            FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
            FieldUtil.setField(checkNL, AbstractFLKCheck.class, findNsiEntries, "findNsiEntries");
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
//            TapInfoServiceImpl tapInfoServiceImpl = new TapInfoServiceImpl();
//            FieldUtil.setField(checkNL, tapInfoServiceImpl, "tapInfoService");
//            TapInfoDAOHibernate tapInfoDAOHibernate = new TapInfoDAOHibernate();
//            FieldUtil.setField(tapInfoServiceImpl, ModulePmpAbstractGenericService.class, tapInfoDAOHibernate, "dao");
//            FieldUtil.setField(tapInfoDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");

            ErrorMarker errorMarker = new ErrorMarkerImpl("", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            Session session = sessionFactory.openSession();

            FindNsiSplpuEntry findNsiSplpuEntry = new FindNsiSplpuFeature();
            FieldUtil.setField(checkNL, AbstractFLKCheck.class, findNsiSplpuEntry, "findNsiSplpuEntry");
            ru.ibs.pmp.nsi.service.NsiService nsiService = new ru.ibs.pmp.nsi.service.NsiServiceImpl();
            FieldUtil.setField(nsiService, nsiSessionFactoryProxy, "sessionFactory");
            FieldUtil.setField(nsiService, appContext, "appContext");
            FieldUtil.setField(findNsiSplpuEntry, nsiService, "nsiService");

//            TapInfo medicalCase = (TapInfo) session.get(MedicalCase.class, 240581330381L);
            MedicalCase medicalCase = (MedicalCase) session.get(MedicalCase.class, 245218540358L);
            checkNL.execute(medicalCase, errorMarker);
            session.close();
        } finally {
//            sessionFactory.cleanSessions();
            sessionFactory.close();
            nsiSessionFactoryProxy.close();
        }
    }
}
