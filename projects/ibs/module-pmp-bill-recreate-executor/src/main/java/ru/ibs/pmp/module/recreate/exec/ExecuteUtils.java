package ru.ibs.pmp.module.recreate.exec;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import static ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.isWindowsOS;
import ru.ibs.pmp.module.recreate.exec.bean.OsEnum;
import ru.ibs.pmp.module.recreate.exec.bean.OsProcessBean;
import ru.ibs.pmp.module.recreate.exec.bean.RunProcessResultBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBeanWrapper;

/**
 * @author NAnishhenko
 */
public interface ExecuteUtils {

    public List<OsProcessBean> getProcessList(TargetSystemBeanWrapper targetSystemBean);

    public boolean executeRemoteProcess(TargetSystemBeanWrapper targetSystemBean);

    public Long getFreeMemory(TargetSystemBeanWrapper targetSystemBean);

    public List<String> deleteOldJars(TargetSystemBeanWrapper targetSystemBean, String remoteDirName, Pattern dirnamePattern);

    public void uploadNewVersion(TargetSystemBeanWrapper targetSystemBean, String remoteDirName, File recreateJar, File pmpConfigPath);

    public boolean runProcessInCmd(String[] commands) throws InterruptedException, IOException;

    public RunProcessResultBean runProcessInBackgroung(String[] commands) throws InterruptedException, IOException;

    public RunProcessResultBean runProcess(String[] commands, boolean agregateResult, boolean runInCmd) throws InterruptedException, IOException;
}
