/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.module.pmp.bill.recreate.exec;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.api.interfaces.bill.RecreateBillsFeature;
import ru.ibs.pmp.api.model.Bill.BillStatus;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.RecreateBillsRequest;
import ru.ibs.pmp.dao.SyncDAO;
import ru.ibs.pmp.module.pmp.bill.recreate.exec.bean.ProcessBean;

/**
 *
 * @author NAnishhenko
 */
public class ExecuteRecreate {

    private static final int SLICE_SIZE = 5;
    private static final int SLICE_SIZE_TO_GET = 8;
//    AtomicInteger i = new AtomicInteger(SLICE_SIZE - 1);
    static AtomicInteger currentThreadCount = new AtomicInteger(0);

    static ApplicationContext applicationContext;
    static SessionFactory sessionFactory;
    static TransactionTemplate tx;
    private static final Pattern pattern = Pattern.compile("^\\w+?\\{" + "moId=" + "(\\d+?)" + ", periodMonth=" + "(\\d+?)" + ", periodYear=" + "(\\d+?)" + "\\}$");

    static boolean isWindowsOS = isWindows();

    private static final String AUTOMATIC = "AUTOMATIC";

    // В VM options надо добавить -Dpmp.config.path=D:\GIT\rmis\etc\pmp
//    public static void main(final String args[]) throws Exception {
//        init();
//        syncDAO = applicationContext.getBean(SyncDAO.class);
//        putStuckBillsBackToTheQueue();
//        deleteOldRowsFromQueueImpl();
//        testExecute(args);
//        mainExecute(args);
//    }
    private static void testExecute(final String[] args) {
        init();
//        org.apache.commons.dbcp2.BasicDataSource ds;
        ExecuteRecreate recreate = new ExecuteRecreate();
        try {
            recreate.executeMassRecreate(new File(args[0]));
        } catch (Exception ex) {
            Logger.getLogger(ExecuteRecreate.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    private static void mainExecute(final String[] args) throws BeansException, InterruptedException {
        if (!validateParameters(args)) {
            System.out.println(""
                    + "Usage example:\n"
                    + "java -Xmx512M -Dpmp.config.path=C:\\pump\\etc\\pmp -jar target\\module-pmp-bill-recreate-1.160420.1-SNAPSHOT.jar 5224 2016-05\n"
                    + "java -Xmx[Memory] -Dpmp.config.path=[Path to configs] -jar [path to jar file] [moid] [period]"
                    + "");
        } else {
            init();
//            startSimpleHttpServer();
            int i = 0;
            int j = 0;
            while (true) {
                System.out.println("Thread started! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                int currentThreadCount_ = currentThreadCount.get();
                if (currentThreadCount_ <= SLICE_SIZE_TO_GET) {
                    new Thread(
                            new Runnable() {

                        @Override
                        public void run() {
                            ExecuteRecreate recreate = new ExecuteRecreate();
                            try {
                                recreate.executeMassRecreate(new File(args[0]));
                            } catch (Exception ex) {
                                Logger.getLogger(ExecuteRecreate.class.getName()).log(Level.SEVERE, null, ex);
                                ex.printStackTrace();
                            }
                        }
                    }
                    ).start();
                }
                System.out.println("Waiting for next execution! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                if (i > 10) {
                    checkForStuckBills();
                    i = 0;
                    j = 0;
                } else if (j > 8) {
                    deleteOldRowsFromQueue();
                    i++;
                    j = 0;
                } else {
                    i++;
                    j++;
                }
//                checkForStuckBills();
//                deleteOldRowsFromQueue();
                Thread.sleep(WAIT_TIME);
            }
        }
    }

    private static void deleteOldRowsFromQueue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteOldRowsFromQueueImpl();
            }
        }).start();
    }

    private static void deleteOldRowsFromQueueImpl() {
        System.out.println("deleteOldRowsFromQueue started!");
        Integer[] executeUpdate = null;
        try {

            final Date now = new Date();
            final Date date = DateUtils.addHours(now, -24);

            executeUpdate = tx.execute(new TransactionCallback<Integer[]>() {
                @Override
                public Integer[] doInTransaction(TransactionStatus ts) {
                    Session session = sessionFactory.openSession();
                    int executeUpdate_ = session.createSQLQuery("update pmp_sync set created=SYSDATE where created is null").executeUpdate();
                    // Почему-то это не пашет!
//                    int executeUpdate = session.createSQLQuery("delete from pmp_sync where failed='1' and created<:date").setDate("date", date).executeUpdate();
                    int executeUpdate = session.createSQLQuery("delete from pmp_sync where failed='1' and created<to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "','yyyy-MM-dd HH24:mi:ss')").executeUpdate();
                    session.close();
                    return new Integer[]{executeUpdate, executeUpdate_};
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("deleteOldRowsFromQueue finished! " + executeUpdate[0].toString() + " rows deleted! " + executeUpdate[1].toString() + " rows updated!");
    }

    private static void checkForStuckBills() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("checkForStuckBills started!");
                try {
                    putStuckBillsBackToTheQueue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("checkForStuckBills finished!");
            }
        }).start();
    }

    private static void putStuckBillsBackToTheQueue() throws TransactionException {
        List<Object[]> stuckBillList = tx.execute(new TransactionCallback<List<Object[]>>() {
            @Override
            public List<Object[]> doInTransaction(TransactionStatus ts) {
                Session session = sessionFactory.openSession();
                List<Object[]> ret = session.createSQLQuery("select distinct b.id,re.mo_id,b.status,re.period from pmp_bill b\n"
                        + "inner join pmp_requirement re on b.requirement_id=re.id\n"
                        + "where b.status like '%QUE%'\n"
                        + "order by re.mo_id,b.status,b.id").list();
                session.close();
                return ret;
            }
        });
        if (!stuckBillList.isEmpty()) {
            Map<StuckBean, List<Long>> lpuByPeriodToBillListForRecreate = new HashMap<>();
            Map<StuckBean, List<Long>> lpuByPeriodToBillListForSend = new HashMap<>();
            for (Object[] stuckBill : stuckBillList) {
                Long billId = ((BigDecimal) stuckBill[0]).longValue();
                String lpuId = (String) stuckBill[1];
                BillStatus billStatus = BillStatus.valueOf((String) stuckBill[2]);
                Date period = (Date) stuckBill[3];
                if (billStatus.equals(BillStatus.RECREATE_QUEUE)) {
                    StuckBean key = new StuckBean(lpuId, period, RecreateBillsFeature.NAME);
                    fillMap(lpuByPeriodToBillListForRecreate, key, billId);
                } else if (billStatus.equals(BillStatus.SEND_QUEUE)) {
                    StuckBean key = new StuckBean(lpuId, period, RecreateBillsFeature.SEND);
                    fillMap(lpuByPeriodToBillListForSend, key, billId);
                }
            }
            handleStuckMap(lpuByPeriodToBillListForRecreate);
            handleStuckMap(lpuByPeriodToBillListForSend);
        }
    }

    private static void handleStuckMap(Map<StuckBean, List<Long>> lpuByPeriodToBillListForRecreate) {
        for (Entry<StuckBean, List<Long>> entry : lpuByPeriodToBillListForRecreate.entrySet()) {
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
            final Boolean canProcess = syncDAO.canProcess(key.getType(), callData, parameters, Integer.valueOf(key.getLpuId()), period, AUTOMATIC);
            System.out.println(callData + " created!");
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

    static SyncDAO syncDAO;

    public static synchronized SyncDAO getSyncDAO() {
        return syncDAO;
    }

//    protected static void findJavaThreads() throws InterruptedException, IOException {
//        if (!isWindowsOS) {
//            System.out.println("Linux!");
//            String execCommand = execCommand(new String[]{"bash", "-c", "ps -eF | grep java"});
//            File file = new File("/home/mls/recreate_exec/log.txt");
//            if (file.exists()) {
//                file.delete();
//            }
//            Files.write(file.toPath(), execCommand.getBytes(), StandardOpenOption.CREATE_NEW);
//            List<Integer> processIds = analizeExecuteResult(execCommand);
//            if (!processIds.isEmpty()) {
//                for (Integer id : processIds) {
//                    String command = "kill -9 " + id.toString();
//                    String execCommand_ = execCommand(new String[]{"bash", "-c", command});
//                    System.out.println("------------command------------");
//                    System.out.println(command);
//                    System.out.println("------------command------------");
//                    System.out.println("----------execCommand----------");
//                    System.out.println(execCommand_);
//                    System.out.println("----------execCommand----------");
//                }
//            } else {
//                System.out.println("processIds is empty!");
//            }
//
//            List<PmpSync> pmpSyncListForDelete = new ArrayList<PmpSync>();
//            List<PmpSync> all = getSyncDAO().getAll();
//            for (PmpSync pmpSync : all) {
//                if (pmpSync.getPmpSyncPK().getCallData().startsWith(RecreateBillsRequest.RECREATE)) {
//                    pmpSyncListForDelete.add(pmpSync);
//                }
//            }
//            if (!pmpSyncListForDelete.isEmpty()) {
//                for (PmpSync pmpSync : pmpSyncListForDelete) {
//                    getSyncDAO().finishProcess(pmpSync.getPmpSyncPK().getFeatureName(), pmpSync.getPmpSyncPK().getCallData());
//                    System.out.println(pmpSync.getPmpSyncPK().getFeatureName() + " " + pmpSync.getPmpSyncPK().getCallData() + " deleted!");
//                }
//            } else {
//                System.out.println("pmpSyncListForDelete is empty!");
//            }
//        } else {
//            System.out.println("Windows!");
//        }
//    }
//    private static void debugLogFile() throws IOException {
//        File file = new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate-executor\\log.txt");
//        byte[] readAllBytes = Files.readAllBytes(file.toPath());
//        String string = new String(readAllBytes);
//        analizeExecuteResult(string);
//    }
//    private static List<Integer> analizeExecuteResult(String result) {
//        List<Integer> processIds = new ArrayList<Integer>();
//        String[] lines = result.split("\n");
//        for (String line : lines) {
//            if (line.contains("-jar /home/mls/recreate/module-pmp-bill-recreate.jar")) {
//                Matcher matcher = Pattern.compile("^[\\w]+?[^\\w]+?([\\d]+?)[^\\w].*$", Pattern.DOTALL).matcher(line);
////                Matcher matcher = Pattern.compile("^(.+?)\\s.*$", Pattern.DOTALL).matcher(line);
//                if (matcher.find()) {
//                    String id = matcher.group(1);
//                    processIds.add(Integer.valueOf(id));
//                }
//            }
//        }
//        return processIds;
//    }
//    protected static String execCommand(String[] command) throws IOException, InterruptedException {
//        Process process = Runtime.getRuntime().exec(command);
//        process.waitFor();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        StringBuilder sb = new StringBuilder("");
//        String line = "";
//        while ((line = reader.readLine()) != null) {
//            sb.append(line + "\n");
//        }
//        System.out.println(sb.toString());
//        return sb.toString();
//    }
    protected static final int BUFFER_SIZE = 4096;
    private static final int WAIT_TIME = 60000;

//    private static String ip;
//    private static Integer port;
    private static boolean validateParameters(String args[]) {
//        Pattern pattern = Pattern.compile("^(\\d+?\\.\\d+?\\.\\d+?\\.\\d+?):(\\d+?)$", Pattern.DOTALL);
        if (!(args != null && args.length == 1)) {
//        if (!(args != null && (args.length == 2) && pattern.matcher(args[1]).matches())) {
            return false;
        }
//        Matcher matcher = pattern.matcher(args[1]);
//        if (matcher.find()) {
//            ip = matcher.group(1);
//            port = Integer.valueOf(matcher.group(2));
//        }

        // Можно проверить что это что-то похожее на путь...
//        try {
//            File file = new File(args[0]);
//            if (file.exists()) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        return true;
    }

    private static void init() throws BeansException {
        applicationContext = new ClassPathXmlApplicationContext("module_exec.xml");
        ru.ibs.pmp.config.PmpProperties propertyConfigurer = (ru.ibs.pmp.config.PmpProperties) applicationContext.getBean("propertyConfigurer");
        setTransactionBeans(applicationContext);
        syncDAO = applicationContext.getBean(SyncDAO.class);
    }

    private static void setTransactionBeans(ApplicationContext applicationContext) throws BeansException {
        sessionFactory = applicationContext.getBean("sessionFactory", SessionFactory.class);
        tx = applicationContext.getBean("transactionTemplate", TransactionTemplate.class);
    }

    public void executeMassRecreate(File recreateJar) throws ParseException, IOException, InterruptedException {
        if (recreateJar.exists()) {
            String pmpConfigPath = System.getProperty("pmp.config.path");
            int lockCount = syncDAO.getLockCount();
            if (lockCount < SLICE_SIZE_TO_GET) {
                List<PmpSync> pmpSyncList = getSyncDAO().getSlice(SLICE_SIZE_TO_GET);
                String operationMode = null;
                List<ProcessBean> list = Collections.synchronizedList(new ArrayList<ProcessBean>(pmpSyncList.size()));
                List<ProcessBean> recreateList = Collections.synchronizedList(new ArrayList<ProcessBean>(pmpSyncList.size()));
                List<ProcessBean> sendList = Collections.synchronizedList(new ArrayList<ProcessBean>(pmpSyncList.size()));

//            List<ProcessBean> rejectionList = Collections.synchronizedList(new ArrayList<ProcessBean>(pmpSyncList.size()));
                Set<String> rejectionSet = new HashSet<String>(pmpSyncList.size());

//            String toCallData = recreateBillsRequest.toCallData();
//            final Boolean canProcess2 = syncDAO.canProcess(key.getType(), toCallData, parameters, Integer.valueOf(key.getLpuId()), key.getPeriod());
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
//                    }
                    }
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
                AtomicInteger i = new AtomicInteger(SLICE_SIZE - 1);
                List<Thread> threadList = new ArrayList<Thread>((SLICE_SIZE >= list.size() ? list.size() : SLICE_SIZE));
                for (int j = 0; j < (SLICE_SIZE >= list.size() ? list.size() : SLICE_SIZE); j++) {
                    Thread thread = executeProcess(list, j, pmpConfigPath, recreateJar, i);
                    if (thread != null) {
                        threadList.add(thread);
                    }
                }
                System.out.println("Waiting for threads!");
                for (Thread thread : threadList) {
                    thread.join();
                }
                System.out.println("Finished!");
            } else {
                System.out.println("To many locks!");
            }
        } else {
            System.out.println("Warning!!! ${pmp.recreate.jar.path} does not exists!!!");
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

    protected Thread executeProcess(final List<ProcessBean> list, int j, final String pmpConfigPath, final File recreateJar, final AtomicInteger i) throws IOException {
        final ProcessBean element = list.get(j);
        final String lpuId = element.getMoId();
        final String periodStr = element.getPeriodYear() + "-" + addSymbols(element.getPeriodMonth());
        final String operationMode = element.getOperationMode();

        final RecreateBillsRequest recreateBillsRequest = new RecreateBillsRequest(lpuId,
                Integer.valueOf(element.getPeriodYear()), Integer.valueOf(element.getPeriodMonth()), new ArrayList<Long>());
        String toCallData = recreateBillsRequest.toCallData();
        final Boolean canProcess2 = syncDAO.canProcess(element.getType(), toCallData, element.getParameters(), Integer.valueOf(lpuId), element.getPeriod(), element.getUserId());
        if (!canProcess2) {
            return null;
        }
        String executeString = "java -Xmx16G -Dpmp.config.path=" + pmpConfigPath + " -jar " + recreateJar.getAbsolutePath() + " " + operationMode + " " + lpuId + " " + periodStr;
        System.out.println(executeString);
        final Process process;
        if (isWindowsOS) {
            process = Runtime.getRuntime().exec("cmd.exe /c start /wait " + executeString);
        } else {
            process = Runtime.getRuntime().exec(executeString);
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    process.waitFor();
                    int incrementAndGet = i.incrementAndGet();
                    if (incrementAndGet < list.size()) {
                        currentThreadCount.incrementAndGet();
                        Thread thread = executeProcess(list, incrementAndGet, pmpConfigPath, recreateJar, i);
                        if (thread != null) {
                            currentThreadCount.decrementAndGet();
                            thread.join();
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ExecuteRecreate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
        return thread;
    }

    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
    }

//    public static void startSimpleHttpServer() {
//        Thread thread = new Thread(() -> {
//            try {
//                // creation de la socket
//                ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName(ip));
//                System.err.println("Serveur lance sur le port : " + port);
//
//                // repeatedly wait for connections, and process
//                while (true) {
//                    // on reste bloque sur l'attente d'une demande client
//                    Socket clientSocket = serverSocket.accept();
//                    System.out.println("Nouveau client connecte");
//
//                    // on ouvre un flux de converation
//                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//
//                    // chaque fois qu'une donnee est lue sur le reseau on la renvoi sur
//                    // le flux d'ecriture.
//                    // la donnee lue est donc retournee exactement au meme client.
//                    String s;
//                    while ((s = in.readLine()) != null) {
//                        System.out.println(s);
//                        if (s.isEmpty()) {
//                            break;
//                        }
//                    }
//
//                    out.write("HTTP/1.0 200 OK\r\n");
//                    out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
//                    out.write("Server: Apache/0.8.4\r\n");
//                    out.write("Content-Type: text/html\r\n");
//                    out.write("Content-Length: 59\r\n");
//                    out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
//                    out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
//                    out.write("\r\n");
//                    out.write("<HTML>");
//                    out.write("<HEAD></HEAD>");
//                    out.write("<TITLE>Exemple</TITLE>");
//                    out.write("<BODY>");
//                    out.write("<P>Ceci est une page d'exemple.</P>");
//                    out.write("</BODY>");
//                    out.write("</HTML>");
//                    // on ferme les flux.
//                    System.out.println("Connexion avec le client terminee");
//                    out.close();
//                    in.close();
//                    clientSocket.close();
//                    findJavaThreads();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        thread.setDaemon(true);
//        thread.start();
//    }
    public static class StuckBean {

        private final String lpuId;
        private final Date period;
        private String parameters;
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

        public String getParameters() {
            return parameters;
        }

        public void setParameters(String parameters) {
            this.parameters = parameters;
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
}
