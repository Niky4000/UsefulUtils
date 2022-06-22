//package ru.ibs.testpumputils;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.util.HashSet;
//import java.util.concurrent.ConcurrentHashMap;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.springframework.context.ApplicationContext;
//import ru.ibs.pmp.api.model.MedicalCase;
//import ru.ibs.pmp.api.model.msk.TapInfo;
//import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
//import ru.ibs.pmp.dao.hibernate.AbstractGenericDAO;
//import ru.ibs.pmp.dao.hibernate.TapInfoDAOHibernate;
//import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
//import ru.ibs.pmp.nsi.service.NsiService;
//import ru.ibs.pmp.service.check.msk.CheckInvoiceNU;
//import ru.ibs.pmp.service.flk.ErrorMarker;
//import ru.ibs.pmp.service.flk.ErrorMarkerImpl;
//import ru.ibs.pmp.service.flk.check.AbstractFLKCheck;
//import ru.ibs.pmp.api.mailgw.service.impl.ModulePmpAbstractGenericService;
//import ru.ibs.pmp.service.impl.TapInfoServiceImpl;
//import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
//import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
//import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
//import ru.ibs.testpumputils.utils.FieldUtil;
//
///**
// * @author IBS_ERZL
// */
//public class CheckInvoiceNUTest {
//
//	static SessionFactoryInterface sessionFactory;
//	static SessionFactory nsiSessionFactoryProxy;
//
//	public static void test() throws Exception {
//		sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
//		nsiSessionFactoryProxy = TestPumpUtilsMain.buildNsiSessionFactory();
//		try {
//			CheckInvoiceNU checkInvoiceNU = new CheckInvoiceNU();
//			FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
//			ru.ibs.pmp.nsi.service.NsiServiceImpl nsiServiceImpl = new ru.ibs.pmp.nsi.service.NsiServiceImpl();
//			FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
//			FieldUtil.setField(checkInvoiceNU, AbstractFLKCheck.class, findNsiEntries, "findNsiEntries");
//			FieldUtil.setField(nsiServiceImpl, nsiSessionFactoryProxy, "sessionFactory");
//			ApplicationContext appContext = (ApplicationContext) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{ApplicationContext.class}, new InvocationHandler() {
//				@Override
//				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//					if (method.getName().equals("getBean")) {
//						Class cl = (Class) args[0];
//						if (cl.equals(NsiService.class)) {
//							return nsiServiceImpl;
//						} else {
//							return null;
//						}
//					} else {
//						return null;
//					}
//				}
//			});
//			FieldUtil.setField(nsiServiceImpl, appContext, "appContext");
//			TapInfoServiceImpl tapInfoServiceImpl = new TapInfoServiceImpl();
//			FieldUtil.setField(checkInvoiceNU, tapInfoServiceImpl, "tapInfoService");
//			TapInfoDAOHibernate tapInfoDAOHibernate = new TapInfoDAOHibernate();
//			FieldUtil.setField(tapInfoServiceImpl, ModulePmpAbstractGenericService.class, tapInfoDAOHibernate, "dao");
//			FieldUtil.setField(tapInfoDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
//
//			ErrorMarker errorMarker = new ErrorMarkerImpl("", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
//			Session session = sessionFactory.openSession();
//			try {
////            TapInfo medicalCase = (TapInfo) session.get(MedicalCase.class, 240581330381L);
//				TapInfo medicalCase = (TapInfo) session.get(MedicalCase.class, 244212335386L);
//				checkInvoiceNU.execute(medicalCase, errorMarker);
//			} finally {
//				session.close();
//			}
//		} finally {
//			sessionFactory.cleanSessions();
//			sessionFactory.close();
//			nsiSessionFactoryProxy.close();
//		}
//	}
//}
