package ru.ibs.testpumputils;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.Session;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.service.check.msk.CheckOO02;
import ru.ibs.pmp.service.check.msk.CheckOO03;
import ru.ibs.pmp.service.flk.ErrorMarkerImpl;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;


/**
 *
 * @author me
 */
public class CheckOOTest {

    static SessionFactoryInterface sessionFactory;

    public static void test() throws IOException {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            Session session = sessionFactory.openSession();
            CheckOO02 checkOO02 = new CheckOO02();
            CheckOO03 checkOO03 = new CheckOO03();
            MedicalCase medicalCase = (MedicalCase) session.get(MedicalCase.class, 1099704401L);
            ErrorMarkerImpl marker = new ErrorMarkerImpl("", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            ErrorMarkerImpl marker2 = new ErrorMarkerImpl("", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            checkOO02.execute(medicalCase, marker);
            checkOO03.execute(medicalCase, marker2);
            System.err.println("CheckOO02 = " + marker.isMarkPlaced());
            System.err.println("CheckOO03 = " + marker2.isMarkPlaced());
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
