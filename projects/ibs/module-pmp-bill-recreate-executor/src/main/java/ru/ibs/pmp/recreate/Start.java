package ru.ibs.pmp.recreate;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.module.recreate.exec.ExecuteRecreate;
import ru.ibs.pmp.module.recreate.exec.ExecuteRecreateDAO;
import ru.ibs.pmp.module.recreate.exec.ExecuteUtils;
import ru.ibs.pmp.module.recreate.exec.JammedService;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBeanWrapper;

/**
 * @author NAnishhenko
 */
public class Start {

    ApplicationContext applicationContext;

    public Start(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void getProcessListForDebug() throws Exception {
        ExecuteUtils executeUtils = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteUtils.class);
        ExecuteRecreate executeRecreate = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.class);
        Method initRemotes = ExecuteRecreate.class.getDeclaredMethod("initRemotes", null);
        initRemotes.setAccessible(true);
        List<TargetSystemBean> targetSystemBeanCollection = (List<TargetSystemBean>) initRemotes.invoke(executeRecreate, null);
        executeUtils.getProcessList(new TargetSystemBeanWrapper(targetSystemBeanCollection.get(0)));
    }

    private void setCleanScheduledTask(final ExecuteRecreate executeRecreate) {
        LocalTime localNow = LocalTime.of(22, 22, 22);
        LocalTime now = LocalTime.now();
        Duration duration = Duration.between(now, localNow);
        long initalDelay = duration.getSeconds();
        if (initalDelay < 0) {
            initalDelay = SECONDS_IN_DAY - initalDelay;
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> executeRecreate.cleanExecute(), initalDelay, SECONDS_IN_DAY, TimeUnit.SECONDS);
    }

    private static final long SECONDS_IN_DAY = 24L * 60L * 60L;
    private static final long CLEAN_DELAY_TIME = 1L * 30L * 60L;
    private static final long JAMMED_DELAY_TIME = 1L * 15L * 60L;
    private static final long DEADLOCK_DELAY_TIME = 60L;

    private void setDeadlockTask(final JammedService jammedService) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                jammedService.killDeadlocks();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, DEADLOCK_DELAY_TIME, DEADLOCK_DELAY_TIME, TimeUnit.SECONDS);
    }

    private void setCleanScheduledTask2(final ExecuteRecreate executeRecreate) {
        LocalTime now = LocalTime.now();
        LocalTime localNow = now.truncatedTo(ChronoUnit.HOURS).plusHours(1);
        Duration duration = Duration.between(now, localNow);
        long initalDelay = duration.getSeconds();
        if (initalDelay < 0) {
            initalDelay = SECONDS_IN_DAY - initalDelay;
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> executeRecreate.cleanExecute(), initalDelay, CLEAN_DELAY_TIME, TimeUnit.SECONDS);
    }

    private void setCleanScheduledJammedTask(final ExecuteRecreate executeRecreate, final JammedService jammedService) {
        LocalTime now = LocalTime.now();
        LocalTime localNow = now.truncatedTo(ChronoUnit.HOURS).plusHours(1);
        Duration duration = Duration.between(now, localNow);
        long initalDelay = duration.getSeconds();
        if (initalDelay < 0) {
            initalDelay = SECONDS_IN_DAY - initalDelay;
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        List<TargetSystemBean> targetSystemBeanCollection = executeRecreate.initRemotes();
        final LinkedList<TargetSystemBeanWrapper> targetSystemWrapperBeanList = targetSystemBeanCollection.stream().map(targetSystemBean -> new TargetSystemBeanWrapper(targetSystemBean)).collect(Collectors.toCollection(LinkedList::new));
        scheduler.scheduleAtFixedRate(() -> {
            try {
                jammedService.checkForJammedQueries(targetSystemWrapperBeanList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                jammedService.putStuckBillsBackToTheQueue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, initalDelay, JAMMED_DELAY_TIME, TimeUnit.SECONDS);
    }

    public void start() throws Exception {
        ExecuteRecreate executeRecreate = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.class);
        JammedService jammedService = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.JammedService.class);
        setCleanScheduledTask2(executeRecreate);
//        setCleanScheduledJammedTask(executeRecreate, jammedService);
//        setDeadlockTask(jammedService);
        while (true) {
            executeRecreate.mainExecute();
            Thread.sleep(20 * 1000);
        }
    }

    public void start2() throws Exception {
        JammedService jammedService = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.JammedService.class);
        ExecuteRecreate executeRecreate = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.class);
        List<TargetSystemBean> targetSystemBeanCollection = executeRecreate.initRemotes();
        LinkedList<TargetSystemBeanWrapper> targetSystemWrapperBeanList = targetSystemBeanCollection.stream().map(targetSystemBean -> new TargetSystemBeanWrapper(targetSystemBean)).collect(Collectors.toCollection(LinkedList::new));
//        jammedService.checkForJammedQueries(targetSystemWrapperBeanList);
        jammedService.putStuckBillsBackToTheQueue();
    }

    public void startDebug() throws Exception {
        ExecuteRecreateDAO executeRecreateDAO = applicationContext.getBean(ExecuteRecreateDAO.class);
        List<Object[]> objList = executeRecreateDAO.getServiceCount(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-04-01 00:00:00"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-04-30 23:59:59"),
                new HashSet<>(Arrays.asList("1876", "2290", "5114", "5259")));
        for (Object[] objs : objList) {
            System.out.println("lpuId: " + objs[0].toString() + " serviceCount: " + objs[1].toString());
        }
    }

    public static void main(String[] args) throws Exception {
        Start start = new Start(new ClassPathXmlApplicationContext("module.xml"));
        start.start();
    }

}
