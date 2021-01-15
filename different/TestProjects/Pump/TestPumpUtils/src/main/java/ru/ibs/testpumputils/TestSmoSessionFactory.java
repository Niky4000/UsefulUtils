/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.util.Date;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import ru.ibs.pmp.api.smo.model.SmoSyncRequest;
import ru.ibs.pmp.common.ex.PmpFeatureException;
import ru.ibs.pmp.common.lib.Db;


/**
 *
 * @author me
 */
public class TestSmoSessionFactory {

    private static SessionFactory smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
//        smoSessionFactory = TestPumpUtilsMain.buildSessionFactory();
        try {
            checkThatProcessIsFinished(null, null, null);
        } finally {
            smoSessionFactory.close();
        }
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
            return num.longValue();
        });
        if (count.equals(0L)) {
            throw new PmpFeatureException("Невозможно отправить ответ, пока он находится в процессе формирования. Дождитесь завершения формирования ответа");
        }
    }
}
