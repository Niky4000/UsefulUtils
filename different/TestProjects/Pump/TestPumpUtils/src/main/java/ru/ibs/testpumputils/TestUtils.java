package ru.ibs.testpumputils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.api.model.Bill;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.Requirement;
import ru.ibs.pmp.api.service.payer.PayersService;
import ru.ibs.pmp.auth.model.SmoEntity;
import ru.ibs.pmp.dao.BillStatisticsDAO;
import ru.ibs.pmp.dao.hibernate.BillStatisticsDAOHibernate;

/**
 * @author NAnishhenko
 */
public class TestUtils {

    private static final int PARTITION_SIZE = 1000;

//    public static void testSyncServiceImpl(SessionFactory sessionFactory) throws Exception {
//        SyncDAOHibernate synсDAO = new SyncDAOHibernate();
//        setField(synсDAO, sessionFactory, "sessionFactory");
//        BillDAOHibernate billDAO = new BillDAOHibernate();
//        setField(billDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
//        SmoEntityDAOHibernate smoEntityDAO = new SmoEntityDAOHibernate();
//        setField(smoEntityDAO, sessionFactory, "sessionFactory");
//
//        List<PmpSync> syncs = synсDAO.getSyncByPeriod(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-09-01 00:00:00"));
//        AccountingSyncStatusDTO dto = new AccountingSyncStatusDTO();
////        dto.setOffset(getAccountSyncDTO.getOffset());
////        dto.setSize(getAccountSyncDTO.getSize());
//        dto.setTotal(syncs.size());
//        if (CollectionUtils.isNotEmpty(syncs)) {
////            syncs = syncs.stream().filter(s -> !s.getPmpSyncPK().getCallData().toUpperCase().contains("LOCK_ON_MO_AT_PERIOD")).filter(s -> lpuIds.contains(s.getPmpSyncPK().getLpuId())).collect(Collectors.toList());
////            int sizeIndex = (syncs.size() - getAccountSyncDTO.getOffset()) < getAccountSyncDTO.getSize() ? syncs.size() - getAccountSyncDTO.getOffset() : getAccountSyncDTO.getSize();
//            if (CollectionUtils.isNotEmpty(syncs)) {
//                Map<PmpSync, List<Long>> pmpSyncToBillIdMap = syncs.stream().map(sync -> SyncUtils.getBillIdsFromParameters(sync.getBillParameters(), sync, new ArrayList<>(1))).flatMap(List::stream).collect(Collectors.groupingBy(objArr -> (PmpSync) objArr[1], Collectors.collectingAndThen(Collectors.toList(), ff -> ff.stream().map(objArr -> (Long) objArr[0]).collect(Collectors.toList()))));
//                Set<Long> billIdSet = pmpSyncToBillIdMap.values().stream().flatMap(list -> list.stream()).collect(Collectors.toSet());
//                List<Bill> billList = new ArrayList<>(billIdSet.size());
//                Iterables.partition(billIdSet, PARTITION_SIZE).forEach(billSubList -> billList.addAll(billDAO.get(billSubList)));
//                Map<Long, Bill> billMap = billList.stream().collect(Collectors.toMap(Bill::getId, bill -> bill));
//                Set<String> payerOgrnSet = billList.stream().map(Bill::getPayerOgrn).collect(Collectors.toSet());
//                Map<String, SmoEntity> smoEntityByPayerOgrnMap = smoEntityDAO.get(payerOgrnSet).stream().collect(Collectors.toMap(SmoEntity::getOgrn, smo -> smo));
//                dto.setItems(syncs.stream().map(s -> new AccountingSyncStatusDTO.SyncDTO(s, getParameters(s, pmpSyncToBillIdMap, billMap, smoEntityByPayerOgrnMap))).collect(Collectors.toList()));
//                System.out.println("Hello from testSyncServiceImpl!");
//            }
//        }
//    }
    private static String getParameters(PmpSync pmpSync, Map<PmpSync, List<Long>> pmpSyncToBillIdMap, Map<Long, Bill> billMap, Map<String, SmoEntity> smoEntityByPayerOgrnMap) {
        return StringUtils.join(pmpSyncToBillIdMap.get(pmpSync).stream().map(billId -> {
            String payerOgrn = billMap.get(billId).getPayerOgrn();
            if (!payerOgrn.equals(PayersService.UNKNOWN_PAYER)) {
                return smoEntityByPayerOgrnMap.get(payerOgrn).getName() + " (" + billId.toString() + ")";
            } else {
                return PayersService.UNKNOWN_PAYER + " (" + billId.toString() + ")";
            }
        }).collect(Collectors.toList()), " ");
    }
//    private static void testBillStatisticsDAOImpl(SessionFactory sessionFactory) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SecurityException {
//        Session session = sessionFactory.getCurrentSession();
//        Requirement requirement = (Requirement) session.get(Requirement.class, 10009547L);
//        BillStatisticsDAO billStatisticsDAOImpl = new BillStatisticsDAOHibernate();
//        setField(billStatisticsDAOImpl, sessionFactory, "sessionFactory");
//        Map<Long, List<String>> duplicatePatientsInfo = billStatisticsDAOImpl.getDuplicatePatientsInfo(requirement);
//    }
}
