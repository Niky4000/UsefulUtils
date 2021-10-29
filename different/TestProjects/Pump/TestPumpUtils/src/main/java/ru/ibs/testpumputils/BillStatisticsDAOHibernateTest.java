package ru.ibs.testpumputils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Session;
import ru.ibs.pmp.api.model.Requirement;
import ru.ibs.pmp.dao.hibernate.BillStatisticsDAOHibernate;
import static ru.ibs.testpumputils.AccountingPeriodServiceImplTest.sessionFactory;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 * @author NAnishhenko
 */
public class BillStatisticsDAOHibernateTest {

    public static void test() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            BillStatisticsDAOHibernate billStatisticsDAOHibernate = new BillStatisticsDAOHibernate();
            Method method = BillStatisticsDAOHibernate.class.getDeclaredMethod("getInfoFromBillStatisticsTimeList", List.class);
            method.setAccessible(true);
            method.invoke(billStatisticsDAOHibernate, Arrays.asList("RmiHost: 192.168.192.217: Warning! Duplicate patients: 993240579 patientType: 1 insuranceNumber: 2958520828000351 caseId: 198904265356 billId: 100203119!"));
	    Session session = sessionFactory.openSession();
	    try {
		Requirement requirement = (Requirement) session.get(Requirement.class, 100023146L);
		FieldUtil.setField(billStatisticsDAOHibernate, sessionFactory, "sessionFactory");
		billStatisticsDAOHibernate.getDuplicatePatientsInfo(requirement);
	    } finally {
		session.close();
	    }
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
