package ru.ibs.pmp.module.recreate.exec;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.TransactionException;
import ru.ibs.pmp.api.interfaces.bill.RecreateBillsFeature;
import ru.ibs.pmp.api.model.Bill;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.RecreateBillsRequest;
import ru.ibs.pmp.dao.SyncDAO;
import ru.ibs.pmp.module.pmp.bill.recreate.exec.bean.JammedBean;
import ru.ibs.pmp.module.pmp.bill.recreate.exec.bean.ProcessBean;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by Parfenov on 22.02.2017.
 */
public class ExecuteRecreate {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ExecuteRecreate.class);
    @Autowired
    private SyncDAO syncDAO;
    @Autowired
    @Qualifier("pmpSessionFactory")
    private SessionFactory sessionFactory;
    @Value("${runtime.pmp.start-recreate-executor}")
    private String canStartExecutor;

    @Value("${runtime.pmp.recreate-executor.configPath}")
    private String configPath;
    @Value("${runtime.pmp.recreate-executor.jarPath}")
    private String jarPath;
    private static final int PART_IN_WORK = 5; // максимальное количество счетов в процессе формирования
    private static final int JAMMED_TIME = 60;
    ExecutorService executor = Executors.newFixedThreadPool(PART_IN_WORK);
    static AtomicInteger currentThreadCount = new AtomicInteger(0);

    private static final Pattern pattern = Pattern.compile("^\\w+?\\{" + "moId=" + "(\\d+?)" + ", periodMonth=" + "(\\d+?)" + ", periodYear=" + "(\\d+?)" + "\\}$");

    static boolean isWindowsOS = isWindows();

//    private AtomicInteger countIterations = new AtomicInteger(0);
    private static final String AUTOMATIC = "AUTOMATIC";

    public static void main(final String args[]) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("module.xml");
        ExecuteRecreate executeRecreate = context.getBean(ExecuteRecreate.class);
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 20000)
    public void stuckExecute() {
        if (canStartExecutor.equals(Boolean.TRUE.toString())) {
            log_info("StuckExecute started! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            checkForStuckBills();
        }
    }

    @Scheduled(fixedDelay = 60000 * 2, initialDelay = 40000)
    public void garbageExecute() {
        if (canStartExecutor.equals(Boolean.TRUE.toString())) {
            log_info("GarbageExecute started! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            deleteOldRowsFromQueue();
        }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public void handleJammedQueries() throws UnknownHostException {
        if (canStartExecutor.equals(Boolean.TRUE.toString())) {
            log_info("HandleJammedQueries started! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            checkForJammedQueries();
        }
    }

    //опрашиваем БД каждые 1 min
    @Scheduled(fixedDelay = 60000)
    public void mainExecute() throws BeansException, InterruptedException {
        if (canStartExecutor.equals(Boolean.TRUE.toString())) {
//            System.out.println(""
//                    + "Usage example:\n"
//                    + "java -Xmx512M -Dpmp.config.path=C:\\pump\\etc\\pmp -jar target\\module-pmp-bill-recreate-1.160420.1-SNAPSHOT.jar 5224 2016-05\n"
//                    + "java -Xmx[Memory] -Dpmp.config.path=[Path to configs] -jar [path to jar file] [moid] [period]"
//                    + "");
            log_info("Thread started! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            int currentThreadCount_ = currentThreadCount.get();
            if (currentThreadCount_ <= PART_IN_WORK) {
                try {
                    executeMassRecreate();
                } catch (Exception ex) {
                    Logger.getLogger(ExecuteRecreate.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }

            }
            log_info("Waiting for next execution! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//            countIterations.addAndGet(1);
//            if (countIterations.intValue() > 10) {
//                checkForStuckBills();
//                deleteOldRowsFromQueue();
//                countIterations.set(0);
//            }
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    private void checkForJammedQueries() throws UnknownHostException {
        List<PmpSync> jammed = syncDAO.getJammed(JAMMED_TIME);
        // Здесь ещё можно добавить проверки на присутствие в списке процессов данного процесса.
        // Может быть его надо прибить и перезапустить?

        Set<JammedBean> jammedBeanSet = new HashSet<>();
        for (PmpSync pmpSync : jammed) {
            jammedBeanSet.add(new JammedBean(pmpSync.getPmpSyncPK().getLpuId(), pmpSync.getPmpSyncPK().getPeriod()));
        }
        List<PmpSync> jammedList = new ArrayList<>(jammedBeanSet.size() * 2);
        for (JammedBean jammedBean : jammedBeanSet) {
            List<PmpSync> syncByLpuAndPeriod = syncDAO.getSyncByLpuAndPeriod(jammedBean.getLpuId(), jammedBean.getPeriod());
            jammedList.addAll(syncByLpuAndPeriod);
        }

        if (!jammedList.isEmpty()) {
            for (PmpSync pmpSync : jammedList) {
                if (pmpSync.getPmpSyncPK().getCallData().contains(RecreateBillsRequest.LOCK)) {
                    log_info("LpuId: " + pmpSync.getPmpSyncPK().getLpuId()
                            + " Period: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pmpSync.getPmpSyncPK().getPeriod())
                            + " CallData: " + pmpSync.getPmpSyncPK().getCallData()
                            + " UserId: " + pmpSync.getUserId()
                            + " lock will be deleted!");
                } else {
                    log_info("LpuId: " + pmpSync.getPmpSyncPK().getLpuId()
                            + " Period: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pmpSync.getPmpSyncPK().getPeriod())
                            + " CallData: " + pmpSync.getPmpSyncPK().getCallData()
                            + " UserId: " + pmpSync.getUserId()
                            + " will be returned in work!");
                }
            }
            syncDAO.returnJammedIntoWork(jammedList);
        }
    }

    private void deleteOldRowsFromQueue() {
        log_info("deleteOldRowsFromQueue started!");
        Integer[] executeUpdate = null;
        try {

            final Date now = new Date();
            final Date date = DateUtils.addHours(now, -24);

            Session session = sessionFactory.openSession();
            int executeUpdate_ = session.createSQLQuery("update pmp_sync set created=SYSDATE where created is null").executeUpdate();
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

    private void putStuckBillsBackToTheQueue() throws TransactionException {
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
                    Set<String> sets = new HashSet(Arrays.asList(sync.getParameters().split(",")));
                    if (billIds != null && !billIds.isEmpty()) {
                        billIds.stream().forEach(aLong -> {
                            sets.add(aLong.toString());
                        });
                        syncDAO.update(sync);
                    }
                }
            } else {
                final Boolean canProcess = syncDAO.canProcess(key.getType(), callData, parameters, Integer.valueOf(key.getLpuId()), period, AUTOMATIC);
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

    public void executeMassRecreate() throws ParseException, IOException, InterruptedException {
        File recreateJar = new File(jarPath);
        if (recreateJar.exists()) {
            String pmpConfigPath = configPath;
            int lockCount = syncDAO.getLockCount();
            if (lockCount < PART_IN_WORK) {
                List<PmpSync> pmpSyncList = syncDAO.getSlice(PART_IN_WORK);
                String operationMode = null;
                List<ProcessBean> list = Collections.synchronizedList(new ArrayList<ProcessBean>(pmpSyncList.size()));
                List<ProcessBean> recreateList = Collections.synchronizedList(new ArrayList<ProcessBean>(pmpSyncList.size()));
                List<ProcessBean> sendList = Collections.synchronizedList(new ArrayList<ProcessBean>(pmpSyncList.size()));
                Set<String> rejectionSet = new HashSet<String>(pmpSyncList.size());
                for (PmpSync pmpSync : pmpSyncList) {
                    String callData = pmpSync.getPmpSyncPK().getCallData();
                    boolean success = false;
                    boolean type = true;
                    String callType = null;
                    boolean rejection = false;
                    if (callData.startsWith(RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST)) {
                        operationMode = "-m";
                        success = true;
                        callType = RecreateBillsFeature.NAME;
                    } else if (callData.startsWith(RecreateBillsRequest.SEND_BILLS_VIRTUAL_REQUEST)) {
                        operationMode = "-ss";
                        success = true;
                        type = false;
                        callType = RecreateBillsFeature.SEND;
                    } else if (callData.startsWith(RecreateBillsRequest.LOCK)) {
                        rejection = true;
                    } else {
                        continue;
                    }
                    if (success) {
//                    Matcher matcher = pattern.matcher(callData);
//                    if (matcher.find()) {
                        ProcessBean processBean = new ProcessBean();
//                        String moId = matcher.group(1);
//                        String periodMonth = matcher.group(2);
//                        String periodYear = matcher.group(3);
                        String moId = Integer.valueOf(pmpSync.getPmpSyncPK().getLpuId()).toString();
                        String periodMonth = new SimpleDateFormat("MM").format(pmpSync.getPmpSyncPK().getPeriod());
                        String periodYear = new SimpleDateFormat("yyyy").format(pmpSync.getPmpSyncPK().getPeriod());
                        processBean.setMoId(moId);
                        processBean.setOperationMode(operationMode);
                        processBean.setPeriodYear(periodYear);
                        processBean.setPeriodMonth(periodMonth);
                        processBean.setType(callType);
                        processBean.setPeriod(pmpSync.getPmpSyncPK().getPeriod());
                        processBean.setParameters(pmpSync.getParameters());
                        processBean.setUserId(pmpSync.getUserId());
                        if (!rejection) {
                            if (type) {
                                recreateList.add(processBean);
                            } else {
                                sendList.add(processBean);
                            }
                        } else {
                            rejectionSet.add(getProcessBeanKey(processBean));
                        }
                    }
//                    }
                }
                if (!recreateList.isEmpty()) {
                    Collections.addAll(list, recreateList.toArray(new ProcessBean[1]));
                }
                if (!sendList.isEmpty()) {
                    Collections.addAll(list, sendList.toArray(new ProcessBean[1]));
                }
                rejectionSet = new TreeSet<>(rejectionSet);
                Iterator<ProcessBean> iterator = list.iterator();
                while (iterator.hasNext()) {
                    ProcessBean processBean = iterator.next();
                    if (rejectionSet.contains(getProcessBeanKey(processBean))) {
                        iterator.remove();
                    }
                }
                int sizeOperationPool = (PART_IN_WORK >= list.size() ? list.size() : PART_IN_WORK);
//                CountDownLatch latch = new CountDownLatch(sizeOperationPool);
//                try {
                for (int j = 0; j < sizeOperationPool; j++) {
                    int finalJ = j;
                    executor.execute(() -> {
                        try {
                            executeProcess(list.get(finalJ), pmpConfigPath, recreateJar);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
//                    log_info("Waiting for threads!");

//                    latch.await();
//                } finally {
//                    executor.shutdown();
//                }
                log_info("Finished!");
            } else {
                log_info("To many locks!");
            }
        } else {
            log_info("Warning!!! ${pmp.recreate.jar.path} does not exists!!!");
        }
    }

    private static String getProcessBeanKey(ProcessBean processBean) {
        return processBean.getMoId() + "_" + processBean.getPeriodYear() + "_" + processBean.getPeriodMonth();
    }

    private String addSymbols(String str) {
        if (str.length() == 1) {
            return "0" + str;
        } else {
            return str;
        }
    }

    protected void executeProcess(final ProcessBean element, final String pmpConfigPath, final File recreateJar) throws IOException {
        try {
            final String lpuId = element.getMoId();
            final String periodStr = element.getPeriodYear() + "-" + addSymbols(element.getPeriodMonth());
            final String operationMode = element.getOperationMode();
            final RecreateBillsRequest recreateBillsRequest = new RecreateBillsRequest(lpuId, Integer.valueOf(element.getPeriodYear()), Integer.valueOf(element.getPeriodMonth()), new ArrayList<Long>());
            String toCallData = recreateBillsRequest.toCallData();
//            final Boolean canProcess2 = syncDAO.canProcess(element.getType(), toCallData, element.getParameters(), Integer.valueOf(lpuId), element.getPeriod(), element.getUserId());
//            if (canProcess2) {
            String executeString = "java -Xmx16G -Dpmp.config.path=" + new File(pmpConfigPath).getAbsolutePath() + " -jar " + recreateJar.getAbsolutePath() + " " + operationMode + " " + lpuId + " " + periodStr;
            log_info(executeString);
            final Process process;
            if (isWindowsOS) {
                process = Runtime.getRuntime().exec("cmd.exe /c start /wait " + executeString);
            } else {
                process = Runtime.getRuntime().exec(executeString);
            }
//                try {
//                    process.waitFor();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            log_error("Process Execute Exception!", e);
        }
    }

    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
    }

    public static class StuckBean {

        private final String lpuId;
        private final Date period;
        private final String type;

        public StuckBean(String lpuId, Date period, String type) {
            this.lpuId = lpuId;
            this.period = period;
            this.type = type;
        }

        public String getLpuId() {
            return lpuId;
        }

        public Date getPeriod() {
            return period;
        }

        public String getType() {
            return type;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + Objects.hashCode(this.lpuId);
            hash = 53 * hash + Objects.hashCode(this.period);
            hash = 53 * hash + Objects.hashCode(this.type);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StuckBean other = (StuckBean) obj;
            if (!Objects.equals(this.lpuId, other.lpuId)) {
                return false;
            }
            if (!Objects.equals(this.type, other.type)) {
                return false;
            }
            if (!Objects.equals(this.period, other.period)) {
                return false;
            }
            return true;
        }
    }

    private void log_info(String message) {
        log.info(message);
        System.out.println(message);
    }

    private void log_error(String message, Exception e) {
        log.error(message, e);
        System.err.println(message);
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public void setCanStartExecutor(String canStartExecutor) {
        this.canStartExecutor = canStartExecutor;
    }

    public String getCanStartExecutor() {
        return canStartExecutor;
    }
}
