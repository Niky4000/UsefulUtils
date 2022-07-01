package ru.ibs.testpumputils;

import java.lang.reflect.Proxy;
import org.hibernate.Hibernate;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.pmp.service.impl.SaveMedicalCaseServiceImpl;
import static ru.ibs.testpumputils.TestPumpUtilsMain.buildSessionFactory;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

public class SaveMedicalCaseServiceImplTest {

    public static void test() throws Exception {
        SaveMedicalCaseServiceImpl saveMedicalCaseServiceImpl = new SaveMedicalCaseServiceImpl();
        SessionFactoryInterface sessionFactoryProxy = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            MedicalCase medicalCase = Db.select(sessionFactoryProxy, session -> {
                MedicalCase medicalCaseDb = (MedicalCase) session.get(MedicalCase.class, 394966335351L);
                Hibernate.initialize(medicalCaseDb.getMedCaseOnkCons());
                Hibernate.initialize(medicalCaseDb.getMedCaseOnkDiags());
                Hibernate.initialize(medicalCaseDb.getMedCaseOnkProts());
                Hibernate.initialize(medicalCaseDb.getMedCaseOnkSl());
                Hibernate.initialize(medicalCaseDb.getMedCaseOnkUsls());
                Hibernate.initialize(medicalCaseDb.getMedcaseDirections());
                Hibernate.initialize(medicalCaseDb.getSimpleServices());
                return medicalCaseDb;
            });
            Db.update(session -> session.merge(medicalCase), sessionFactoryProxy);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactoryProxy.cleanSessions();
            sessionFactoryProxy.close();
        }
    }
}
