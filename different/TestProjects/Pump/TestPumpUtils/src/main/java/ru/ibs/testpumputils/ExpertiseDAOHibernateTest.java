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
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import ru.ibs.pmp.api.model.dto.msk.expertise.FindExpertiseErrorsRequest;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.dao.hibernate.expertise.ExpertiseDAOHibernate;
import ru.ibs.pmp.lpu.service.LpuMultifilialService;
import ru.ibs.pmp.lpu.service.impl.LpuMultifilialServiceImpl;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.nsi.service.NsiServiceImpl;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class ExpertiseDAOHibernateTest {

    static SessionFactoryInterface sessionFactory;
    static SessionFactory nsiSessionFactoryProxy;

    public static void test() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        nsiSessionFactoryProxy = TestPumpUtilsMain.buildNsiSessionFactory();
        try {
            ExpertiseDAOHibernate expertiseDAOHibernate = new ExpertiseDAOHibernate();
            FieldUtil.setField(expertiseDAOHibernate, sessionFactory, "sessionFactory");
            LpuMultifilialService lpuMultifilialService = new LpuMultifilialServiceImpl();
//            FieldUtil.setField(requirementDAOHibernate, lpuMultifilialService, "lpuMultifilialService");
            FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
//            FieldUtil.setField(nsiHelper, findNsiEntries, "findNsiEntries");
            FieldUtil.setField(lpuMultifilialService, findNsiEntries, "findNsiEntries");
            FieldUtil.setField(expertiseDAOHibernate, lpuMultifilialService, "lpuMultifilialService");
            NsiServiceImpl nsiServiceImpl = new NsiServiceImpl();
            FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
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
            FieldUtil.setField(nsiServiceImpl, nsiSessionFactoryProxy, "sessionFactory");
            FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
            FieldUtil.setField(expertiseDAOHibernate, findNsiEntries, "findNsiEntries");
            FindExpertiseErrorsRequest request = new FindExpertiseErrorsRequest();
            request.setMoId(1892L);
            request.setLpuId("1892");
            request.setPeriod(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-10-01 00:00:00"));
            request.setDesc(true);
            request.setFieldSortName("id");
            request.setMailGwId(6018850L);
            expertiseDAOHibernate.findErrors(request);
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
            nsiSessionFactoryProxy.close();
        }
    }
}
