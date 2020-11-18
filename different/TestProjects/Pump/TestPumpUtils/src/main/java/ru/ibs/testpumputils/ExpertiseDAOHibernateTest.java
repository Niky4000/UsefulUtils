/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import ru.ibs.pmp.api.model.dto.msk.expertise.FindExpertiseErrorsRequest;
import ru.ibs.pmp.dao.hibernate.expertise.ExpertiseDAOHibernate;
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

    public static void test() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            ExpertiseDAOHibernate expertiseDAOHibernate = new ExpertiseDAOHibernate();
            FieldUtil.setField(expertiseDAOHibernate, sessionFactory, "sessionFactory");
            FindExpertiseErrorsRequest request = new FindExpertiseErrorsRequest();
            request.setMoId(1892L);
            request.setPeriod(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-10-01 00:00:00"));
            request.setDesc(true);
            request.setFieldSortName("id");
            request.setMailGwId(6018850L);
            expertiseDAOHibernate.findErrors(request);
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
