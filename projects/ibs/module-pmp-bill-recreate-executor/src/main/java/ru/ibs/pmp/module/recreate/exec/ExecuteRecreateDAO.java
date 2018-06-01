package ru.ibs.pmp.module.recreate.exec;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.api.interfaces.bill.RecreateBillsFeature;
import ru.ibs.pmp.api.model.Bill;
import ru.ibs.pmp.api.model.BillStatistics;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.RecreateBillsRequest;
import ru.ibs.pmp.api.model.Requirement;
import ru.ibs.pmp.dao.SyncDAO;
import ru.ibs.pmp.module.recreate.exec.bean.StuckBean;

/**
 * @author NAnishhenko
 */
public class ExecuteRecreateDAO {

    @Autowired
    @Qualifier("pmpSessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    @Qualifier("pmpTransactionTemplate")
    TransactionTemplate tx;
    @Autowired
    private SyncDAO syncDAO;
    private static final Logger log = LoggerFactory.getLogger(ExecuteRecreateDAO.class);

    public Requirement getRequirement(final String moId, final int year, final int month) throws TransactionException {
        final Calendar calendar = new GregorianCalendar(year, month - 1, 1);
        return tx.execute(status -> {
            Requirement requirement = null;
            Session session = sessionFactory.openSession();
            try {
                requirement = (Requirement) session.createCriteria(Requirement.class).add(Restrictions.eq("period", calendar.getTime())).add(Restrictions.eq("lpuId", moId)).uniqueResult();
                if (requirement == null) {
                    return null;
                }
                Hibernate.initialize(requirement.getBills());
            } finally {
                session.close();
            }
            return requirement;
        });
    }

    public String getLastServerThatRecreatedBills(Requirement requirement) {
        return tx.execute(status -> {
            Optional<BillStatistics> billStatistics = Optional.empty();
            Session session = sessionFactory.openSession();
            try {
                billStatistics = Optional.ofNullable((BillStatistics) session.createCriteria(BillStatistics.class)
                        .add(Restrictions.eq("requirementId", requirement.getId()))
                        .add(Restrictions.eq("type", BillStatistics.BillOperation.RECREATE_BILL_MODULE))
                        .addOrder(Order.desc("created"))
                        .setFirstResult(0).setMaxResults(1).uniqueResult());
            } catch (Exception e) {
                e.printStackTrace();
                billStatistics = Optional.empty();
            } finally {
                session.close();
            }
            return billStatistics.map(BillStatistics::getServerIp).orElse(null);
        });
    }

    public List<Object[]> getServiceCount(final Date period, Date periodEnd, final Set<String> lpuIdSetForRecreate) throws TransactionException {
        List<Object[]> ret = new ArrayList<>(lpuIdSetForRecreate.size());
        ArrayList<String> arrayList = new ArrayList<>(lpuIdSetForRecreate);
        Collections.sort(arrayList);
        for (String lpuId : arrayList) {
            Object[] obj = tx.execute(status -> {
                Session session = sessionFactory.openSession();
                try {
                    String filIdQuery = "select fil_id from PMP_NSI_NEW.NSI_MOSCOW_OMS_DEPARTMENT where code=:lpuId and VERSION_ID=(select CUR_VER from (select CUR_VER from PMP_NSI_NEW.NSI_MOSCOW_AISOMS_DICTS where CODE = 'sprlpu' and to_date(INTR_DATE, 'yyyymmdd') <= :period order by INTR_DATE desc) where rownum = 1)";
                    List<Object> filIdListDb = session.createSQLQuery(filIdQuery)
                            .setParameter("period", period)
                            .setParameter("lpuId", lpuId)
                            .list();
                    Set<String> filIdSet = filIdListDb.stream().map(filId -> filId.toString()).collect(Collectors.toSet());

                    String simpleServiceQuery = "select count(*) from pmp_simple_service ss\n"
                            + "inner join pmp_medical_case mc on mc.id=ss.case_id\n"
                            + "where mc.case_date between :period and :periodEnd and mc.lpu_id in(:lpuId)";

                    Number simpleServiceCount = (Number) session.createSQLQuery(simpleServiceQuery)
                            .setParameter("period", period)
                            .setParameter("periodEnd", periodEnd)
                            .setParameterList("lpuId", filIdSet)
                            .uniqueResult();

                    String hospDeptStayQuery = "select count(*) from pmp_hosp_dept_stay hds\n"
                            + "inner join pmp_medical_case mc on mc.id=hds.medical_case_id\n"
                            + "where mc.case_date between :period and :periodEnd and mc.lpu_id in(:lpuId)";

                    Number hospDeptStayCount = (Number) session.createSQLQuery(hospDeptStayQuery)
                            .setParameter("period", period)
                            .setParameter("periodEnd", periodEnd)
                            .setParameterList("lpuId", filIdSet)
                            .uniqueResult();

                    return new Object[]{BigDecimal.valueOf(Long.valueOf(lpuId)), BigDecimal.valueOf(simpleServiceCount.longValue() + hospDeptStayCount.longValue())};
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    session.close();
                }
            });
            ret.add(obj);
        }
        return ret;
    }

    public void truncateBillWithMedicalCaseLink() {
        tx.execute(status -> {
            Session session = sessionFactory.openSession();
            try {
                session.createSQLQuery("truncate table bill_with_medical_case_link").executeUpdate();
            } finally {
                session.close();
            }
            return null;
        });
    }

//    public List<Object[]> getServiceCount(final Date period, Date periodEnd, final Set<String> lpuIdSetForRecreate) throws TransactionException {
//        List<Object[]> objList = tx.execute(status -> {
//            Session session = sessionFactory.openSession();
//            try {
//                String query = "select to_number(dic.lpu_id) as lpu_id,to_number(count(hds.id)+count(ss.id)) as all_\n"
//                        + "from pmp_medical_case mc\n"
//                        + "inner join (select fil_id, code as lpu_id from PMP_NSI_NEW.NSI_MOSCOW_OMS_DEPARTMENT where code in(:lpuId) and VERSION_ID=(select CUR_VER from (select CUR_VER from PMP_NSI_NEW.NSI_MOSCOW_AISOMS_DICTS where CODE = 'sprlpu' and to_date(INTR_DATE, 'yyyymmdd') <= :period order by INTR_DATE desc) where rownum = 1)) dic on dic.fil_id=mc.lpu_id\n"
//                        + "left join pmp_tap_info ti on mc.id=ti.medical_case_id\n"
//                        + "left join pmp_hosp_case hc on hc.medical_case_id=mc.id\n"
//                        + "left join pmp_hosp_dept_stay hds on hds.medical_case_id=mc.id\n"
//                        + "left join pmp_simple_service ss on ss.case_id=mc.id\n"
//                        + "inner join MOSPRLPU mo on mc.lpu_id=mo.fil_id\n"
//                        + "where case_date between :period and :periodEnd and dic.fil_id in(:lpuId)\n"
//                        + "group by dic.lpu_id,mo.moname\n"
//                        + "order by count(hds.id)+count(ss.id) desc";
//                List<Object[]> objListDb = session.createSQLQuery(query)
//                        .setParameter("period", period)
//                        .setParameter("periodEnd", periodEnd)
//                        .setParameterList("lpuId", lpuIdSetForRecreate)
//                        .list();
//                return objListDb;
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw e;
//            } finally {
//                session.close();
//            }
//        });
//        return objList;
//    }
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
                    StuckBean key = new StuckBean(lpuId, period, RecreateBillsFeature.NAME, billStatus);
                    fillMap(lpuByPeriodToBillListForRecreate, key, billId);
                } else if (billStatus.equals(Bill.BillStatus.SEND_QUEUE)) {
                    StuckBean key = new StuckBean(lpuId, period, RecreateBillsFeature.SEND, billStatus);
                    fillMap(lpuByPeriodToBillListForSend, key, billId);
                }
            }
            handleStuckMap(lpuByPeriodToBillListForRecreate);
            handleStuckMap(lpuByPeriodToBillListForSend);
        }
    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private static final String AUTOMATIC = "AUTOMATIC";

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

    private static String selectedBillsToString(List<Long> selectedBills) {
        if (selectedBills != null && !selectedBills.isEmpty()) {
            return StringUtils.join(selectedBills, ",");
        }
        return null;
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

    private void log_info(String message) {
        log.info(message);
        System.out.println(message);
    }

    public boolean checkForRecreateJarExisting(File recreateJarPar) {
        return existFile(recreateJarPar);
    }

    public boolean existFile(File recreateJar) {
        return recreateJar.exists();
    }
}
