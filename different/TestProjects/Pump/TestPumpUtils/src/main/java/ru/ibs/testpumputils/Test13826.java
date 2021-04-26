package ru.ibs.testpumputils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class Test13826 {

    static SessionFactoryInterface sessionFactory;

    public static void test(String args[]) throws Exception {
        Long lpuId = Long.valueOf(args[0]);
        Date period = new SimpleDateFormat("yyyy-MM-dd").parse(args[1]);
        Long rev = Long.valueOf(args[2]);
        Date created = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(args[3]);
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            List<Number> parcelIdListDb = Db.select(sessionFactory, session -> (List<Number>) session.createSQLQuery("select id from pmp_parcel where version_number=:rev and lpu_id=:lpuId and period=:period and creation_date>:creationDate and exists(select 1 from pmp_bill where id=bill_id and amount>0) order by creation_date desc")
                    .setParameter("rev", rev).setParameter("lpuId", lpuId).setParameter("period", period).setParameter("creationDate", created).list());
            if (!parcelIdListDb.isEmpty()) {
                String sql = null;
                try (InputStream inputStream = Test13826.class.getClassLoader().getResourceAsStream("13826_query.sql")) {
                    List<String> sqlList = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
                    sql = sqlList.stream().reduce("", (str1, str2) -> str1 + "\n" + str2);
                }
                List<Long> parcelIdList = parcelIdListDb.stream().map(Number::longValue).sorted().collect(Collectors.toList());
                String sql2 = Arrays.stream(sql.split("\n")).map(str -> removeComments(str)).reduce("", (str1, str2) -> str1 + "\n" + str2);
                for (Long parcelId : parcelIdList) {
                    List<Object[]> select = Db.select(sessionFactory, session -> (List<Object[]>) session.createSQLQuery(sql2).setParameter("lpuId", lpuId).setParameter("period", period).setParameter("parcelId", parcelId).list());
                    if (!select.isEmpty()) {
                        System.out.println("Errors for parcelId = " + parcelId.toString() + ":");
                        for (Object[] objArray : select) {
                            System.out.println(Arrays.stream(objArray).map(obj -> objToString(obj)).reduce("", (str1, str2) -> str1 + " " + str2));
                        }
                        System.out.println();
                        System.out.println();
                    } else {
                        System.out.println("There are no errors for parcelId = " + parcelId.toString() + "!");
                    }
                }
            } else {
                System.out.println("parcelId List is Empty! Apparently parameters are wrong!");
            }
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    private static String removeComments(String sqlStr) {
        if (sqlStr.startsWith("--")) {
            return "";
        } else if (sqlStr.contains("--")) {
            return sqlStr.substring(0, sqlStr.indexOf("--"));
        } else {
            return sqlStr;
        }
    }

    private static String objToString(Object obj) {
        return obj != null ? obj.toString() : "null";
    }
}
