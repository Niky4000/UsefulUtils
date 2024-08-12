/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.api.smo.model.SmoSyncRequest;
import ru.ibs.pmp.common.lib.Db;
import static ru.ibs.testpumputils.TestPumpUtilsMain.buildSmoSessionFactory;
import ru.ibs.testpumputils.interceptors.SQLInterceptor;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler2;

/**
 *
 * @author me
 */
public class TestSmoSessionFactory {

    private static SessionFactory sessionFactory;
    private static SessionFactoryInterface smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler2(buildSmoSessionFactory(), new SQLInterceptor(sql -> {
            return sql.replaceAll("PMP_PROD", "PMP_SMO");
        })));
        sessionFactory = TestPumpUtilsMain.buildSessionFactory();
        try {
//            checkMedicalCase();
            checkThatProcessIsFinished2(4708L, new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-01"), 728L);
        } finally {
            smoSessionFactory.close();
            sessionFactory.close();
        }
    }

    private static void checkMedicalCase() {
        try {
            Long count = Db.select(sessionFactory, session -> {
                Number num = (Number) session.createCriteria(MedicalCase.class).setProjection(Projections.rowCount()).uniqueResult();
                return num.longValue();
            });
            System.out.println("count = " + count.toString() + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SmoSyncRequest checkThatProcessIsFinished2(Long lpuId, Date period, Long answerNum) {
        // Так почему-то не работает! Возвращается null! Видимо, есть какие-то причуды hibernate'а!
//		Long count = Db.select(sessionFactory, session -> ((Number) session.createCriteria(SmoSyncRequest.class).setProjection(Projections.count("id")).add(Restrictions.and(
//				Restrictions.eq("period", new java.sql.Date(period.getTime())), Restrictions.eq("moId", lpuId), Restrictions.eq("id", answerNum), Restrictions.eq("status", SmoSyncRequest.RequestStatus.FINISHED))).uniqueResult()).longValue());
        List<Object[]> objList = Db.select(smoSessionFactory, session -> (List<Object[]>) session.createSQLQuery("select id, parent_id from smo_request_sync where mo_id=:lpuId and period=:period and id=:answerNum and status=:status order by creation_date desc").setParameter("lpuId", lpuId).setParameter("period", period).setParameter("answerNum", answerNum).setParameter("status", SmoSyncRequest.RequestStatus.FINISHED.name()).list());
        if (objList.isEmpty()) {
            throw new RuntimeException("Невозможно отправить ответ, пока он находится в процессе формирования. Дождитесь завершения формирования ответа");
        }
        SmoSyncRequest smoRequest = new SmoSyncRequest();
        smoRequest.setId(((Number) objList.get(0)[0]).longValue());
        smoRequest.setParentId(((Number) objList.get(0)[1]).longValue());
        return smoRequest;
    }

    private static void checkThatProcessIsFinished(Long lpuId, Date period, Long answerNum) {
        Long count = Db.select(smoSessionFactory, session -> {
            Number num = (Number) session.createCriteria(SmoSyncRequest.class)
                    .setProjection(Projections.rowCount())
                    //                    .add(Restrictions.and(
                    //                            Restrictions.eq("period", new java.sql.Date(period.getTime())),
                    //                            Restrictions.eq("moId", lpuId),
                    //                            Restrictions.eq("id", answerNum),
                    //                            Restrictions.eq("status", SmoSyncRequest.RequestStatus.FINISHED))
                    //                    )
                    .uniqueResult();
//            Number num2 = (Number) session.createSQLQuery("select count(*) from SMO_REQUEST_SYNC").uniqueResult();
            return num.longValue();
        });
        System.out.println("count = " + count.toString() + "!");
    }
}
