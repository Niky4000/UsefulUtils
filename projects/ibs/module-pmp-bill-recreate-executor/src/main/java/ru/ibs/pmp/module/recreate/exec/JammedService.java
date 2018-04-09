package ru.ibs.pmp.module.recreate.exec;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.api.interfaces.bill.RecreateBillsFeature;
import ru.ibs.pmp.api.model.Bill;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.RecreateBillsRequest;
import ru.ibs.pmp.dao.SyncDAO;
import ru.ibs.pmp.module.recreate.exec.bean.JammedBean;
import ru.ibs.pmp.module.recreate.exec.bean.OsProcessBean;
import ru.ibs.pmp.module.recreate.exec.bean.StuckBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBeanWrapper;

/**
 * @author NAnishhenko
 */
public class JammedService {

    @Autowired
    @Qualifier("pmpSessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    @Qualifier("pmpTransactionTemplate")
    TransactionTemplate tx;
    @Autowired
    private SyncDAO syncDAO;
    @Autowired
    ExecuteUtils executeUtils;

    private static final int JAMMED_TIME = 5;

    private static final String AUTOMATIC = "AUTOMATIC";
    private static final Logger log = LoggerFactory.getLogger(JammedService.class);

    public void putStuckBillsBackToTheQueue() throws TransactionException {
        Session session = sessionFactory.openSession();
        List<Object[]> ret = session.createSQLQuery("select distinct b.id,re.mo_id,b.status,re.period from pmp_bill b\n"
                + "inner join pmp_requirement re on b.requirement_id=re.id\n"
                + "where b.status like '%QUE%'\n"
                + "order by re.mo_id,b.status,b.id").list();
        session.close();
        List<Object[]> stuckBillList = ret;

        if (!stuckBillList.isEmpty()) {
            Map<StuckBean, List<Long>> lpuByPeriodToBillListForRecreate = new HashMap<>();
            Map<StuckBean, List<Long>> lpuByPeriodToBillListForSend = new HashMap<>();
            for (Object[] stuckBill : stuckBillList) {
                Long billId = ((BigDecimal) stuckBill[0]).longValue();
                String lpuId = (String) stuckBill[1];
                Bill.BillStatus billStatus = Bill.BillStatus.valueOf((String) stuckBill[2]);
                Date period = (Date) stuckBill[3];
                if (billStatus.equals(Bill.BillStatus.RECREATE_QUEUE)) {
                    StuckBean key = new StuckBean(lpuId, period, RecreateBillsFeature.NAME);
                    fillMap(lpuByPeriodToBillListForRecreate, key, billId);
                } else if (billStatus.equals(Bill.BillStatus.SEND_QUEUE)) {
                    StuckBean key = new StuckBean(lpuId, period, RecreateBillsFeature.SEND);
                    fillMap(lpuByPeriodToBillListForSend, key, billId);
                }
            }
            handleStuckMap(lpuByPeriodToBillListForRecreate);
            handleStuckMap(lpuByPeriodToBillListForSend);
        }
    }

    private static void fillMap(Map<StuckBean, List<Long>> map, StuckBean key, Long billId) {
        if (map.containsKey(key)) {
            map.get(key).add(billId);
        } else {
            List<Long> billIdList = new ArrayList<>();
            billIdList.add(billId);
            map.put(key, billIdList);
        }
    }

    private static String selectedBillsToString(List<Long> selectedBills) {
        if (selectedBills != null && !selectedBills.isEmpty()) {
            return StringUtils.join(selectedBills, ",");
        }
        return null;
    }

    private void handleStuckMap(Map<StuckBean, List<Long>> lpuByPeriodToBillListForRecreate) {
        for (Map.Entry<StuckBean, List<Long>> entry : lpuByPeriodToBillListForRecreate.entrySet()) {
            StuckBean key = entry.getKey();
            List<Long> billIds = entry.getValue();
            Date period = key.getPeriod();
            final Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(period);
            final RecreateBillsRequest recreateBillsRequest = new RecreateBillsRequest(key.getLpuId(),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, billIds);
            String callData = null;

            if (key.getType().equals(RecreateBillsFeature.NAME)) {
                callData = recreateBillsRequest.toCallDataRequest();
            } else if (key.getType().equals(RecreateBillsFeature.SEND)) {
                callData = recreateBillsRequest.toSendDataRequest();
            }
            String parameters = selectedBillsToString(billIds);
            PmpSync sync = syncDAO.get(Integer.valueOf(key.getLpuId()), period, callData);
            if (sync != null) {
                if (sync.getInProgress() == null || sync.getInProgress() == Boolean.FALSE) {
                    Set<String> sets = new HashSet(Arrays.asList(sync.getBillParameters().split(",")));
                    if (billIds != null && !billIds.isEmpty()) {
                        billIds.stream().forEach(aLong -> {
                            sets.add(aLong.toString());
                        });
                        syncDAO.update(sync);
                    }
                }
            } else {
                final Boolean canProcess = syncDAO.canProcess(key.getType(), callData, parameters,
                        Integer.valueOf(key.getLpuId()), period, AUTOMATIC);
            }
            log_info(callData + " created!");
        }
    }

    private void log_info(String message) {
        log.info(message);
        System.out.println(message);
    }

    public void checkForJammedQueries(List<TargetSystemBeanWrapper> targetSystemWrapperBeanList) throws UnknownHostException {
        List<PmpSync> jammed = syncDAO.getJammed(JAMMED_TIME);
        if (!jammed.isEmpty()) {
            // Здесь ещё можно добавить проверки на присутствие в списке процессов
            // данного процесса.
            // Может быть его надо прибить и перезапустить?

            Map<String, List<OsProcessBean>> processListByHost = new HashMap<>();
            for (TargetSystemBeanWrapper targetSystemBeanWrapper : targetSystemWrapperBeanList) {
                processListByHost.put(targetSystemBeanWrapper.getHost(), executeUtils.getProcessList(targetSystemBeanWrapper));
            }

            Iterator<PmpSync> jammedIterator = jammed.iterator();
            while (jammedIterator.hasNext()) {
                boolean itIsNotJammedTask = false;
                PmpSync pmpSync = jammedIterator.next();
                for (Entry<String, List<OsProcessBean>> processEntry : processListByHost.entrySet()) {
                    String host = processEntry.getKey();
                    List<OsProcessBean> processBeanList = processEntry.getValue();
                    for (OsProcessBean process : processBeanList) {
                        Integer lpuId = process.getLpuId();
                        int syncLpuId = pmpSync.getPmpSyncPK().getLpuId();
                        Date period = process.getPeriod();
                        Date syncPeriod = pmpSync.getPmpSyncPK().getPeriod();
                        String type = process.getType();
                        String syncCallData = pmpSync.getPmpSyncPK().getCallData();
                        if (lpuId.equals(syncLpuId) && period.equals(syncPeriod) && type.equals(syncCallData)) {
                            itIsNotJammedTask = true;
                            jammedIterator.remove();
                            break;
                        }
                    }
                    if (itIsNotJammedTask) {
                        break;
                    }
                }
            }

            Set<JammedBean> jammedBeanSet = new HashSet<>();
            for (PmpSync pmpSync : jammed) {
                jammedBeanSet.add(new JammedBean(pmpSync.getPmpSyncPK().getLpuId(),
                        pmpSync.getPmpSyncPK().getPeriod()));
            }
            List<PmpSync> jammedList = new ArrayList<>(jammedBeanSet.size() * 2);
            for (JammedBean jammedBean : jammedBeanSet) {
                List<PmpSync> syncByLpuAndPeriod
                        = syncDAO.getSyncByLpuAndPeriod(jammedBean.getLpuId(),
                                jammedBean.getPeriod());
                jammedList.addAll(syncByLpuAndPeriod);
            }

            if (!jammedList.isEmpty()) {
                for (PmpSync pmpSync : jammedList) {
                    if (pmpSync.getPmpSyncPK().getCallData().contains(RecreateBillsRequest.LOCK)) {
                        log_info("LpuId: " + pmpSync.getPmpSyncPK().getLpuId() + " Period: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pmpSync.getPmpSyncPK().getPeriod()) + " CallData: " + pmpSync.getPmpSyncPK().getCallData() + " UserId: " + pmpSync.getUserId() + " lock will be deleted!");
                    } else {
                        log_info("LpuId: " + pmpSync.getPmpSyncPK().getLpuId() + " Period: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pmpSync.getPmpSyncPK().getPeriod()) + " CallData: " + pmpSync.getPmpSyncPK().getCallData() + " UserId: " + pmpSync.getUserId() + " will be returned in work!");
                    }
                }
                syncDAO.returnJammedIntoWork(jammedList);
            }
        }
    }

    private void deleteOldRowsFromQueue() {
        log_info("deleteOldRowsFromQueue started!");
        Integer[] executeUpdate = null;
        try {
            final Date now = new Date();
            final Date date = DateUtils.addHours(now, -24);
            Session session = sessionFactory.openSession();
            int executeUpdate_ = session.createSQLQuery("update pmp_sync set created = SYSDATE where created is null").executeUpdate();
            int executeUpdate__ = session.createSQLQuery("delete from pmp_sync where failed='1' and created<to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "','yyyy-MM-dd HH24:mi:ss')").executeUpdate();
            session.close();
            executeUpdate = new Integer[]{executeUpdate__, executeUpdate_};
        } catch (Exception e) {
            e.printStackTrace();
        }
        log_info("deleteOldRowsFromQueue finished! " + executeUpdate[0].toString() + " rows deleted! " + executeUpdate[1].toString() + " rows updated!");
    }

    private void checkForStuckBills() {
        log_info("checkForStuckBills started!");
        try {
            putStuckBillsBackToTheQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log_info("checkForStuckBills finished!");
    }

}
