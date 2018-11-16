package ru.ibs.pmp.medicalcaserepairer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.medicalcaserepairer.bean.ParcelInvoiceBean;
import ru.ibs.pmp.medicalcaserepairer.bean.ParcelInvoiceDiagnosisQueryBean;
import ru.ibs.pmp.medicalcaserepairer.stages.RepairCommon;
import ru.ibs.pmp.module.pmp.bill.recreate.RecreateImpl;
import ru.ibs.pmp.module.pmp.bill.recreate.RecreateUtils;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.StopwatchBean;
import ru.ibs.pmp.module.pmp.bill.recreate.params.AppInputParams;
import ru.ibs.pmp.module.pmp.bill.recreate.utils.SerializeService;

/**
 * @author NAnishhenko
 */
@Service
public class ParcelInvoiceRepairer {

    @Autowired
    @Qualifier("pmpTransactionTemplate")
    TransactionTemplate tx;
    @Autowired
    @Qualifier("pmpSessionFactory")
    SessionFactory sessionFactory;
    @Autowired
    SerializeService serializeUtils;
    @Autowired
    AppInputParams appInputParams;
    @Autowired
    RecreateUtils recreateUtils;
    @Autowired
    protected RepairCommon recreateCommon;
    @Autowired
    protected DbInit dbInit;

    private static final int SLICE = 4096;

    private static final String processName = "REPAIR_PARCEL_INVOICES";

    public void init() throws ParseException {
        dbInit.init(processName);
//        cacheFile = serializeUtils.getSerFile(VERSION_NUMBER_BEAN);
//        recreateUtils.logMessage("SerializedObjectsDirName = " + cacheFile.getAbsolutePath() + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
    }

    public void repair(Date period, Date dateFrom, Date dateTo) {
        StopwatchBean stopwatch = recreateCommon.createStopWatch();
        List<Object[]> diagnosisQueryResult = execQuery(diagnosisQuery, ImmutableMap.<String, Object>builder().put("period", period).put("dateFrom", dateFrom).put("dateTo", dateTo).build());
        List<ParcelInvoiceDiagnosisQueryBean> parcelInvoiceDiagnosisQueryBeanList = diagnosisQueryResult.stream().map(objArr
                -> new ParcelInvoiceDiagnosisQueryBean(((Number) objArr[0]).longValue(), ((Number) objArr[1]).longValue(),
                        ((Number) objArr[7]).longValue(), ((Number) objArr[8]).longValue())).collect(Collectors.toList());
        if (!parcelInvoiceDiagnosisQueryBeanList.isEmpty()) {
            for (ParcelInvoiceDiagnosisQueryBean parcelInvoiceDiagnosisQueryBean : parcelInvoiceDiagnosisQueryBeanList) {
                Long revisionId = parcelInvoiceDiagnosisQueryBean.getRevisionId();
                Long nullInvoicesCountOfparcelS = parcelInvoiceDiagnosisQueryBean.getNullInvoicesCountOfparcelS();
                Long nullInvoicesCountOfparcelSXX = parcelInvoiceDiagnosisQueryBean.getNullInvoicesCountOfparcelSXX();
                if (nullInvoicesCountOfparcelS > 0L) {
                    List<Object[]> parcelsQueryResult = execQuery(parcelsQuery, ImmutableMap.<String, Object>builder().put("rev", revisionId).build());
                    List<ParcelInvoiceBean> parcelInvoiceBeanList = parcelsQueryResult.stream().map(objArr -> new ParcelInvoiceBean(((Number) objArr[0]).longValue(), ((Number) objArr[1]).longValue())).collect(Collectors.toList());
                    int execUpdateQuery = execUpdateQuery(updateParcelsQuery, parcelInvoiceBeanList);
                    recreateUtils.logMessage(formatDates(period, dateFrom, dateTo) + ": " + execUpdateQuery + " parcelS " + getWasWere(execUpdateQuery) + " updated!", true, stopwatch, null, RecreateImpl.LogType.INFO, null);
                }
                if (nullInvoicesCountOfparcelSXX > 0L) {
                    List<Object[]> parcelsxxQueryResult = execQuery(parcelsxxQuery, ImmutableMap.<String, Object>builder().put("rev", revisionId).build());
                    List<ParcelInvoiceBean> parcelInvoiceBeanList = parcelsxxQueryResult.stream().map(objArr -> new ParcelInvoiceBean(((Number) objArr[0]).longValue(), ((Number) objArr[1]).longValue())).collect(Collectors.toList());
                    int execUpdateQuery = execUpdateQuery(updateParcelsxxQuery, parcelInvoiceBeanList);
                    recreateUtils.logMessage(formatDates(period, dateFrom, dateTo) + ": " + execUpdateQuery + " parcelSXX " + getWasWere(execUpdateQuery) + " updated!", true, stopwatch, null, RecreateImpl.LogType.INFO, null);
                }
            }
        } else {
            recreateUtils.logMessage(formatDates(period, dateFrom, dateTo) + " no data was found!", true, stopwatch, null, RecreateImpl.LogType.INFO, null);
        }
    }

    private String getWasWere(int execUpdateQuery) {
        return execUpdateQuery > 1 ? "were" : "was";
    }

    private String formatDates(Date period, Date dateFrom, Date dateTo) {
        return new SimpleDateFormat("yyyy-MM").format(period) + " " + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(dateFrom) + " " + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(dateTo);
    }

    private List<Object[]> execQuery(String query, Map<String, Object> parameters) {
        return tx.execute(status -> {
            List<Object[]> listDb = null;
            Session session = sessionFactory.openSession();
            try {
                SQLQuery sqlQuery = session.createSQLQuery(query);
                for (Entry<String, Object> entry : parameters.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    sqlQuery.setParameter(key, value);
                }
                listDb = sqlQuery.list();
            } finally {
                session.close();
            }
            return listDb;
        });
    }

    private int execUpdateQuery(String query, List<ParcelInvoiceBean> parcelInvoiceBeanList) {
        int updatedRows = 0;
        for (final List<ParcelInvoiceBean> parcelInvoiceBeanListSlice : Iterables.partition(parcelInvoiceBeanList, SLICE)) {
            Transaction tx = null;
            Session session = sessionFactory.openSession();
            try {
                tx = session.getTransaction();
                tx.begin();
                for (ParcelInvoiceBean parcelInvoiceBean : parcelInvoiceBeanListSlice) {
                    updatedRows += session.createSQLQuery(query).setParameter("invoiceId", parcelInvoiceBean.getInvoiceId()).setParameter("id", parcelInvoiceBean.getParcelsId()).executeUpdate();
                }
                tx.commit();
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
            } finally {
                session.close();
            }
        }
        return updatedRows;
    }
    String updateParcelsQuery = "update pmp_parcel_s set invoice_id=:invoiceId where id=:id";
    String updateParcelsxxQuery = "update pmp_parcel_sxx set invoice_id=:invoiceId where id=:id";

    String diagnosisQuery = "with main_q as(\n"
            + "select \n"
            + "st.id,st.rev,st.created,re.mo_id,re.period,\n"
            + "nvl((select sum(case when ps1.invoice_id is not null then 1 else 0 end) from pmp_parcel_s ps1 inner join pmp_parcel p1 on p1.id=ps1.parcel_id where ps1.parcel_id=p1.id and p1.version_number=st.rev ),0) as not_null_s,\n"
            + "nvl((select sum(case when psxx1.invoice_id is not null then 1 else 0 end) from pmp_parcel_sxx psxx1 inner join pmp_parcel p2 on p2.id=psxx1.parcel_id where psxx1.parcel_id=p2.id and p2.version_number=st.rev),0) as not_null_sxx,\n"
            + "nvl((select sum(case when ps2.invoice_id is null then 1 else 0 end) from pmp_parcel_s ps2 inner join pmp_parcel p3 on p3.id=ps2.parcel_id where ps2.parcel_id=p3.id and p3.version_number=st.rev),0) as is_null_s,\n"
            + "nvl((select sum(case when psxx2.invoice_id is null then 1 else 0 end) from pmp_parcel_sxx psxx2 inner join pmp_parcel p4 on p4.id=psxx2.parcel_id where psxx2.parcel_id=p4.id and p4.version_number=st.rev),0) as is_null_sxx,\n"
            + "(select count(*) from pmp_invoice_aud inva where inva.rev=st.rev and inva.bill_id in (select id from pmp_bill b where b.requirement_id=re.id and b.payer_ogrn='UNKNOWN')) as unknown_invoice_count,\n"
            + "st.invoice_count\n"
            + "from pmp_bill_statistics st\n"
            + "inner join pmp_requirement re on st.requirement_id=re.id\n"
            + "where st.created between :dateFrom and :dateTo\n"
            + "and re.period=:period\n"
            + "and st.rev is not null and st.finished is not null\n"
            + "order by st.created desc\n"
            + ")\n"
            + "select\n"
            + "id,rev,created,mo_id,to_char(period,'yyyy-MM-dd') as period,\n"
            + "not_null_s,not_null_sxx,is_null_s,is_null_sxx,unknown_invoice_count,invoice_count, (not_null_s+not_null_sxx+unknown_invoice_count) as inv_sum\n"
            + "from main_q\n"
            + "where is_null_s<>0 or is_null_sxx<>0";

    String parcelsQuery = "select ps.id as parcel_s_id,inva.id as invoice_id,ps.invoice_id as parcel_invoice_id,ps.recid,inva.recid as inva_recid\n"
            + "from pmp_parcel p\n"
            + "inner join pmp_parcel_s ps on ps.parcel_id=p.id\n"
            + "inner join pmp_invoice_aud inva on inva.recid=ps.recid and inva.rev=p.version_number\n"
            + "where p.version_number=:rev and ps.invoice_id is null\n"
            + "order by inva.id";

    String parcelsxxQuery = "select ps.id as parcel_s_id,inva.id as invoice_id,ps.invoice_id as parcel_invoice_id,ps.recid,inva.recid as inva_recid\n"
            + "from pmp_parcel p\n"
            + "inner join pmp_parcel_sxx ps on ps.parcel_id=p.id\n"
            + "inner join pmp_invoice_aud inva on inva.recid=ps.recid and inva.rev=p.version_number\n"
            + "where p.version_number=:rev and ps.invoice_id is null\n"
            + "order by inva.id";
}
