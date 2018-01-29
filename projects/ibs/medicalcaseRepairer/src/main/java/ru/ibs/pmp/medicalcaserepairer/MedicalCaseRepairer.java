package ru.ibs.pmp.medicalcaserepairer;

import com.google.common.collect.Iterables;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.api.model.BillFlkInvoice;
import ru.ibs.pmp.api.model.BillStatistics;
import ru.ibs.pmp.api.model.MedicalCaseAud;
import ru.ibs.pmp.api.model.Requirement;
import ru.ibs.pmp.medicalcaserepairer.bean.MedicalCaseWrapper;
import ru.ibs.pmp.medicalcaserepairer.bean.VersionNumberBean;
import ru.ibs.pmp.medicalcaserepairer.stages.RepairCommon;
import ru.ibs.pmp.module.pmp.bill.recreate.RecreateImpl;
import ru.ibs.pmp.module.pmp.bill.recreate.RecreateUtils;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.BillStatisticsBean;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.StopwatchBean;
import ru.ibs.pmp.module.pmp.bill.recreate.log.LogThread;
import ru.ibs.pmp.module.pmp.bill.recreate.log.TimerThread;
import ru.ibs.pmp.module.pmp.bill.recreate.params.AppInputParams;
import ru.ibs.pmp.module.pmp.bill.recreate.utils.SerializeService;

/**
 * @author NAnishhenko
 */
@Service
public class MedicalCaseRepairer {

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
    LogThread logThread;
    File cacheFile;
    // Constants
    private static final String VERSION_NUMBER_BEAN = "versionNumberBean";
    private static final int SLICE = 500;
    private static final int LOG_INTERVAL = 1000;
    private static final BigDecimal div = BigDecimal.valueOf(100L);
    private static Date initDate;
    private static String initLpuId;
    private static final String processName = "REPAIR_MEDICAL_CASES";

    public static void main(String args[]) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("repair_module.xml");
        MedicalCaseRepairer medicalCaseRepairer = applicationContext.getBean(MedicalCaseRepairer.class);
        medicalCaseRepairer.init();
        boolean anyMatch = Arrays.stream(args).anyMatch(arg -> arg.equals("-d"));
        if (anyMatch) {
            medicalCaseRepairer.deleteCacheFile();
        }
        Long versionNumber = null;
        if (args.length == 1) {
            try {
                versionNumber = Long.valueOf(args[0]);
            } catch (Exception e) {
                // Ignore Exception!
            }
        }
        if (versionNumber == null) {
            medicalCaseRepairer.repairMedicalCase();
        } else {
            medicalCaseRepairer.handleSingleVersion(versionNumber);
        }
    }

    public void deleteCacheFile() {
        if (cacheFile.delete()) {
            recreateUtils.logMessage("cacheFile " + cacheFile.getAbsolutePath() + " was deleted!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
        } else {
            recreateUtils.logMessage("cacheFile " + cacheFile.getAbsolutePath() + " was not deleted!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
        }
    }

    public void handleSingleVersion(Long versionNumber) throws Exception, InterruptedException {
        StopwatchBean globalStopWatch = recreateCommon.createStopWatch();
        handleVersion(versionNumber, getLastVersionNumbers());
        finalizationActions(globalStopWatch, recreateUtils.getBillStatistics().getId());
    }

    public void init() throws ParseException {
        initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2000-01-01 00:00:00");
        initLpuId = "0000";
        appInputParams.init(initLpuId, initDate);
        recreateUtils.logMessage("Hello!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
        cacheFile = serializeUtils.getSerFile(VERSION_NUMBER_BEAN);
        recreateUtils.logMessage("SerializedObjectsDirName = " + cacheFile.getAbsolutePath() + "!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
        logThread = initLogger();
        recreateUtils.setLogThread(logThread);
        logThread.setBillStatisticsTimeList(recreateUtils.getBillStatisticsTimeList());
        recreateCommon.setSessionFactory(sessionFactory);
        recreateCommon.setTx(tx);
        recreateCommon.setRecreateUtils(recreateUtils);
        Requirement requirement = createOrGetFakeRequirement();
        BillStatisticsBean billStatistics = recreateCommon.createBillStatistics(requirement, initLpuId, initDate, BillStatistics.BillOperation.OTHERS, null, processName, false);
        recreateUtils.setBillStatistics(billStatistics.getBillStatistics());
        recreateUtils.logMessage("billStatistics with id = " + billStatistics.getBillStatistics().getId().toString() + " was created!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
    }

    private LogThread initLogger() {
        LogThread logThread = new LogThread(recreateCommon);
        logThread.setDaemon(false);
        logThread.start();
        TimerThread timerThread = new TimerThread(logThread);
        timerThread.setDaemon(true);
        timerThread.start();
        return logThread;
    }

    public void repairMedicalCase() throws InterruptedException {
        StopwatchBean globalStopWatch = recreateCommon.createStopWatch();
        StopwatchBean createStopWatch = recreateCommon.createStopWatch();
        Set<Long> lastVersionNumbers = getLastVersionNumbers();
        recreateUtils.logMessage("lastVersionNumbers set has been got of size = " + lastVersionNumbers.size() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);
        List<Long> versionNumberList = tx.execute(status -> {
            Session session = sessionFactory.openSession();
            List<Long> versionNumberListDb = session.createCriteria(BillFlkInvoice.class).setProjection(Projections.distinct(Projections.property("versionNumber"))).list();
            session.close();
            return versionNumberListDb;
        });
        VersionNumberBean cachedVersionNumberBean = serializeUtils.deSerializeObject(VersionNumberBean.class, VERSION_NUMBER_BEAN);
        VersionNumberBean versionNumberBean = new VersionNumberBean(new HashSet<>(versionNumberList), cachedVersionNumberBean);
        List<Long> unfinishedVersions = versionNumberBean.getUnfinishedVersions();
        recreateUtils.logMessage("versionNumberList size=" + versionNumberList.size() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);
        recreateUtils.logMessage("unfinishedVersions size=" + unfinishedVersions.size() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);
        int i = 0;
        for (Long versionNumber : unfinishedVersions) {
            boolean error = false;
            try {
                recreateUtils.logMessage("versionNumber = " + versionNumber.toString() + " is processing now...", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);
                handleVersion(versionNumber, lastVersionNumbers);
                i++;
                recreateUtils.logMessage("versionNumber = " + versionNumber.toString() + " has been handled! " + (unfinishedVersions.size() - i) + " remained...", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);
            } catch (Exception e) {
                recreateUtils.logMessage("handleVersion Error! versionNumber = " + versionNumber.toString(), true, createStopWatch, null, RecreateImpl.LogType.ERROR, e);
                error = true;
            }
            if (!error) {
                versionNumberBean.setFinished(versionNumber);
                serializeUtils.serializeObject(versionNumberBean, VERSION_NUMBER_BEAN);
            }
        }
        finalizationActions(globalStopWatch, recreateUtils.getBillStatistics().getId());
    }

    private void finalizationActions(StopwatchBean globalStopWatch, Long billStatisticsId) throws InterruptedException {
        recreateUtils.logMessage("billStatisticsId = " + billStatisticsId.toString() + "!", true, globalStopWatch, null, RecreateImpl.LogType.INFO, null);
        recreateUtils.logMessage("Finished!", true, globalStopWatch, null, RecreateImpl.LogType.INFO, null);
        logThread.interrupt();
        logThread.join();
        if (recreateUtils.getBillStatistics() != null) {
            recreateUtils.flushLogMessage();
        }
        updateBillStatisticsFinishedDate(billStatisticsId);
    }

    private void handleVersion(final Long versionNumber, Set<Long> lastVersionNumbers) throws Exception {
        StopwatchBean createStopWatch = recreateCommon.createStopWatch();
        List<BillFlkInvoice> billFlkInvoiceList = tx.execute(status -> {
            Session session = sessionFactory.openSession();
            List<BillFlkInvoice> billFlkInvoiceListDb = session.createCriteria(BillFlkInvoice.class).add(Restrictions.eq("versionNumber", versionNumber)).list();
            session.close();
            return billFlkInvoiceListDb;
        });
        recreateUtils.logMessage("billFlkInvoiceList size=" + billFlkInvoiceList.size() + " of version = " + versionNumber.toString() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);

        List<MedicalCaseAud> medicalCaseAudList = tx.execute(status -> {
            Session session = sessionFactory.openSession();
            List<MedicalCaseAud> medicalCaseAudListDb = session.createCriteria(MedicalCaseAud.class
            ).add(Restrictions.eq("revinfo.id", versionNumber)).list();
            session.close();
            return medicalCaseAudListDb;
        });
        recreateUtils.logMessage("medicalCaseAudList size=" + medicalCaseAudList.size() + " of version = " + versionNumber.toString() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);

        List<MedicalCaseWrapper> medicalCaseAudWrapperList = medicalCaseAudList.stream().map(MedicalCaseWrapper::new).collect(Collectors.toList());
        handleMedicalCaseWrapper(medicalCaseAudWrapperList, billFlkInvoiceList, versionNumber, "Aud");
        if (lastVersionNumbers.contains(versionNumber)) {
            recreateUtils.logMessage("lastVersion = " + versionNumber.toString() + " has been found!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);

//            List<MedicalCase> medicalCaseList = tx.execute(status -> {
//                Session session = sessionFactory.openSession();
//                List<MedicalCase> medicalCaseListDb = session.createCriteria(MedicalCase.class,
//                        "medicalCase")
//                        .createAlias("medicalCase.invoices", "invoice")
//                        .createAlias("invoice.bill", "bill")
//                        .createAlias("bill.parcels", "parcel")
//                        .add(Restrictions.eq("parcel.versionNumber", versionNumber))
//                        //                        .setProjection(Projections.distinct(Projections.property("medicalCase")))
//                        .list();
//                session.close();
//                return medicalCaseListDb;
//            });
//            recreateUtils.logMessage("medicalCaseList size=" + medicalCaseList.size() + " of version = " + versionNumber.toString() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);
            List<Object[]> medicalCaseList = getMedicalCases(versionNumber);
            recreateUtils.logMessage("medicalCaseList size=" + medicalCaseList.size() + " of version = " + versionNumber.toString() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);

            List<MedicalCaseWrapper> medicalCaseWrapperList = medicalCaseList.stream().map(MedicalCaseWrapper::new).collect(Collectors.toList());
            handleMedicalCaseWrapper(medicalCaseWrapperList, billFlkInvoiceList, versionNumber, "");
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private void handleMedicalCaseWrapper(List<MedicalCaseWrapper> medicalCaseList, List<BillFlkInvoice> billFlkInvoiceList, final Long versionNumber, String aud) {
        StopwatchBean createStopWatch = recreateCommon.createStopWatch();
        Map<Long, MedicalCaseWrapper> medicalCaseToIdMap = medicalCaseList.stream().filter(distinctByKey(MedicalCaseWrapper::getId))
                .collect(Collectors.toMap(medicalCase -> medicalCase.getId(), medicalCase -> medicalCase));
//        Map<Long, List<MedicalCaseWrapper>> collect = medicalCaseList.stream().collect(Collectors.groupingBy(medicalCase -> medicalCase.getId()));
        Map<Long, List<BillFlkInvoice>> medicalCaseIdToErrorList = billFlkInvoiceList.stream().collect(Collectors.groupingBy(billFlkInvoice -> billFlkInvoice.getCaseId()));

        List<MedicalCaseWrapper> medicalCaseForZeroUpdate = new ArrayList<>(medicalCaseList.size() - medicalCaseIdToErrorList.size());
        List<MedicalCaseWrapper> medicalCaseForUpdate = new ArrayList<>(medicalCaseIdToErrorList.size());
        medicalCaseToIdMap.entrySet().stream()
                //.filter(entry -> medicalCaseIdToErrorList.containsKey(entry.getKey()))
                //                 .map(entry -> {
                .forEach(entry -> {
                    Long medicalCaseId = entry.getKey();
                    MedicalCaseWrapper medicalCase = entry.getValue();
                    Long versionNumberOfMedicalCase = medicalCase.getVersionNumberOfMedicalCase();
                    if (medicalCaseIdToErrorList.containsKey(medicalCaseId)) {
                        medicalCase.setBillFlkErrorAmount(BigDecimal.ZERO);
                        List<BillFlkInvoice> billFlkInvoiceListLocal = medicalCaseIdToErrorList.get(medicalCaseId);
                        Set<Long> invoiceIdSet = new HashSet<>(billFlkInvoiceListLocal.size());
                        for (BillFlkInvoice billFlkInvoice : billFlkInvoiceListLocal) {
                            Long invoiceId = billFlkInvoice.getInvoiceId();
                            Long invoiceSum = billFlkInvoice.getInvoiceSum();
                            if (!invoiceIdSet.contains(invoiceId)) {
                                BigDecimal billFlkErrorAmount = medicalCase.getBillFlkErrorAmount();
                                BigDecimal invoiceSumBigDecimal = BigDecimal.valueOf(invoiceSum != null ? invoiceSum : 0L).divide(div);
                                BigDecimal valueToSet = billFlkErrorAmount.add(invoiceSumBigDecimal);
                                medicalCase.setBillFlkErrorAmount(valueToSet);
                                invoiceIdSet.add(invoiceId);
                            }
                        }
                        if (medicalCase.getBillFlkErrorAmount().compareTo(medicalCase.getCaseAmount()) == 1) {
                            recreateUtils.logMessage("WARNING! medicalCase" + aud + "Id = " + medicalCase.getId().toString() + " of version = "
                                    + versionNumber.toString()
                                    + " billFlkErrorAmountResult was equated to amount! billFlkErrorAmount = " + medicalCase.getBillFlkErrorAmount().toString()
                                    + " medicalCaseAmount = " + medicalCase.getCaseAmount().toString() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);
                            medicalCase.setBillFlkErrorAmount(medicalCase.getCaseAmount());
                        }
                        medicalCaseForUpdate.add(medicalCase);
                    } else if (versionNumberOfMedicalCase == null) {
                        medicalCase.setBillFlkErrorAmount(BigDecimal.ZERO);
                        medicalCaseForZeroUpdate.add(medicalCase);
                    }
                }
                );
        recreateUtils.logMessage("medicalCase" + aud + "ForUpdate size=" + medicalCaseForUpdate.size() + "! VersionNumber=" + versionNumber.toString() + "!", true, createStopWatch, null, RecreateImpl.LogType.INFO, null);

        updateMedicalCaseList(medicalCaseForUpdate, aud, versionNumber, "medicalCase");
        updateMedicalCaseList(medicalCaseForZeroUpdate, aud, versionNumber, "medicalCaseWithoutErrors");

    }

    private void updateMedicalCaseList(List<MedicalCaseWrapper> medicalCaseForUpdate, String aud, final Long versionNumber, String medicalCaseName) throws TransactionException {
        StopwatchBean updateStopWatch = recreateCommon.createStopWatch();
        Integer counter = 0;
        for (final List<MedicalCaseWrapper> wrapperList : Iterables.partition(medicalCaseForUpdate, SLICE)) {
            StopwatchBean txStopWatch = recreateCommon.createStopWatch();
            tx.execute(status -> {
                Session session = sessionFactory.openSession();
                for (MedicalCaseWrapper medicalCaseWrapper : wrapperList) {
                    Long medicalCaseId = medicalCaseWrapper.getId();
                    BigDecimal invoiceFlkErrorSum = medicalCaseWrapper.getBillFlkErrorAmount();
                    Long rev = medicalCaseWrapper.getRev();
                    if (rev == null) {
                        int executeUpdate = session.createSQLQuery("update pmp_medical_case set AMOUNT_ERR_FLK_BILL=:billFlkErrorAmount where id=:id").setParameter("billFlkErrorAmount", invoiceFlkErrorSum).setParameter("id", medicalCaseId).executeUpdate();
                    } else {
                        int executeUpdate2 = session.getNamedQuery("MedicalCaseAud.billFlkErrorAmount").setParameter("billFlkErrorAmount", invoiceFlkErrorSum).setParameter("id", medicalCaseId).setParameter("rev", rev).executeUpdate();
                    }
                }
                session.flush();
                session.close();
                return null;
            });
            counter += SLICE;
            counter = counter > medicalCaseForUpdate.size() ? medicalCaseForUpdate.size() : counter;
            recreateUtils.logMessage(medicalCaseName + aud + "ForUpdate of size=" + counter.toString() + " and versionNumber=" + versionNumber.toString() + " " + (medicalCaseForUpdate.size() > 1 ? "were" : "was") + " updated!", true, txStopWatch, null, RecreateImpl.LogType.INFO, null);
        }
        recreateUtils.logMessage(medicalCaseName + aud + "ForUpdate of size=" + medicalCaseForUpdate.size() + " and versionNumber=" + versionNumber.toString() + " " + (medicalCaseForUpdate.size() > 1 ? "were" : "was") + " updated completely!", true, updateStopWatch, null, RecreateImpl.LogType.INFO, null);
    }

    private List<Object[]> getMedicalCases(Long versionNumber) {
        List<Object[]> medicalCaseInfoList = tx.execute(status -> {
            Session session = sessionFactory.openSession();
            List<Object[]> medicalCaseInfoListDb = session.createSQLQuery(medicalCaseSql).setParameter("versionNumber", versionNumber).list();
            session.close();
            return medicalCaseInfoListDb;
        });
        return medicalCaseInfoList;
    }

    private Set<Long> getLastVersionNumbers() {
        List<Number> lastVersionsList = tx.execute(status -> {
            Session session = sessionFactory.openSession();
            List<Number> lastVersionsDbList = session.createSQLQuery(sqlQueryToGetLastVersions).list();
            session.close();
            return lastVersionsDbList;
        });
        return lastVersionsList.stream().map(el -> el.longValue()).collect(Collectors.toSet());
    }

    private void updateBillStatisticsFinishedDate(Long billStatisticsId) {
        tx.execute(status -> {
            Session session = sessionFactory.openSession();
            BillStatistics billStatisticsDb = (BillStatistics) session.get(BillStatistics.class, billStatisticsId);
            billStatisticsDb.setFinished(new Date());
            session.flush();
            session.close();
            return null;
        });
    }

    String sqlQueryToGetLastVersions = "with main_q as(\n"
            + "select p.version_number,re.id as re_id,re.mo_id as lpu_id,re.period,b.id as bill_id,p.creation_date,\n"
            + "TO_DATE('1970-01-01 03:00:00','YYYY-MM-DD HH24:mi:ss') + REV_TIMESTAMP / 86400000 as create_time\n"
            + "from pmp_parcel p\n"
            + "inner join revinfo revi on revi.id=p.version_number\n"
            + "inner join pmp_bill b on b.id=p.bill_id\n"
            + "inner join pmp_requirement re on re.id=b.requirement_id\n"
            + "where p.version_number is not null and p.version_number in (select distinct version_number from pmp_bill_flk_invoice)\n"
            + "order by p.version_number desc\n"
            + ")\n"
            + ",group_q as(\n"
            + "select version_number,re_id,lpu_id,period,bill_id,creation_date,\n"
            + "row_number() over(partition by bill_id order by creation_date desc) as rn\n"
            + "from main_q\n"
            + ")\n"
            + ",post_group_q as(\n"
            + "select distinct version_number,re_id,bill_id,lpu_id,period,creation_date,rn \n"
            + "from group_q\n"
            + "where \n"
            + "rn=1\n"
            + ")\n"
            + "select distinct version_number from post_group_q order by version_number";

    String medicalCaseSql = "select mc.id,mc.amount_err_flk_bill,mc.amount,p.version_number\n"
            + "from pmp_medical_case mc\n"
            + "inner join pmp_invoice inv on inv.case_id=mc.id\n"
            + "inner join pmp_bill b on b.id=inv.bill_id\n"
            + "inner join pmp_requirement re on re.id=b.requirement_id\n"
            + "left join pmp_parcel p on p.bill_id=b.id\n"
            + "where re.id=(select distinct bb.requirement_id from pmp_bill bb inner join pmp_parcel pp on pp.bill_id=bb.id where pp.version_number=:versionNumber)";

    private Requirement createOrGetFakeRequirement() {
        Requirement requirement = tx.execute(status -> {
            Session session = sessionFactory.openSession();
            Requirement requirementDb = (Requirement) session.createCriteria(Requirement.class).add(Restrictions.eq("lpuId", initLpuId)).add(Restrictions.eq("period", initDate)).uniqueResult();
            if (requirementDb == null) {
                requirementDb = new Requirement();
                requirementDb.setLpuId(initLpuId);
                requirementDb.setPeriod(initDate);
                requirementDb.setActive(false);
                requirementDb.setStatusChangeDate(new Date());
                requirementDb.setStatus(Requirement.RequirementStatus.DRAFT);
                requirementDb.setCreationDate(new Date());
                requirementDb.setVersion(0);
                Long requirementId = (Long) session.save(requirementDb);
                requirementDb.setId(requirementId);
            }
            session.flush();
            session.close();
            return requirementDb;
        });
        return requirement;
    }
}
