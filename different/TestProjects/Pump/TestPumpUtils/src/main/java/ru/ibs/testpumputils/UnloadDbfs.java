/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import com.google.common.io.Files;
import java.io.File;
import ru.ibs.testpumputils.bean.UnloadZipBean;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class UnloadDbfs {

    static SessionFactoryInterface sessionFactory;

    public static void unload() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        Date period = new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01");
        String ogrn = "1027739099772";
//        String ogrn="1027739449913";
        try {
            Session session = sessionFactory.openSession();
            try {
                List<UnloadZipBean> dbList = (List<UnloadZipBean>) session.createSQLQuery("select ma.id,ma.name,ma.payload,ma.mo_id from PMP_MAILGW_ATTACHMENT ma \n"
                        + "inner join pmp_mailgw_log ml on ml.id=ma.log_entry_id and ml.period=ma.period and ml.mo_id=ma.mo_id\n"
                        + "inner join pmp_parcel p on p.id=ml.parcel_id and p.period=ml.period and p.lpu_id=ml.mo_id\n"
                        + "inner join pmp_bill b on b.last_send_parcel_id=p.id and b.mo_id=p.lpu_id and b.period=p.period\n"
                        + "left join MOSPRSMO smo on smo.q_ogrn=b.payer_ogrn\n"
                        + "where ma.period=:period and direction=:direction and b.payer_ogrn=:ogrn").addEntity(UnloadZipBean.class)
                        .setParameter("period", period).setParameter("direction", "OUT").setParameter("ogrn", ogrn).list();
                for (UnloadZipBean obj : dbList) {
                    String name = obj.getName();
                    byte[] bytes = obj.getPayload();
                    String lpuId = obj.getMoId();
                    Files.write(bytes, new File("/home/me/tmp/parcels/" + name));
                    System.out.println("lpuId = " + lpuId + " name = " + name + "!");
                };
            } finally {
                session.close();
            }
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
