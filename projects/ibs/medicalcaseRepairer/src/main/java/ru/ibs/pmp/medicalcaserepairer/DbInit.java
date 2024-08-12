package ru.ibs.pmp.medicalcaserepairer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.api.model.BillStatistics;
import ru.ibs.pmp.api.model.Requirement;
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
public class DbInit {

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
    private static Date initDate;
    private static String initLpuId;
    LogThread logThread;
    TimerThread timerThread;

    public void init(String processName) throws ParseException {
        initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2000-01-01 00:00:00");
        initLpuId = "0000";
        appInputParams.init(initLpuId, initDate);
        recreateUtils.logMessage("Hello!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
        initLogger();
        recreateUtils.setLogThread(logThread);
        logThread.setBillStatisticsTimeList(recreateUtils.getBillStatisticsTimeList());
        recreateCommon.setSessionFactory(sessionFactory);
        recreateCommon.setTx(tx);
        recreateCommon.setRecreateUtils(recreateUtils);
        Requirement requirement = createOrGetFakeRequirement();
        BillStatisticsBean billStatistics = recreateCommon.createBillStatistics(requirement, initLpuId, initDate, BillStatistics.BillOperation.OTHERS, null, processName, false);
        recreateUtils.setBillStatistics(billStatistics.getBillStatistics());
        recreateUtils.logMessage("billStatistics with id = " + billStatistics.getBillStatistics().getId().toString() + " was created!", true, recreateCommon.createStopWatch(), null, RecreateImpl.LogType.INFO, null);
        logThread.start();
        timerThread.start();
    }

    private void initLogger() {
        logThread = new LogThread(recreateCommon);
        logThread.setDaemon(false);
        timerThread = new TimerThread(logThread);
        timerThread.setDaemon(true);

    }

    private Requirement createOrGetFakeRequirement() {
        Requirement requirement = tx.execute(status -> {
            Session session = sessionFactory.openSession();
            Requirement requirementDb = (Requirement) session.createCriteria(Requirement.class).add(Restrictions.eq("lpuId", initLpuId)).add(Restrictions.eq("period", initDate)).uniqueResult();
            if (requirementDb == null) {
                requirementDb = new Requirement();
                requirementDb.setLpuId(initLpuId);
                requirementDb.setPeriod(initDate);
                requirementDb.setStatusChangeDate(new Date());
                requirementDb.setStatus(Requirement.RequirementStatus.DRAFT);
                requirementDb.setCreationDate(new Date());
                Long requirementId = (Long) session.save(requirementDb);
                requirementDb.setId(requirementId);
            }
            session.flush();
            session.close();
            return requirementDb;
        });
        return requirement;
    }

    public void finalizationActions(StopwatchBean globalStopWatch, Long billStatisticsId) throws InterruptedException {
        recreateUtils.logMessage("billStatisticsId = " + billStatisticsId.toString() + "!", true, globalStopWatch, null, RecreateImpl.LogType.INFO, null);
        recreateUtils.logMessage("Finished!", true, globalStopWatch, null, RecreateImpl.LogType.INFO, null);
        logThread.setTimerInterraption(false);
        logThread.interrupt();
        logThread.join();
        if (recreateUtils.getBillStatistics() != null) {
            recreateUtils.flushLogMessage();
        }
        updateBillStatisticsFinishedDate(billStatisticsId);
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
}
