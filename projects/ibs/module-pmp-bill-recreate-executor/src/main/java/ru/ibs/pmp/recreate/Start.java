package ru.ibs.pmp.recreate;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.module.recreate.exec.ExecuteRecreate;
import ru.ibs.pmp.module.recreate.exec.ExecuteUtils;
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

    private static final long SECONDS_IN_DAY = 24L * 60L * 60L;

    private void setCleanScheduledTask(final ExecuteRecreate executeRecreate) {
        LocalTime localNow = LocalTime.of(22, 22, 22);
//        LocalTime localNow = LocalTime.of(18, 44, 22);
        LocalTime now = LocalTime.now();
        Duration duration = Duration.between(now, localNow);
        long initalDelay = duration.getSeconds();
        if (initalDelay < 0) {
            initalDelay = SECONDS_IN_DAY - initalDelay;
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> executeRecreate.cleanExecute(), initalDelay, SECONDS_IN_DAY, TimeUnit.SECONDS);
    }

    public void start() throws Exception {
        ExecuteRecreate executeRecreate = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.class);
        setCleanScheduledTask(executeRecreate);
        while (true) {
            executeRecreate.mainExecute();
            Thread.sleep(20 * 1000);
        }
//        Thread.sleep(20000 * 1000);
    }

    public static void main(String[] args) throws Exception {
        Start start = new Start(new ClassPathXmlApplicationContext("module.xml"));
        start.start();
    }
}
