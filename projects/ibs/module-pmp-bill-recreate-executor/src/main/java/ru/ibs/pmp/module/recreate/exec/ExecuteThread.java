package ru.ibs.pmp.module.recreate.exec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.isWindowsOS;
import ru.ibs.pmp.module.recreate.exec.bean.ProcessBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBean;

/**
 * @author NAnishhenko
 */
public class ExecuteThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ExecuteThread.class);

    private final LinkedBlockingQueue<ProcessBean> taskQueue;
    private final String pmpConfigPath;
    private final File recreateJar;
    private final ExecuteUtils executeUtils;
//    private final LinkedBlockingQueue<TargetSystemBean> quotaQueue;
    private final Set<String> lpuInProcessSet;

    public ExecuteThread(ExecuteUtils executeUtils, LinkedBlockingQueue<ProcessBean> taskQueue, String pmpConfigPath, File recreateJar, Set<String> lpuInProcessSet) {
        this.executeUtils = executeUtils;
        this.taskQueue = taskQueue;
        this.pmpConfigPath = pmpConfigPath;
        this.recreateJar = recreateJar;
//        this.quotaQueue = quotaQueue;
        this.lpuInProcessSet = lpuInProcessSet;
    }

    @Override
    public void run() {
        try {
            executeProcess();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(ExecuteThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeProcess() throws InterruptedException {
        boolean result = true;
        while (true) {
            ProcessBean processBean = taskQueue.take();
            TargetSystemBean targetSystemBean = processBean.getTargetSystemBean();
            String jarPath = targetSystemBean.getJarPath();
            String confPath = targetSystemBean.getConfPath();
            lpuInProcessSet.add(processBean.getMoId());
//            TargetSystemBean targetSystemBean = quotaQueue.take();
            try {
                final String lpuId = processBean.getMoId();
                final String periodStr = processBean.getPeriodYear() + "-" + addSymbols(processBean.getPeriodMonth());
                final String operationMode = processBean.getOperationMode();
//                String[] executeParams = {"java", "-Xmx40G", "-Dpmp.config.path=" + confPath, "-jar", jarPath, operationMode, lpuId, periodStr};
                String[] executeParams = {"java -Xmx40G -Dpmp.config.path=" + confPath + " -jar " + jarPath + " " + operationMode + " " + lpuId + " " + periodStr};
                log_info(StringUtils.join(executeParams, ", "));
                targetSystemBean.setCommands(executeParams);
                result = executeUtils.executeRemoteProcess(targetSystemBean);
            } catch (Exception e) {
                e.printStackTrace();
                log_error("Process Execute Exception!", e);
                result = false;
            }
            lpuInProcessSet.remove(processBean.getMoId());
//            backLinkQueue.put(true);
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

    private String addSymbols(String str) {
        if (str.length() == 1) {
            return "0" + str;
        } else {
            return str;
        }
    }
}
