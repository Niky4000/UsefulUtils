/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang.time.DateUtils;
import ru.ibs.pmp.auth.utils.AuthApiUtils;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class BillFlkDAOHibernateTest {

    static SessionFactoryInterface sessionFactory;

    public static void test() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            List<String> flkVersions = getFlkVersions(4102L, new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01"), 77056L);
            flkVersions.stream().forEach(System.out::println);
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    private static List<String> getFlkVersions(Long moId, Date period, Long parcelId) {
        if (AuthApiUtils.isLpuRequest()) {
            return Arrays.asList("0");
        }
        List<Object[]> objList = (List<Object[]>) sessionFactory.getCurrentSession().createSQLQuery("select 0 as id, p.send_date as flk_date\n"
                + "from pmp_parcel p where p.period=:period and p.lpu_id=:moId and p.id=:parcelId and p.is_flk=:flk\n"
                + "union all\n"
                + "select fv.id, fv.flk_date\n"
                + "from pmp_bill_flk_versions fv\n"
                + "where fv.period=:period and fv.mo_id=:moId and fv.parcel_id=:parcelId")
                .setParameter("parcelId", parcelId).setParameter("period", period).setParameter("moId", moId).setParameter("flk", 1).list();
        Collections.sort(objList, (arr1, arr2) -> {
            Date date1 = (Date) arr1[1];
            Date date2 = (Date) arr2[1];
            if (date1 == null && date2 != null) {
                return 1;
            } else if (date1 != null && date2 == null) {
                return -1;
            } else if (date1 == null && date2 == null) {
                Long n1 = ((Number) arr1[0]).longValue();
                Long n2 = ((Number) arr2[0]).longValue();
                return -n1.compareTo(n2);
            } else {
                Date truncate1 = DateUtils.truncate(date1, Calendar.DAY_OF_MONTH);
                Date truncate2 = DateUtils.truncate(date2, Calendar.DAY_OF_MONTH);
                int k = -truncate1.compareTo(truncate2);
                if (k != 0) {
                    return k;
                } else {
                    Long n1 = ((Number) arr1[0]).longValue();
                    Long n2 = ((Number) arr2[0]).longValue();
                    return -n1.compareTo(n2);
                }
            }
        });
        return objList.stream().map(objArray -> ((Number) objArray[0]).toString() + " от " + Optional.ofNullable((Date) objArray[1]).map(date -> new SimpleDateFormat("dd/MM/yyyy").format(date)).orElse("--/--/----")).collect(Collectors.toList());
    }
}
