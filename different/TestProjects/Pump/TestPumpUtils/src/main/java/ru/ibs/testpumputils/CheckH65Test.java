/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.Session;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.dao.hibernate.AbstractGenericDAO;
import ru.ibs.pmp.dao.hibernate.MedicalCaseDAOHibernate;
import ru.ibs.pmp.service.ServiceFacade;
import ru.ibs.pmp.service.check.msk.CheckH65;
import ru.ibs.pmp.service.flk.ErrorMarker;
import ru.ibs.pmp.service.flk.ErrorMarkerImpl;
import ru.ibs.pmp.service.flk.check.AbstractFLKCheck;
import ru.ibs.pmp.service.impl.MedicalCaseServiceImplementation;
import ru.ibs.pmp.service.impl.ModulePmpAbstractGenericService;
import ru.ibs.pmp.service.impl.ServiceFacadeImpl;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author User
 */
public class CheckH65Test {

    static SessionFactoryInterface sessionFactory;

    public static void test() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            Session session = sessionFactory.openSession();
            MedicalCase medicalCase = (MedicalCase) session.get(MedicalCase.class, 242580090356L);
            CheckH65 checkH65 = new CheckH65();
            ServiceFacade serviceFacade = new ServiceFacadeImpl();
            MedicalCaseServiceImplementation medicalCaseService = new MedicalCaseServiceImplementation() {
            };
            FieldUtil.setField(checkH65, AbstractFLKCheck.class, serviceFacade, "serviceFacade");
            FieldUtil.setField(serviceFacade, medicalCaseService, "medicalCaseService");
            MedicalCaseDAOHibernate medicalCaseDAOHibernate = new MedicalCaseDAOHibernate();
            FieldUtil.setField(medicalCaseService, ModulePmpAbstractGenericService.class, medicalCaseDAOHibernate, "dao");
            FieldUtil.setField(medicalCaseDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            ErrorMarker marker = new ErrorMarkerImpl("", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            checkH65.execute(medicalCase, marker);
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
