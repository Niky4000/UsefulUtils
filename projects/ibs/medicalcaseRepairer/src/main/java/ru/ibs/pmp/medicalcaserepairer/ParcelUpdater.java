package ru.ibs.pmp.medicalcaserepairer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.medicalcaserepairer.stages.RepairCommon;
import ru.ibs.pmp.module.pmp.bill.recreate.RecreateImpl;
import ru.ibs.pmp.module.pmp.bill.recreate.RecreateUtils;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.StopwatchBean;
import ru.ibs.pmp.module.pmp.bill.recreate.utils.SerializeService;
import ru.ibs.pmp.parcel.update.bean.InvoiceBean;
import ru.ibs.pmp.parcel.update.bean.MedicalCaseBean;
import ru.ibs.pmp.parcel.update.bean.ParcelUpdateBean;

/**
 * @author NAnishhenko
 */
@Service
public class ParcelUpdater {

    @Autowired
    @Qualifier("pmpTransactionTemplate")
    TransactionTemplate tx;
    @Autowired
    @Qualifier("pmpSessionFactory")
    SessionFactory sessionFactory;
    @Autowired
    SerializeService serializeUtils;
    @Autowired
    RecreateUtils recreateUtils;
    @Autowired
    protected RepairCommon recreateCommon;
    @Autowired
    protected DbInit dbInit;
    private static final String processName = "PARCEL_UPDATER";
    private static final int MAX_RESULTS = 65536;
    private static final int SLICE = 4096;

    public void init() throws ParseException {
        dbInit.init(processName);
        recreateUtils.logMessage("ParcelUpdater was inited!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
    }

    public void updateParcels(int year, int month, int endYear, int endMonth) throws InterruptedException {
        StopwatchBean globalStopWatch = recreateCommon.createStopWatch();
        final LocalDateTime endDate = LocalDateTime.of(endYear, endMonth, 1, 0, 0, 0);
        LocalDateTime currentDate = LocalDateTime.of(year, month, 1, 0, 0, 0);
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            recreateUtils.logMessage(currentDate.getYear() + "-" + formatMonth(currentDate.getMonthValue()) + " was started!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
            recreateUtils.logMessage("pmp_parcel_s is under process now!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
            getObjectsFromDb("pmp_parcel_s", Date.from(currentDate.toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now()))));
            recreateUtils.logMessage("pmp_parcel_sxx is under process now!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
            getObjectsFromDb("pmp_parcel_sxx", Date.from(currentDate.toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now()))));
            recreateUtils.logMessage(currentDate.getYear() + "-" + formatMonth(currentDate.getMonthValue()) + " was finished!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
            currentDate = currentDate.plusMonths(1L);
        }
        dbInit.finalizationActions(globalStopWatch, recreateUtils.getBillStatistics().getId());
    }

    private String formatMonth(int month) {
        String ret = month + "";
        for (int i = 2; i > (month + "").length(); i--) {
            ret = "0" + ret;
        }
        return ret;
    }

    private String getWasWere(int size) {
        return size > 1 ? " were" : " was";
    }

    private void getObjectsFromDb(final String tableName, final Date period) throws TransactionException {

        int totalAmountOfUpdatedData = 0;
        List<Object[]> requirementIdList = getDataFromDb(
                "select re.id,re.mo_id,rownum as rr from pmp_requirement re where re.period=:period", //
                ImmutableMap.<String, Object>builder().put("period", period).build());

//        Set<Long> requirementIdSet = requirementIdList.stream().map(objs -> ((Number) objs[0]).longValue()).collect(Collectors.toCollection(TreeSet::new));
//        Map<Long, String> requirementIdMapToLpuId = requirementIdList.stream().collect(Collectors.groupingBy(objs -> ((Number) objs[0]).longValue(), TreeMap::new, Collectors.collectingAndThen(Collectors.toList(), ff -> (String) ff.get(0)[1])));
        Map<String, Long> requirementIdMapToLpuId = requirementIdList.stream().collect(Collectors.groupingBy(objs -> (String) objs[1], TreeMap::new, Collectors.collectingAndThen(Collectors.toList(), ff -> ((Number) ff.get(0)[0]).longValue())));

        for (Entry<String, Long> entry : requirementIdMapToLpuId.entrySet()) {

            String lpuId = entry.getKey();
            Long requirementId = entry.getValue();

            List<Object[]> versionList = getDataFromDb(
                    "select distinct p.version_number,rownum as rr\n" //
                    + "from " + tableName + " ps\n" //
                    + "inner join pmp_parcel p on p.id=ps.parcel_id\n" //
                    + "inner join pmp_bill b on b.id=p.bill_id\n" //
                    + "inner join pmp_requirement re on re.id=b.requirement_id\n" //
                    + "where re.id=:requirementId\n" //
                    + "order by p.version_number", //
                    ImmutableMap.<String, Object>builder().put("requirementId", requirementId).build());

            Set<Long> allUniqueVersions = versionList.stream().map(objs -> ((Number) objs[0]).longValue()).collect(Collectors.toCollection(TreeSet::new));

            recreateUtils.logMessage("allUniqueVersions size = " + allUniqueVersions.size() + " for lpuId = " + lpuId + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);

            int fullSize = 0;
            for (Long version : allUniqueVersions) {

                List<Object[]> objList = getDataFromDb(
                        "select ps.id,ps.invoice_id,p.version_number,rownum as rr\n" //
                        + "from " + tableName + " ps\n" //
                        + "inner join pmp_parcel p on p.id=ps.parcel_id\n" //
                        + "inner join pmp_bill b on b.id=p.bill_id\n" //
                        + "inner join pmp_requirement re on re.id=b.requirement_id\n" //
                        + "where re.id=:requirementId and p.version_number=:version\n" //
                        + "order by ps.id", //
                        ImmutableMap.<String, Object>builder().put("requirementId", requirementId).put("version", version).build());

                recreateUtils.logMessage(tableName + " of " + objList.size() + getWasWere(objList.size()) + " got! lpuId = " + lpuId + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);

                if (objList.isEmpty()) {
                    continue;
                }

                List<ParcelUpdateBean> parcelUpdateBeanList = new ArrayList<>(objList.size());
                for (Object[] objs : objList) {
                    parcelUpdateBeanList.add(new ParcelUpdateBean(((Number) objs[0]).longValue(), ((Number) objs[1]).longValue(), ((Number) objs[2]).longValue()));
                }
//            Set<Long> allUniqueVersions = parcelUpdateBeanList.stream().map(ParcelUpdateBean::getVersionNumber).collect(Collectors.toCollection(TreeSet::new));

                List<ParcelUpdateBean> parcelUpdateBeanListOfParticularVersion = parcelUpdateBeanList.stream().filter(parcelUpdateBean -> parcelUpdateBean.getVersionNumber().equals(version)).collect(Collectors.toList());

                List<InvoiceBean> invoiceData = new ArrayList<>();
//            for (List<Long> versionSliceList : Iterables.partition(allUniqueVersions, SLICE)) {
                List<Object[]> objList2 = getDataFromDb("select inv.id,inv.invoice_sum,inv.case_id,rownum as rr from pmp_invoice_aud inv where inv.rev in(:rev) order by inv.id,inv.rev", //
                        ImmutableMap.<String, Object>builder().put("rev", version).build());
                for (Object[] objs : objList2) {
                    invoiceData.add(new InvoiceBean(((Number) objs[0]).longValue(), ((Number) objs[2]).longValue(), ((Number) objs[1]).longValue()));
                }
//            }

                recreateUtils.logMessage("pmp_invoice_aud of " + invoiceData.size() + getWasWere(invoiceData.size()) + " got! lpuId = " + lpuId + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);

                List<MedicalCaseBean> caseData = new ArrayList<>();
//            for (List<Long> versionSliceList : Iterables.partition(allUniqueVersions, SLICE)) {
                List<Object[]> objList3 = getDataFromDb("select mc.id as mc_id,mc.case_type,rownum as rr from pmp_medical_case_aud mc where mc.rev in(:rev) order by mc.id,mc.rev", //
                        ImmutableMap.<String, Object>builder().put("rev", version).build());
                for (Object[] objs : objList3) {
                    caseData.add(new MedicalCaseBean(((Number) objs[0]).longValue(), (String) objs[1]));
                }
//            }
                recreateUtils.logMessage("pmp_medical_case_aud of " + caseData.size() + getWasWere(caseData.size()) + " got! lpuId = " + lpuId + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);

                Map<Long, InvoiceBean> invoiceDataMapByInvoiceId = invoiceData.stream().collect(Collectors.groupingBy(InvoiceBean::getInvoiceId, Collectors.collectingAndThen(Collectors.toList(), ff -> ff.get(0))));
                recreateUtils.logMessage("invoiceDataMapByInvoiceId created!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
                Map<Long, MedicalCaseBean> caseDataMapById = caseData.stream().collect(Collectors.groupingBy(MedicalCaseBean::getMedicalCaseId, Collectors.collectingAndThen(Collectors.toList(), ff -> ff.get(0))));
                recreateUtils.logMessage("caseDataMapById created!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);

                recreateUtils.logMessage("handling in progress!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
                for (ParcelUpdateBean parcelUpdateBean : parcelUpdateBeanListOfParticularVersion) {
                    InvoiceBean invoice = invoiceDataMapByInvoiceId.get(parcelUpdateBean.getInvoiceId());
                    if (invoice == null) {

                        continue;
                    }
                    MedicalCaseBean medicalCase = caseDataMapById.get(invoice.getCaseId());
                    if (medicalCase == null) {
                        recreateUtils.logMessage("medicalCase id=" + invoice.getCaseId().toString() + " wasn't found for parcelId=" + parcelUpdateBean.getId().toString() + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
                        continue;
                    }
                    parcelUpdateBean.setInvoiceSum(BigDecimal.valueOf(invoice.getInvoiceSum()).divide(BigDecimal.valueOf(100L)));
                    parcelUpdateBean.setMedicalCaseId(medicalCase.getMedicalCaseId());
                    parcelUpdateBean.setMedicalCaseType(medicalCase.getMedicalCaseType());
                }
                List<ParcelUpdateBean> parcelUpdateBeanListOfParticularVersionFiltered = parcelUpdateBeanListOfParticularVersion.stream().filter(parcelUpdateBean
                        -> parcelUpdateBean.getInvoiceId() != null
                        && parcelUpdateBean.getInvoiceSum() != null
                        && parcelUpdateBean.getMedicalCaseId() != null
                        && parcelUpdateBean.getMedicalCaseType() != null)
                        .collect(Collectors.toList());
                recreateUtils.logMessage("handling was finished!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);

                Integer updatedRowsCount = 0;
                for (final List<ParcelUpdateBean> parcelUpdateBeanListSlice : Iterables.partition(parcelUpdateBeanListOfParticularVersionFiltered, SLICE)) {
                    updatedRowsCount += tx.execute(status -> {
                        Session session = sessionFactory.openSession();
                        try {
                            int executeUpdate = 0;
                            for (ParcelUpdateBean parcelUpdateBean : parcelUpdateBeanListSlice) {
                                executeUpdate += session.createSQLQuery("update " + tableName + " set case_id=:caseId,service_sum=:serviceSum,case_type=:caseType where id=:id")
                                        .setParameter("caseId", parcelUpdateBean.getMedicalCaseId())
                                        .setParameter("serviceSum", parcelUpdateBean.getInvoiceSum())
                                        .setParameter("caseType", parcelUpdateBean.getMedicalCaseType())
                                        .setParameter("id", parcelUpdateBean.getId())
                                        .executeUpdate();
                            }
                            session.flush();
                            return executeUpdate;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            session.close();
                        }
                        return null;
                    });
                    fullSize += parcelUpdateBeanListSlice.size();
                    recreateUtils.logMessage("parcelUpdateBeanListSlice of " + parcelUpdateBeanListSlice.size() + " updated!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
                }
                recreateUtils.logMessage(tableName + " of " + updatedRowsCount + getWasWere(updatedRowsCount) + " updated! lpuId = " + lpuId + "! Version = " + version.toString() + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
            }
            totalAmountOfUpdatedData += fullSize;
            recreateUtils.logMessage("All versions for " + tableName + " of " + fullSize + getWasWere(fullSize) + " updated! lpuId = " + lpuId + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
        }
        recreateUtils.logMessage("totalAmountOfUpdatedData=" + totalAmountOfUpdatedData + "! period = " + new SimpleDateFormat("yyyy-MM").format(period) + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
    }

    private List<Object[]> getDataFromDb(String sql, final Map<String, Object> parameters) throws TransactionException {
        List<Object[]> objList = new ArrayList<>();
        AtomicInteger start = new AtomicInteger(0);
        AtomicInteger end = new AtomicInteger(start.get() + MAX_RESULTS);
        List<Object[]> execute = null;
        while (execute == null || !execute.isEmpty()) {
            execute = tx.execute(status -> {
                Session session = sessionFactory.openSession();
                try {
                    SQLQuery sqlQuery = session.createSQLQuery("select * from(\n" //
                            + sql
                            + ") where rr>:beginRow and rr<=:endRow" //
                    );
                    sqlQuery.setParameter("beginRow", start.get());
                    sqlQuery.setParameter("endRow", end.get());
                    parameters.forEach((key, value) -> {
                        if (value instanceof List) {
                            sqlQuery.setParameterList(key, (List) value);
                        } else {
                            sqlQuery.setParameter(key, value);
                        }
                    });
                    List<Object[]> objListDb = sqlQuery.list();
                    return objListDb;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    session.close();
                }
                return null;
            });
            start.addAndGet(execute.size());
            end.addAndGet(execute.size());
            objList.addAll(execute);
            recreateUtils.logMessage("Rows of " + objList.size() + getWasWere(objList.size()) + " got!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
        }
        return objList;
    }

//    
//    
//    
//    private <T> List<T> getObjectsFromDb(final Class<T> objClass, final int firstResult) throws TransactionException {
//        return tx.execute(status -> {
//            Session session = sessionFactory.openSession();
//            try {
//                
//                List<T> objListDb = session.createCriteria(objClass)
//                        .add(
//                        Restrictions.or(
//                                Restrictions.isNull("caseId"),
//                                Restrictions.isNull("serviceSum"),
//                                Restrictions.isNull("caseType"))
//                ).setFirstResult(firstResult).setMaxResults(MAX_RESULTS).list();
//                return objListDb;
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                session.close();
//            }
//            return null;
//        });
//    }
}
