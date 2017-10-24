package ru.ibs.updater;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author NAnishhenko
 */
@Component
public class UpdaterMainImpl implements UpdaterMain {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    TransactionTemplate tx;

    private static final int PARTITION_SIZE = 1024;

    @Override
    public boolean update(String sql, boolean loop) throws Exception {
        System.out.println("---------------------------------------------------");
        System.out.println("---------------------------------------------------");
        System.out.println(sql.replace(";", ""));
        System.out.println("---------------------------------------------------");
        System.out.println("---------------------------------------------------");
        final List<String> sqlList = tx.execute(new TransactionCallback<List<String>>() {
            @Override
            public List<String> doInTransaction(TransactionStatus ts) {
                Session session = sessionFactory.openSession();
//                List<String> list = (List<String>) session.createSQLQuery("select\n"
//                        + "q'[update pmp_simple_service set doctor_job_id = ']'||trim(s.PCOD)||q'[' where id=]'||ss.id||';' as sql_update\n"
//                        + "from pmp_parcel p\n"
//                        + "inner join ( select PARCEL_ID,recid,SN_POL,D_U,RSLT,ISHOD,K_U,PCOD,DS,COD,D_TYPE,FIL_ID, INVOICE_ID from PMP_PARCEL_S union all\n"
//                        + "select PARCEL_ID,recid,SN_POL,D_U,RSLT,ISHOD,K_U,PCOD,DS,COD,D_TYPE,FIL_ID,INVOICE_ID from PMP_PARCEL_SXX ) s on p.id = s.parcel_id\n"
//                        + "inner join pmp_invoice inv on inv.ID = s.INVOICE_ID\n"
//                        + "inner join pmp_medical_case mc on mc.id = inv.case_id\n"
//                        + "inner join pmp_simple_service ss on ss.case_id=mc.id").list();
                List<String> list = (List<String>) session.createSQLQuery(sql.replace(";", ""))
                        //.setFirstResult(0).setMaxResults(10000)
                        .list();
                session.close();
                return list;
            }
        });
        System.out.println("sqlList size = " + sqlList.size());
        if (sqlList.isEmpty()) {
            return false;
        }
        Set<String> sqlSetHash = new HashSet<String>(sqlList);
        final Set<String> sqlSet = new TreeSet<String>(sqlSetHash);
        Long counter = 0L;
        for (Collection<String> sqlCollection : Iterables.partition(sqlSet, PARTITION_SIZE)) {
            tx.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus ts) {
                    Session session = sessionFactory.openSession();
                    for (String sql : sqlCollection) {
                        session.createSQLQuery(sql.replace(";", "")).executeUpdate();
                    }
                    session.flush();
                    session.close();
                }
            }
            );
            counter += Integer.valueOf(PARTITION_SIZE).longValue();
            System.out.println(counter + " rows have been commited!");
        }
        System.out.println("All rows have been commited!");
        if (!loop) {
            Thread.sleep(10000);
        }
        return true;
    }
}
