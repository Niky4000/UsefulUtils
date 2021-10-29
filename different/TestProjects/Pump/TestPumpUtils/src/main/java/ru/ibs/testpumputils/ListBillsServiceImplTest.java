package ru.ibs.testpumputils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import org.hibernate.Session;
import ru.ibs.pmp.api.model.Bill;
import ru.ibs.pmp.dao.BillStatisticsDAO;
import ru.ibs.pmp.dao.hibernate.BillStatisticsDAOHibernate;
import ru.ibs.pmp.service.impl.ListBillsServiceImpl;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 * @author NAnishhenko
 */
public class ListBillsServiceImplTest {

    static SessionFactoryInterface sessionFactory;

    public static void test() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            ListBillsServiceImpl listBillsServiceImpl = new ListBillsServiceImpl();
            BillStatisticsDAO billStatisticsDAO = new BillStatisticsDAOHibernate();
            FieldUtil.setField(listBillsServiceImpl, billStatisticsDAO, "billStatisticsDAO");
            Method getInfoFromBillStatisticsTimeListWrapperMethod = listBillsServiceImpl.getClass().getDeclaredMethod("getInfoFromBillStatisticsTimeListWrapper", Bill.class);
            getInfoFromBillStatisticsTimeListWrapperMethod.setAccessible(true);
	    Bill bill;
	    Session session = sessionFactory.openSession();
	    try {
		bill = (Bill) session.get(Bill.class, 10169048L);
	    } finally {
		session.close();
	    }
            List<String> list = (List<String>) getInfoFromBillStatisticsTimeListWrapperMethod.invoke(listBillsServiceImpl, bill);
            list.stream().forEachOrdered(str -> System.out.println(str));
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
