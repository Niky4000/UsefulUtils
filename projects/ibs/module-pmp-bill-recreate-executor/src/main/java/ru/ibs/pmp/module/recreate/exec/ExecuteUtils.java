package ru.ibs.pmp.module.recreate.exec;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import ru.ibs.pmp.module.recreate.exec.bean.OsProcessBean;
import ru.ibs.pmp.module.recreate.exec.bean.RunProcessResultBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBeanWrapper;

/**
 * @author NAnishhenko
 */
public interface ExecuteUtils {

    public List<OsProcessBean> getProcessList(TargetSystemBeanWrapper targetSystemBean);

    public boolean executeRemoteProcess(TargetSystemBeanWrapper targetSystemBean);

    public Long getFreeMemory(TargetSystemBeanWrapper targetSystemBean);

    public List<String> deleteOldJars(TargetSystemBeanWrapper targetSystemBean, String remoteDirName, Pattern dirnamePattern, Set<String> workingDirNameSet);

    public void uploadNewVersion(TargetSystemBeanWrapper targetSystemBean, String remoteDirName, File recreateJar, File pmpConfigPath);

    public boolean runProcessInCmd(String[] commands) throws InterruptedException, IOException;

    public RunProcessResultBean runProcessInBackgroung(String[] commands) throws InterruptedException, IOException;

    public RunProcessResultBean runProcess(String[] commands, boolean agregateResult, boolean runInCmd) throws InterruptedException, IOException;
}
