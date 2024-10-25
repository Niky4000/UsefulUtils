package ru.ibs.pmp.module.recreate.exec;

import java.io.File;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import ru.ibs.pmp.api.interfaces.bill.RecreateBillsFeature;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.RecreateBillsRequest;
import ru.ibs.pmp.dao.SyncDAO;
import ru.ibs.pmp.module.recreate.exec.bean.MemoryBean;
import ru.ibs.pmp.module.recreate.exec.bean.OsEnum;
import ru.ibs.pmp.module.recreate.exec.bean.OsProcessBean;
import ru.ibs.pmp.module.recreate.exec.bean.ProcessBean;
import ru.ibs.pmp.module.recreate.exec.bean.QueueLpuBean;
import ru.ibs.pmp.module.recreate.exec.bean.RamBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBeanWrapper;
import ru.ibs.pmp.module.recreate.exec.bean.TriFunction;

/**
 * Created by Parfenov on 22.02.2017.
 */
public class ExecuteRecreate {

    private static final Logger log = LoggerFactory.getLogger(ExecuteRecreate.class);
    @Autowired
    private SyncDAO syncDAO;
    @Autowired
    ExecuteRecreateDAO executeRecreateDAO;
    @Value("${runtime.pmp.start-recreate-executor}")
    private String canStartExecutor;
    @Value("${runtime.pmp.recreate-executor.configPath}")
    private String configPath;
    @Value("${runtime.pmp.recreate-executor.jarPath}")
    private String jarPath;
    @Value("${runtime.executor.remotes}")
    private String remotesConfig;
    @Autowired
    ExecuteUtils executeUtils;
    private File recreateJar;
    private int PART_IN_WORK = 0; // максимальное количество счетов
    // в процессе формирования
    private static final int JAMMED_TIME = 60;
//    ExecutorService executor = Executors.newFixedThreadPool(PART_IN_WORK);
    static AtomicInteger currentThreadCount = new AtomicInteger(0);
    private static final Pattern pattern = Pattern.compile("^\\w+?\\{" + "moId=" + "(\\d+?)" + ", periodMonth=" + "(\\d+?)" + ", periodYear=" + "(\\d+?)" + "\\}$");
    public static boolean isWindowsOS = isWindows();

    private LinkedBlockingQueue<ProcessBean> taskQueue;
    private List<ExecuteThread> executeThreadList = new ArrayList<>(PART_IN_WORK);
    private AtomicBoolean isItAllowedToExecuteViaTimer = new AtomicBoolean(true);
    private List<TargetSystemBean> targetSystemBeanCollection;
    private LinkedList<TargetSystemBeanWrapper> targetSystemWrapperBeanLinkedList;
    private Map<String, TargetSystemBeanWrapper> targetSystemBeanCollectionByHost;
    private Set<String> lpuInProcessSet = Collections.synchronizedSet(new HashSet<>());

    private Map<Date, Map<String, Map<String, RamBean>>> possibleRamUsageToLpuIdByTypeAndByPeriodGlobal = new HashMap<>(); // Память, которую мы резервируем для каждого процесса. Используется однопоточно.

    private volatile boolean inited = false;

    // опрашиваем БД каждые 1 min
    @Scheduled(fixedDelay = 20000)
    public void mainExecute() throws BeansException, InterruptedException {
        init();
        if (inited && canStartExecutor != null && canStartExecutor.equals(Boolean.TRUE.toString())) {
            log_info("Thread started! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            int currentThreadCount_ = currentThreadCount.get();
            if (currentThreadCount_ <= PART_IN_WORK) {
                try {
                    if (isItAllowedToExecuteViaTimer.get()) {
                        executeMassRecreate();
                    }
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(ExecuteRecreate.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                    log_error("Fatal Exception!!!", ex);
                    log_info("Fatal Exception!!!");
                }
            }
            log_info("Waiting for next execution! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } else {
            log.info("Executor no start::" + canStartExecutor);
        }
    }

    public void executeMassRecreate() throws InterruptedException {
        isItAllowedToExecuteViaTimer.getAndSet(false);
        try {
//            Map<String, Boolean> result = new HashMap<>();
//            if (executeRecreateDAO.checkForRecreateJarExisting(recreateJar)) {
//            String pmpConfigPath = configPath;
            int lockCount = 0;
            try {
                lockCount = syncDAO.getLockCount();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (lockCount < PART_IN_WORK) { // PART_IN_WORK содержит максимально возможное число процессов, запущенных на разных серверах
                List<PmpSync> allSyncs = syncDAO.getAllTasks(); // Получить все заявки
//                    if (allSyncs == null || allSyncs.isEmpty() || allSyncs.size() > 0) {
//                        throw new RuntimeException("Test Exception!!!");
//                    }
                // Убрать из списка блокировки
                Set<PmpSync> pmpSyncAllList = allSyncs.stream().filter(sync -> !sync.getPmpSyncPK().getCallData().equals(RecreateBillsRequest.LOCK)).collect(Collectors.toSet());
                Set<PmpSync> pmpSyncList = allSyncs.stream().filter(sync -> !sync.getPmpSyncPK().getCallData().equals(RecreateBillsRequest.LOCK)
                        && !Optional.ofNullable(sync.getFailed()).orElse(false)
                        && !Optional.ofNullable(sync.getInProgress()).orElse(false)
                ).collect(Collectors.toSet());
                if (!pmpSyncList.isEmpty()) { // Если заявки вообще есть, то продолжить
                    log_info("pmpSyncList size = " + pmpSyncList.size() + "!");

                    Map<String, List<OsProcessBean>> allProcessesMapByHostIp = getAllProcessesMapByHostIp();
                    getServiceAmountForProcessBeans(pmpSyncAllList, allProcessesMapByHostIp); // Получить прогноз по занимаемой памяти для каждого процесса

                    TriFunction<Date, String, Integer, Long> getPossibleMemoryConsumption = (period, type, lpuId) -> {
                        return Optional.ofNullable(possibleRamUsageToLpuIdByTypeAndByPeriodGlobal)
                                .map(map -> map.get(period))
                                .map(map -> map.get(type))
                                .map(map -> map.get(Optional.ofNullable(lpuId).map(lpuId_ -> lpuId_.toString()).orElse("")))
                                .map(map -> map.getRamUsage())
                                .orElse(512L);
                    };

                    // Map: key - тип заявки (формирование, отправка или ПОСОБР); value - заявка и соответствующий ей прогноз по занимаемой памяти.
                    Map<String, Set<MemoryBean>> memoryBeanMapToFeatureName = pmpSyncList.stream()
                            .map(pmpSync -> {
                                Date period = pmpSync.getPmpSyncPK().getPeriod();
                                String featureName = pmpSync.getFeatureName();
                                String lpuId = pmpSync.getPmpSyncPK().getLpuId() + "";
                                Optional<RamBean> optionalRamBean = Optional.ofNullable(possibleRamUsageToLpuIdByTypeAndByPeriodGlobal.get(period))
                                        .map(map -> map.get(featureName))
                                        .map(map -> map.get(lpuId))
                                        .map(map -> map);
                                RamBean ramBean = optionalRamBean.orElse(new RamBean(512L, new Date(), -1L));
                                return new MemoryBean(pmpSync, ramBean);
                            }
                            ).sorted()
                            .collect(Collectors.groupingBy(memoryBean -> memoryBean.getPmpSync().getFeatureName(), TreeMap::new, Collectors.toCollection(TreeSet::new)));

                    // Оценить примерно свободную память на серверах.
                    for (TargetSystemBean targetSystemBean : targetSystemBeanCollection) {
                        Long freeMemory = executeUtils.getFreeMemory(new TargetSystemBeanWrapper(targetSystemBean));
                        TargetSystemBeanWrapper targetSystemBeanByHost = targetSystemBeanCollectionByHost.get(targetSystemBean.getHost());
                        List<OsProcessBean> processList = allProcessesMapByHostIp.get(targetSystemBean.getHost());
                        long usedServerMemory = processList.stream().map(process -> getPossibleMemoryConsumption.apply(process.getPeriod(), process.getType(), process.getLpuId())).mapToLong(i -> i.longValue()).sum();
                        // Посчитали свободную память сервера без учёта наших процессов, которые на нём уже запущены.
                        // Это надо на случай нормального пересчёта, если планировщик был перезапущен! Подсчёт приблизительный и оценочный.
                        // Нам надо примерно оценить на сколько забит сервер.
                        // Здесь проблема в том, что точно мы это посчитать не можем по причине того, что процесс может увеличивать
                        // количество потребляемой памяти по ходу своей работы. То есть getProcessMemory и сложить результаты не получится.
                        // Результат может быть обманчивым, а именно заниженным. Поэтому ориентируемся на прогнозируемый объём занимаемой памяти.
                        targetSystemBeanByHost.setFreeMemory(freeMemory - usedServerMemory);
                        targetSystemBeanByHost.setProcessSet(new HashSet<>(processList));
                    }

                    // В этот список закидываем заявки, которые мы решили распределить по серверам.
                    Set<MemoryBean> memoryBeanSet = new HashSet<>();
                    Set<MemoryBean> memoryBeanSetForPosobr = Optional.ofNullable(memoryBeanMapToFeatureName.get(RecreateBillsFeature.CALC_POSOBR)).orElse(new HashSet<>());
                    Set<MemoryBean> memoryBeanSetForSmoFlk = Optional.ofNullable(memoryBeanMapToFeatureName.get(RecreateBillsFeature.SMO_BILL_FLK)).orElse(new HashSet<>());
                    Set<MemoryBean> memoryBeanSetForSend = Optional.ofNullable(memoryBeanMapToFeatureName.get(RecreateBillsFeature.SEND)).orElse(new HashSet<>());
                    Set<MemoryBean> memoryBeanSetForRecreate = Optional.ofNullable(memoryBeanMapToFeatureName.get(RecreateBillsFeature.NAME)).orElse(new HashSet<>());
                    LinkedList<MemoryBean> memoryBeanSetForPosobrLinkedList = new LinkedList(memoryBeanSetForPosobr);
                    LinkedList<MemoryBean> memoryBeanSetForSmoFlkLinkedList = new LinkedList(memoryBeanSetForSmoFlk);
                    LinkedList<MemoryBean> memoryBeanSetForSendLinkedList = new LinkedList(memoryBeanSetForSend);
                    LinkedList<MemoryBean> memoryBeanSetForRecreateLinkedList = new LinkedList(memoryBeanSetForRecreate);
                    Collections.sort(memoryBeanSetForRecreateLinkedList, (t1, t2) -> t1.getPmpSync().getCreated().before(t2.getPmpSync().getCreated()) ? -1 : 1);

                    // Это нужно для того, чтобы не засовывать одно и тоже ЛПУ за один и тот же период одновременно и на формирование и на отправку!
                    Set<String> lpuAndPeriodSet = new HashSet<>();

                    // Эти заявки выполнять на определённом сервере
                    Set<MemoryBean> memoryBeanSetForSendOnParticularServer = new HashSet<>();
                    // Эти заявки выполнять на любом сервере
                    Set<MemoryBean> memoryBeanSetForSendOnAnyServer = new HashSet<>();
                    while (!memoryBeanSetForSendLinkedList.isEmpty()) {
                        MemoryBean memoryBean = memoryBeanSetForSendLinkedList.pollFirst();
                        String periodMonth = new SimpleDateFormat("MM").format(memoryBean.getPmpSync().getPmpSyncPK().getPeriod());
                        String periodYear = new SimpleDateFormat("yyyy").format(memoryBean.getPmpSync().getPmpSyncPK().getPeriod());
                        String serverIp = executeRecreateDAO.getLastServerThatRecreatedBills(executeRecreateDAO.getRequirement(
                                Integer.valueOf(memoryBean.getPmpSync().getPmpSyncPK().getLpuId()).toString(),
                                Integer.valueOf(periodYear), Integer.valueOf(periodMonth)));
                        if (serverIp != null && targetSystemBeanCollectionByHost.containsKey(serverIp)) {
                            TargetSystemBeanWrapper targetSystemBean = targetSystemBeanCollectionByHost.get(serverIp);
                            setTaskToQueue(new LinkedList(Arrays.asList(memoryBean)), targetSystemBean, memoryBeanSetForSendOnParticularServer, lpuAndPeriodSet);
                            log_info(memoryBean.getPmpSync().getPmpSyncPK().getLpuId() + ": last recreate was made on: " + serverIp + "!");
                        } else {
                            memoryBeanSetForSendOnAnyServer.add(memoryBean);
                            log_info(memoryBean.getPmpSync().getPmpSyncPK().getLpuId() + ": it wasn't found where last recreate happened!");
                        }
                    }
                    memoryBeanSet.addAll(memoryBeanSetForSendOnParticularServer);
                    LinkedList<MemoryBean> memoryBeanSetForSendOnAnyServerLinkedList = new LinkedList(memoryBeanSetForSendOnAnyServer);
                    //
                    //
                    //
                    // Заявки на посылку счетов
                    Iterator<TargetSystemBeanWrapper> targetSystemBeanWrapperDescendingIterator = targetSystemWrapperBeanLinkedList.descendingIterator();
                    while (targetSystemBeanWrapperDescendingIterator.hasNext()) {
                        TargetSystemBeanWrapper targetSystemBeanWrapper = targetSystemBeanWrapperDescendingIterator.next();
                        setTaskToQueue(memoryBeanSetForSendOnAnyServerLinkedList, targetSystemBeanWrapper, memoryBeanSet, lpuAndPeriodSet);
                    }
                    //
                    // Заявки на переформирование счетов
                    Iterator<TargetSystemBeanWrapper> targetSystemBeanWrapperIterator = targetSystemWrapperBeanLinkedList.iterator();
                    while (targetSystemBeanWrapperIterator.hasNext()) {
                        TargetSystemBeanWrapper targetSystemBeanWrapper = targetSystemBeanWrapperIterator.next();
                        setTaskToQueue(memoryBeanSetForRecreateLinkedList, targetSystemBeanWrapper, memoryBeanSet, lpuAndPeriodSet);
                    }
                    //
                    // Заявки на ПОСОБР и на СМО ФЛК
                    Iterator<TargetSystemBeanWrapper> targetSystemBeanWrapperDescendingIterator2 = targetSystemWrapperBeanLinkedList.descendingIterator();
                    while (targetSystemBeanWrapperDescendingIterator2.hasNext()) {
                        TargetSystemBeanWrapper targetSystemBeanWrapper = targetSystemBeanWrapperDescendingIterator2.next();
                        setTaskToQueue(memoryBeanSetForPosobrLinkedList, targetSystemBeanWrapper, memoryBeanSet, lpuAndPeriodSet);
                        setTaskToQueue(memoryBeanSetForSmoFlkLinkedList, targetSystemBeanWrapper, memoryBeanSet, lpuAndPeriodSet);
                    }

                    //
                    //
                    // Отправляем заявки на реальное исполнение!
                    handleClaim(memoryBeanSet);
                    log_info("Finished!");
                } else { // pmpSyncList.isEmpty()!
                    Date now = new Date();
                    Map<Date, Map<String, Map<String, RamBean>>> possibleRamUsageToLpuIdByTypeAndByPeriod = new HashMap<>();
                    refreshPossibleRamUsage(now, possibleRamUsageToLpuIdByTypeAndByPeriod);
                    if (allSyncs.isEmpty() && !dateCriteria(lastBillWithMedicalCaseLinkTruncateTime, now)) {
                        lastBillWithMedicalCaseLinkTruncateTime = new Date();
                        executeRecreateDAO.truncateBillWithMedicalCaseLink();
                        log_info("BillWithMedicalCaseLink was truncated!");
                    }
//                    Set<PmpSync> oldRamUsageRows = getOldRamUsageRows(now);
//                    Map<String, List<OsProcessBean>> allProcessesMapByHostIp = getAllProcessesMapByHostIp();
//                    getServiceAmountForProcessBeans(oldRamUsageRows, allProcessesMapByHostIp);
                }
            } else {
                log_info("To many locks!");
            }
//            } else {
//                log_info("Warning!!! ${pmp.recreate.jar.path} does not exists!!!");
//            }
        } finally {
            isItAllowedToExecuteViaTimer.getAndSet(true);
        }
    }

    private Date lastBillWithMedicalCaseLinkTruncateTime = new Date();

    private Map<String, List<OsProcessBean>> getAllProcessesMapByHostIp() {
        return targetSystemBeanCollection.stream()
                .map(targetSystemBean -> targetSystemBeanCollectionByHost.get(targetSystemBean.getHost()))
                .collect(Collectors.groupingBy(targetSystemBean -> targetSystemBean.getHost(),
                        Collectors.mapping(executeUtils::getProcessList, Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0)))));
    }

    private String getKeyForLpuAndPeriodSet(MemoryBean memoryBean) {
        return memoryBean.getPmpSync().getPmpSyncPK().getLpuId() + "__" + new SimpleDateFormat("yyyy_MM_dd").format(memoryBean.getPmpSync().getPmpSyncPK().getPeriod());
    }

    private void setTaskToQueue(LinkedList<MemoryBean> memoryBeanSetForParticularPurpose, TargetSystemBeanWrapper targetSystemBeanWrapper, Set<MemoryBean> memoryBeanSet, Set<String> lpuAndPeriodSet) {
        Set<String> lpuAndPeriodSetLocal = new HashSet<>();
        while (!memoryBeanSetForParticularPurpose.isEmpty()) {
            MemoryBean memoryBean = memoryBeanSetForParticularPurpose.pollFirst();
            if (lpuInProcessSet.contains(Integer.valueOf(memoryBean.getPmpSync().getPmpSyncPK().getLpuId()).toString())) { // В работе ли ещё данная ЛПУ?
                log_info("lpuId = " + memoryBean.getPmpSync().getPmpSyncPK().getLpuId()
                        + " period = " + new SimpleDateFormat("yyyy-MM").format(memoryBean.getPmpSync().getPmpSyncPK().getPeriod())
                        + " was skipped because it is executing at the moment!");
                continue;
            }
            String keyForLpuAndPeriodSet = getKeyForLpuAndPeriodSet(memoryBean);
            // Такое может быть если заявка одновременно поставлена и на формирование и на посылку
            if (lpuAndPeriodSet.contains(keyForLpuAndPeriodSet)) {
                // Если заявка уже поставлена в очередь на формирование, например, то поставить заявку в конец.
                memoryBeanSetForParticularPurpose.addLast(memoryBean);
                // Если мы уже анализируем повторно, заявку, поставленную в конец, то надо прерваться,
                // так как все остальные уже были проанализированы и поставлены в хвост очереди.
                if (lpuAndPeriodSetLocal.contains(keyForLpuAndPeriodSet)) {
                    log_info("lpuId = " + memoryBean.getPmpSync().getPmpSyncPK().getLpuId()
                            + " period = " + new SimpleDateFormat("yyyy-MM").format(memoryBean.getPmpSync().getPmpSyncPK().getPeriod())
                            + " was breaked because it is already in work and was checked second time!");
                    break;
                } else {
                    // Если мы первый раз анализируем заявку, которая уже поставлена в очередь на формирование, то за ней
                    // могут следовать какие-то ещё другие заявки - мы должны их проанализировать, а эту пропустить.
                    log_info("lpuId = " + memoryBean.getPmpSync().getPmpSyncPK().getLpuId()
                            + " period = " + new SimpleDateFormat("yyyy-MM").format(memoryBean.getPmpSync().getPmpSyncPK().getPeriod())
                            + " was skipped because it is already in work!");
                    lpuAndPeriodSetLocal.add(keyForLpuAndPeriodSet);
                    continue;
                }
            }
            Long freeMemory = targetSystemBeanWrapper.getFreeMemory();
            Long possibleMemoryUsage = memoryBean.getPossibleMemoryUsage();
            Long remainedMemory = freeMemory - possibleMemoryUsage;
            int quota = targetSystemBeanWrapper.getQuota();
            int totalAmountOfProcesses = targetSystemBeanWrapper.getTotalAmountOfProcesses();
            // По идее бы надо бы завести отдельную настройку для ПОСОБРа...
            if ((remainedMemory > targetSystemBeanWrapper.getMinMemoryForSend() && totalAmountOfProcesses < quota)
                    || (targetSystemBeanWrapper.getProcessSet().isEmpty() && totalAmountOfProcesses == 0)) {
                targetSystemBeanWrapper.setFreeMemory(remainedMemory);
                memoryBean.setTargetSystemBeanWrapper(targetSystemBeanWrapper);
                targetSystemBeanWrapper.addPlanedProcessExecution();
                lpuAndPeriodSet.add(keyForLpuAndPeriodSet);
                memoryBeanSet.add(memoryBean);
            } else {
                log_info("Task was skipped for " + memoryBean.getPmpSync().getPmpSyncPK().getLpuId() + "! Server = " + targetSystemBeanWrapper.getHost()
                        + " freeMemory = " + freeMemory.toString()
                        + " possibleMemoryUsage = " + possibleMemoryUsage.toString()
                        + " remainedMemory = " + remainedMemory.toString()
                        + " quota = " + quota + " totalAmountOfProcesses = " + totalAmountOfProcesses
                        + " processCount = " + targetSystemBeanWrapper.getProcessSet().size() + "!"
                );
                memoryBeanSetForParticularPurpose.addFirst(memoryBean);
                break;
            }
        }
    }

    private void handleClaim(Collection<MemoryBean> memoryBeanCollection) {
        for (MemoryBean memoryBean : memoryBeanCollection) {
            PmpSync pmpSync = memoryBean.getPmpSync();
            String callData = pmpSync.getPmpSyncPK().getCallData();
            String operationMode = null;
            String callType = null;
            String moId = pmpSync.getPmpSyncPK().getLpuId() + "";
            String periodMonth = new SimpleDateFormat("MM").format(pmpSync.getPmpSyncPK().getPeriod());
            String periodYear = new SimpleDateFormat("yyyy").format(pmpSync.getPmpSyncPK().getPeriod());
            if (callData.startsWith(RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST)) {
                if (pmpSync.getFeatureName().equals(RecreateBillsFeature.NAME)) {
                    operationMode = "-m";
                } else if (pmpSync.getFeatureName().equals(RecreateBillsFeature.CALC_POSOBR)) {
                    operationMode = "-p";
                }
                callType = RecreateBillsFeature.NAME;
            } else if (callData.startsWith(RecreateBillsRequest.SEND_BILLS_VIRTUAL_REQUEST)) {
                operationMode = "-ss";
                callType = RecreateBillsFeature.SEND;
            } else if (callData.startsWith(RecreateBillsRequest.SMO_BILL_FLK_VIRTUAL_REQUEST)) {
                operationMode = "-smoflk";
                callType = RecreateBillsFeature.SMO_BILL_FLK;
            }
            BigDecimal usage = BigDecimal.valueOf(memoryBean.getPossibleMemoryUsage()).divide(BigDecimal.valueOf(1024L));
//            if (callType.equals(RecreateBillsFeature.SEND)) {
//                usage = BigDecimal.valueOf(1024L * 10L);
//            }
            Long possibleMemoryUsage = Math.max(8L, Math.min(Math.round(usage.longValue() + 2L + usage.multiply(BigDecimal.valueOf(0.4)).longValue()), 80L));
            log_info("Claim handled for moId: " + moId + " operationMode: " + operationMode + " serviceCount: " + memoryBean.getServiceCount() + " possibleMemoryUsage: " + possibleMemoryUsage + "!");
            ProcessBean processBean = new ProcessBean();
            processBean.setMoId(moId);
            processBean.setOperationMode(operationMode);
            processBean.setPossibleMemoryUsage(possibleMemoryUsage);
            processBean.setPeriodYear(periodYear);
            processBean.setPeriodMonth(periodMonth);
            processBean.setType(callType);
            processBean.setPeriod(pmpSync.getPmpSyncPK().getPeriod());
            processBean.setParameters(pmpSync.getParameters());
            processBean.setUserId(pmpSync.getUserId());
            if (callType == null) {
                continue;
            }
            processBean.setTargetSystemBean(memoryBean.getTargetSystemBeanWrapper());
            taskQueue.offer(processBean);
        }
    }

    private static String getMemoryKey(PmpSync pmpSync) {
        return pmpSync.getPmpSyncPK().getLpuId() + "_" + (new SimpleDateFormat("yyyy_MM_dd").format(pmpSync.getPmpSyncPK().getPeriod()));
    }

    private boolean isItAllowedToExecuteLookingByMemoryCriteria(Long freeMemory, TargetSystemBean targetSystemBean, ProcessBean processBean) {
        if (processBean.getType().equals(RecreateBillsFeature.NAME)) {
            if (freeMemory > targetSystemBean.getMinMemoryForRecreate()) {
                return true;
            } else {
                log_info(targetSystemBean.getHost() + " doesn't have enough memory for recreate process! Minimal memory = " + targetSystemBean.getMinMemoryForRecreate().toString() + " but remains only " + freeMemory.toString() + "!");
                return false;
            }
        } else if (processBean.getType().equals(RecreateBillsFeature.SEND)) {
            if (freeMemory > targetSystemBean.getMinMemoryForSend()) {
                return true;
            } else {
                log_info(targetSystemBean.getHost() + " doesn't have enough memory for send process! Minimal memory = " + targetSystemBean.getMinMemoryForRecreate().toString() + " but remains only " + freeMemory.toString() + "!");
                return false;
            }
        } else {
            return false;
        }
    }

    private void init() {
        if (!inited) {
//            System.setProperty("log.name", "D:\\tmp\\zzzzzRecreateExecute.log");
            try {
                log_info("jarPath: " + jarPath);
                log_info("configPath: " + configPath);
                targetSystemBeanCollection = initRemotes();
                updateRemotes(targetSystemBeanCollection);
                deleteOldRemotesDirs(targetSystemBeanCollection);
                PART_IN_WORK = targetSystemBeanCollection.stream().map(TargetSystemBean::getQuota).mapToInt(i -> i.intValue()).sum();
                taskQueue = new LinkedBlockingQueue<>(PART_IN_WORK);
                targetSystemWrapperBeanLinkedList = targetSystemBeanCollection.stream().map(targetSystemBean -> new TargetSystemBeanWrapper(targetSystemBean)).collect(Collectors.toCollection(LinkedList::new));
                targetSystemBeanCollectionByHost = targetSystemWrapperBeanLinkedList.stream().collect(Collectors.groupingBy(TargetSystemBean::getHost, Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));

//                LinkedBlockingQueue<TargetSystemBean> quotaQueue = new LinkedBlockingQueue<>(quotaSum);
//                QuotaThread quotaThread = new QuotaThread(quotaQueue, executeUtils, targetSystemBeanList);
//                quotaThread.setDaemon(true);
//                quotaThread.start();
                for (int i = 0; i < PART_IN_WORK; i++) {
                    ExecuteThread executeThread = new ExecuteThread(executeUtils, taskQueue, configPath, recreateJar, lpuInProcessSet);
                    executeThread.setDaemon(true);
                    executeThread.setName("Thread_" + i);
                    executeThread.start();
                    executeThreadList.add(executeThread);
                }
                inited = true;
            } catch (Exception e) {
                log_error(jarPath, e);
            }
        }
    }

    // Потом сделать private! public - это для отладки!
    public List<TargetSystemBean> initRemotes() {
        log_info("initRemotes...");
        recreateJar = new File(jarPath);
        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(remotesConfig);
        List<TargetSystemBean> targetSystemBeanList = new LinkedList<>();
        String remoteDirName = dateToRemoteDirName.apply(LocalDateTime.now());
//        String remoteDirName = "2018_03_05__15_38_35"; // debug!!!
        while (matcher.find()) {
            String targetSystemBeanStr = matcher.group(1);
            String[] targetSystemBeanStrSplited = targetSystemBeanStr.split(",");

            String osStr = targetSystemBeanStrSplited[0].trim();
            OsEnum os;
            if (osStr.equals("aix")) {
                os = OsEnum.AIX;
            } else if (osStr.equals("windows")) {
                os = OsEnum.WINDOWS;
            } else {
                os = OsEnum.LINUX;
            }
            String s = TargetSystemBean.s.apply(os);
            String workingDir = targetSystemBeanStrSplited[6].trim();
            File pmpConfigPath = new File(configPath);

            String remoteWorkingDirFullPath = workingDir + s + remoteDirName + s;
            String remoteLibDirFullPath = workingDir + s + remoteDirName + s + "lib" + s;
            String remoteConfDirFullPath = workingDir + s + remoteDirName + s + "conf" + s;
            String remoteDbfDirFullPath = workingDir + s + "dbf";

            String jarPath = remoteWorkingDirFullPath + recreateJar.getName();
            String confPath = remoteConfDirFullPath + pmpConfigPath.getName();

            TargetSystemBean targetSystemBean = new TargetSystemBean(
                    os, Integer.valueOf(targetSystemBeanStrSplited[1].trim()).intValue(),
                    targetSystemBeanStrSplited[2].trim(), targetSystemBeanStrSplited[3].trim(), targetSystemBeanStrSplited[4].trim(),
                    Integer.valueOf(targetSystemBeanStrSplited[5].trim()).intValue(), workingDir,
                    getMemoryConfig(targetSystemBeanStrSplited[7].trim()), getMemoryConfig(targetSystemBeanStrSplited[8].trim()),
                    targetSystemBeanStrSplited[9].trim().equals("null") ? null : targetSystemBeanStrSplited[9].trim(),
                    targetSystemBeanStrSplited[10].trim().equals("null") ? null : targetSystemBeanStrSplited[10].trim(),
                    jarPath, confPath, remoteWorkingDirFullPath, remoteLibDirFullPath,
                    remoteConfDirFullPath, remoteDbfDirFullPath, remoteDirName
            );
            targetSystemBeanList.add(targetSystemBean);
        }
        return targetSystemBeanList;
    }

    Function<LocalDateTime, String> dateToRemoteDirName = now -> now.getYear() + "_" + intToStr(now.getMonthValue()) + "_" + intToStr(now.getDayOfMonth()) + "__" + intToStr(now.getHour()) + "_" + intToStr(now.getMinute()) + "_" + intToStr(now.getSecond());

    private void updateRemotes(List<TargetSystemBean> targetSystemBeanList) {
        log_info("updateRemotes...");
//        List<Thread> updateThreadList = new ArrayList<>(targetSystemBeanList.size());
        for (final TargetSystemBean targetSystemBean : targetSystemBeanList) {
//            updateThreadList.add(new Thread(() -> {
            log_info("New version upload started on " + targetSystemBean.getHost() + " server to the " + targetSystemBean.getWorkingDir() + targetSystemBean.getS() + targetSystemBean.getRemoteDirName() + " directory!");
            executeUtils.uploadNewVersion(new TargetSystemBeanWrapper(targetSystemBean), targetSystemBean.getRemoteDirName(), recreateJar, new File(configPath));
            log_info("New version was uploaded to: " + targetSystemBean.getHost() + " server to the " + targetSystemBean.getWorkingDir() + targetSystemBean.getS() + targetSystemBean.getRemoteDirName() + " directory!");
//            }));
        }
//        final AtomicInteger i = new AtomicInteger(0);
//        updateThreadList.stream().forEach(thread -> {
//            thread.setName("UpdateThread_" + i.incrementAndGet());
//            thread.start();
//        });
//        updateThreadList.stream().forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException ex) {
//                java.util.logging.Logger.getLogger(ExecuteRecreate.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
    }

    private Long getMemoryConfig(String memStr) {
        if (memStr.contains("G")) {
            return Long.valueOf(memStr.replace("G", "")) * 1024L;
        } else if (memStr.contains("M")) {
            return Long.valueOf(memStr.replace("M", ""));
        } else {
            return null;
        }
    }

    private void deleteOldRemotesDirs(List<TargetSystemBean> targetSystemBeanList) {
        final Pattern dirnamePattern = Pattern.compile("^.*?(\\d\\d\\d\\d_\\d\\d_\\d\\d__\\d\\d_\\d\\d_\\d\\d).*$");
        for (final TargetSystemBean targetSystemBean : targetSystemBeanList) {
            List<OsProcessBean> processList = executeUtils.getProcessList(new TargetSystemBeanWrapper(targetSystemBean));
            if (processList == null) {
                continue;
            }
            // Проверяем можно ли удалять старые каталоги так: имеются ли процессы, у которых в командной строке что-то не из нашего текущего рабочего каталога?
//            boolean anyMatch = processList.stream().anyMatch(processBean -> !processBean.getProcessCmd().contains(targetSystemBean.getRemoteDirName()));
            Set<String> dirNameSet = processList.stream().map(processBean -> {
                String processCmd = processBean.getProcessCmd();
                Matcher matcher = dirnamePattern.matcher(processCmd);
                if (matcher.find()) {
                    String dirName = matcher.group(1);
                    return dirName;
                } else {
                    return null;
                }
            }).filter(str -> str != null).collect(Collectors.toSet());
//            if (!anyMatch) {
            List<String> deleteOldJars = executeUtils.deleteOldJars(new TargetSystemBeanWrapper(targetSystemBean), targetSystemBean.getWorkingDir(), dirnamePattern, dirNameSet);
            if (!deleteOldJars.isEmpty()) {
                deleteOldJars.stream().forEach(dir -> {
                    log_info("Dir: " + dir + " was deleted on " + targetSystemBean.getHost() + " server!");
                });
            } else {
                log_info("There is nothing to delete on: " + targetSystemBean.getHost() + " server because some processes exist!");
                log_info("Process List on " + targetSystemBean.getHost() + ":");
                processList.stream().forEach(process -> {
                    log_info(targetSystemBean.getHost() + ": " + process.getProcessCmd());
                });
            }
            dirNameSet.stream().forEach(dir -> {
                log_info("Dir: " + dir + " is in use at the moment on " + targetSystemBean.getHost() + " server!");
            });
        }
    }

    private void getServiceAmountForProcessBeans(Set<PmpSync> pmpSyncList, Map<String, List<OsProcessBean>> allProcessesMapByHostIp) {
        Stream<QueueLpuBean> pmpSyncStream = pmpSyncList.stream().map(pmpSync -> new QueueLpuBean(pmpSync.getPmpSyncPK().getPeriod(), pmpSync.getFeatureName(), pmpSync.getPmpSyncPK().getLpuId() + ""));

        for (Entry<String, List<OsProcessBean>> entry : allProcessesMapByHostIp.entrySet()) {
            pmpSyncStream = Stream.concat(pmpSyncStream, entry.getValue().stream()
                    .filter(process -> process.getPeriod() != null && process.getType() != null && process.getLpuId() != null)
                    .map(process -> new QueueLpuBean(process.getPeriod(), process.getType(), process.getLpuId().toString())));
        }

        Map<Date, Map<String, Set<String>>> lpuIdsByTypeAndByPeriod = pmpSyncStream
                .collect(Collectors.groupingBy(QueueLpuBean::getPeriod, TreeMap::new,
                        Collectors.groupingBy(QueueLpuBean::getType,
                                Collectors.mapping(QueueLpuBean::getLpuId, Collectors.toSet()))));

        Map<Date, Map<String, Map<String, RamBean>>> possibleRamUsageToLpuIdByTypeAndByPeriod = new HashMap<>();

        final Date now = new Date();

        TriFunction<Date, String, String, Boolean> defineAlreadyCalculatedValues = (period, type, lpuId) -> {
            Optional<Boolean> optionalValue = Optional.ofNullable(possibleRamUsageToLpuIdByTypeAndByPeriodGlobal)
                    .map(map -> map.get(period))
                    .map(map -> map.get(type))
                    .map(map -> map.get(lpuId))
                    .map(map -> dateCriteria(map.getCreated(), now));
            return optionalValue.orElse(false);
        };

        for (Entry<Date, Map<String, Set<String>>> entry : lpuIdsByTypeAndByPeriod.entrySet()) {
            final Date period = entry.getKey();
            Optional<Map<String, Set<String>>> entryValue = Optional.ofNullable(entry.getValue());
            final Set<String> lpuIdSetForRecreate = entryValue.map(map -> map.get(RecreateBillsFeature.NAME)).orElse(new HashSet<>()).stream().filter(lpuId -> !defineAlreadyCalculatedValues.apply(period, RecreateBillsFeature.NAME, lpuId)).collect(Collectors.toSet());
            final Set<String> lpuIdSetForSend = entryValue.map(map -> map.get(RecreateBillsFeature.SEND)).orElse(new HashSet<>()).stream().filter(lpuId -> !defineAlreadyCalculatedValues.apply(period, RecreateBillsFeature.SEND, lpuId)).collect(Collectors.toSet());
            final Set<String> lpuIdSetForPosobr = entryValue.map(map -> map.get(RecreateBillsFeature.CALC_POSOBR)).orElse(new HashSet<>()).stream().filter(lpuId -> !defineAlreadyCalculatedValues.apply(period, RecreateBillsFeature.CALC_POSOBR, lpuId)).collect(Collectors.toSet());
            final Set<String> lpuIdSetForSmoFlk = entryValue.map(map -> map.get(RecreateBillsFeature.SMO_BILL_FLK)).orElse(new HashSet<>()).stream().filter(lpuId -> !defineAlreadyCalculatedValues.apply(period, RecreateBillsFeature.SMO_BILL_FLK, lpuId)).collect(Collectors.toSet());

            final Set<String> lpuIdSetForRecreateAndForSend = new HashSet<>();
            lpuIdSetForRecreateAndForSend.addAll(lpuIdSetForRecreate);
            lpuIdSetForRecreateAndForSend.addAll(lpuIdSetForSend);

            if (!lpuIdSetForRecreateAndForSend.isEmpty()) {
                Date periodEnd = Date.from(LocalDateTime.ofInstant(period.toInstant(), ZoneId.systemDefault()).plusMonths(1L).minusSeconds(1L).toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())));
                List<Object[]> objList = executeRecreateDAO.getServiceCount(period, periodEnd, lpuIdSetForRecreateAndForSend);
//                List<Object[]> objList = new ArrayList<>(); // Debug!
//                objList.add(new Object[]{objList_.get(0)[0], 708000L});
                for (Object[] objs : objList) {
                    log_info("lpuId: " + objs[0].toString() + " serviceCount: " + objs[1].toString());
                }
//            Map<Object, List<Object[]>> objectsCountByLpuId =
                Function<Long, RamBean> possibleRamLoad = serviceCount -> {
                    BigDecimal k = BigDecimal.valueOf(1); // Это поправочный импирический коэффициент! Формула тоже приблизительная, и основанная на приблизительной статистике.
                    BigDecimal totalMemoryUsage = k.multiply(BigDecimal.valueOf(serviceCount).multiply(BigDecimal.valueOf(40L * 1024L).divide(BigDecimal.valueOf(400L * 1000L))));
                    Long ramUsage = totalMemoryUsage.compareTo(BigDecimal.valueOf(512L)) == 1 ? totalMemoryUsage.longValue() : 512L;
                    return new RamBean(ramUsage, new Date(), serviceCount);
                };
                Map<String, RamBean> possibleRamUsageToLpuId = objList.stream().collect(Collectors.groupingBy(objArray -> objArray[0].toString(), TreeMap::new,
                        Collectors.mapping(objArray -> ((Number) objArray[1]).longValue(), Collectors.collectingAndThen(Collectors.toList(), list -> possibleRamLoad.apply(list.get(0))))));
                log_info("--------------------------------------");
                for (Entry<String, RamBean> ramEntry : possibleRamUsageToLpuId.entrySet()) {
                    String lpuId = ramEntry.getKey();
                    Long ramUsage = ramEntry.getValue().getRamUsage();
                    log_info("lpuId: " + lpuId + " ramUsage: " + ramUsage.toString());
                }

                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period) == null) {
                    possibleRamUsageToLpuIdByTypeAndByPeriod.put(period, new HashMap<>());
                }

                Map<String, RamBean> possibleRamUsageToLpuIdForRecreate = possibleRamUsageToLpuId.entrySet().stream().filter(en -> lpuIdSetForRecreate.contains(en.getKey())).collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));
                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.NAME) == null) {
                    possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).put(RecreateBillsFeature.NAME, new HashMap<>());
                }
                possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.NAME).putAll(possibleRamUsageToLpuIdForRecreate);

                Map<String, RamBean> possibleRamUsageToLpuIdForSend = possibleRamUsageToLpuId.entrySet().stream().filter(en -> lpuIdSetForSend.contains(en.getKey())).collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));
                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.SEND) == null) {
                    possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).put(RecreateBillsFeature.SEND, new HashMap<>());
                }
                possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.SEND).putAll(possibleRamUsageToLpuIdForSend);

            }
//            if (!lpuIdSetForSend.isEmpty()) {
//                Map<String, RamBean> possibleRamUsageToLpuId = lpuIdSetForSend.stream().collect(Collectors.groupingBy(lpuId -> lpuId, Collectors.mapping(lpuId -> lpuId, Collectors.collectingAndThen(Collectors.toList(), list -> new RamBean(1024L * 10L, new Date(), 0L))))); // Это надо доделать!!!
//                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period) == null) {
//                    possibleRamUsageToLpuIdByTypeAndByPeriod.put(period, new HashMap<>());
//                }
//                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.SEND) == null) {
//                    possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).put(RecreateBillsFeature.SEND, new HashMap<>());
//                }
//                possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.SEND).putAll(possibleRamUsageToLpuId);
//            }
            if (!lpuIdSetForPosobr.isEmpty()) {
                Map<String, RamBean> possibleRamUsageToLpuId = lpuIdSetForPosobr.stream().collect(Collectors.groupingBy(lpuId -> lpuId, Collectors.mapping(lpuId -> lpuId, Collectors.collectingAndThen(Collectors.toList(), list -> new RamBean(512L, new Date(), -2L)))));
                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period) == null) {
                    possibleRamUsageToLpuIdByTypeAndByPeriod.put(period, new HashMap<>());
                }
                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.CALC_POSOBR) == null) {
                    possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).put(RecreateBillsFeature.CALC_POSOBR, new HashMap<>());
                }
                possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.CALC_POSOBR).putAll(possibleRamUsageToLpuId);
            }
            if (!lpuIdSetForSmoFlk.isEmpty()) {
                Map<String, RamBean> possibleRamUsageToLpuId = lpuIdSetForSmoFlk.stream().collect(Collectors.groupingBy(lpuId -> lpuId, Collectors.mapping(lpuId -> lpuId, Collectors.collectingAndThen(Collectors.toList(), list -> new RamBean(512L, new Date(), -2L)))));
                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period) == null) {
                    possibleRamUsageToLpuIdByTypeAndByPeriod.put(period, new HashMap<>());
                }
                if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.SMO_BILL_FLK) == null) {
                    possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).put(RecreateBillsFeature.SMO_BILL_FLK, new HashMap<>());
                }
                possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(RecreateBillsFeature.SMO_BILL_FLK).putAll(possibleRamUsageToLpuId);
            }
        }
        refreshPossibleRamUsage(now, possibleRamUsageToLpuIdByTypeAndByPeriod);
    }

    private void refreshPossibleRamUsage(final Date now, Map<Date, Map<String, Map<String, RamBean>>> possibleRamUsageToLpuIdByTypeAndByPeriod) {
        for (Entry<Date, Map<String, Map<String, RamBean>>> entry : possibleRamUsageToLpuIdByTypeAndByPeriodGlobal.entrySet()) {
            Date period = entry.getKey();
            Map<String, Map<String, RamBean>> typeToLpuAndPossibleMemoryMap = entry.getValue();
            if (typeToLpuAndPossibleMemoryMap != null && !typeToLpuAndPossibleMemoryMap.isEmpty()) {
                for (Entry<String, Map<String, RamBean>> internalEntry : typeToLpuAndPossibleMemoryMap.entrySet()) {
                    String type = internalEntry.getKey();
                    Map<String, RamBean> lpuIdToMemoryMap = internalEntry.getValue();
                    Map<String, RamBean> lpuIdToMemoryMapNew = new HashMap<>(lpuIdToMemoryMap.size());
                    for (Entry<String, RamBean> lpuEntry : lpuIdToMemoryMap.entrySet()) {
                        String key = lpuEntry.getKey();
                        RamBean value = lpuEntry.getValue();
                        if (dateCriteria(value.getCreated(), now)) {
                            lpuIdToMemoryMapNew.put(key, value);
//                            log_info("refreshPossibleRamUsage: lpuId: " + key + " period: " + new SimpleDateFormat("yyyy-MM").format(period) + " type: " + type + " was refreshed!");
                        } else {
                            log_info("refreshPossibleRamUsage: lpuId: " + key + " period: " + new SimpleDateFormat("yyyy-MM").format(period) + " type: " + type + " was deleted!");
                        }
                    }
                    if (!lpuIdToMemoryMapNew.isEmpty()) {
                        if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period) == null) {
                            possibleRamUsageToLpuIdByTypeAndByPeriod.put(period, new HashMap<>());
                        }
                        if (possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(type) == null) {
                            possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).put(type, new HashMap<String, RamBean>());
                        }
                        possibleRamUsageToLpuIdByTypeAndByPeriod.get(period).get(type).putAll(lpuIdToMemoryMapNew);
                    }
                }
            }
        }
        possibleRamUsageToLpuIdByTypeAndByPeriodGlobal = possibleRamUsageToLpuIdByTypeAndByPeriod;
    }

    private Set<PmpSync> getOldRamUsageRows(final Date now) {
        Set<PmpSync> pmpSyncList = new HashSet<>();
        for (Entry<Date, Map<String, Map<String, RamBean>>> entry : possibleRamUsageToLpuIdByTypeAndByPeriodGlobal.entrySet()) {
            Date period = entry.getKey();
            Map<String, Map<String, RamBean>> typeToLpuAndPossibleMemoryMap = entry.getValue();
            if (typeToLpuAndPossibleMemoryMap != null && !typeToLpuAndPossibleMemoryMap.isEmpty()) {
                for (Entry<String, Map<String, RamBean>> internalEntry : typeToLpuAndPossibleMemoryMap.entrySet()) {
                    String type = internalEntry.getKey();
                    Map<String, RamBean> lpuIdToMemoryMap = internalEntry.getValue();
                    Map<String, RamBean> lpuIdToMemoryMapNew = new HashMap<>(lpuIdToMemoryMap.size());
                    for (Entry<String, RamBean> lpuEntry : lpuIdToMemoryMap.entrySet()) {
                        String key = lpuEntry.getKey();
                        RamBean value = lpuEntry.getValue();
                        if (!dateCriteria(value.getCreated(), now)) {
                            PmpSync pmpSync = new PmpSync(Integer.valueOf(key), period, null);
                            pmpSync.setFeatureName(type);
                            pmpSyncList.add(pmpSync);
                        }
                    }
                }
            }
        }
        return pmpSyncList;
    }

    private boolean dateCriteria(Date created, final Date now) {
        return DateUtils.addMinutes(created, 60).after(now);
    }

    private static String intToStr(int k) {
        String kk = k + "";
        for (int i = kk.length(); i < 2; i++) {
            kk = "0" + kk;
        }
        return kk;
    }

    public void shutdown() {
//        executor.shutdown();
        executeThreadList.stream().forEach(thread -> thread.interrupt());
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private void log_info(String message) {
        log.info(message);
        if (isWindowsOS) {
            System.out.println(message);
        }
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

    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
    }

    @Scheduled(cron = "0 0/20 16,21,22,23 * * *")
    public void cleanExecute() {
        init();
        for (TargetSystemBean targetSystemBean : targetSystemBeanCollection) {
            ProcessBean processBean = new ProcessBean();
            processBean.setMoId(null);
            processBean.setOperationMode("-clean");
            processBean.setPeriodYear(null);
            processBean.setPeriodMonth(null);
            processBean.setType(null);
            processBean.setPeriod(null);
            processBean.setParameters(null);
            processBean.setUserId(null);
            processBean.setTargetSystemBean(new TargetSystemBeanWrapper(targetSystemBean));
            taskQueue.offer(processBean);
        }
    }

}
