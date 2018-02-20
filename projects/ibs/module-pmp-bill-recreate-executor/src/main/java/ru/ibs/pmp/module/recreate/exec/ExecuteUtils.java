package ru.ibs.pmp.module.recreate.exec;

import java.io.File;
import java.io.IOException;
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

/**
 * @author NAnishhenko
 */
@Service
public class ExecuteUtils {

//    private static final String[] GET_PROCESS_LIST_WIN = new String[]{"TASKLIST"};
    private static final String[] GET_PROCESS_LIST_WIN = new String[]{"WMIC path win32_process get Caption,Processid,Commandline"};
    private static final String[] GET_PROCESS_LIST_LIN = new String[]{"ps -eF | grep recreate"};
    private static final Logger log = LoggerFactory.getLogger(ExecuteThread.class);
    private static final Pattern win32ProcessPattern = Pattern.compile("^(.+?)\\s(.+?)\\s(\\d+?)$", Pattern.DOTALL);
    private static final Pattern win32ProcessPatternWithoutCmd = Pattern.compile("^(.+?)\\s(\\d+?)$", Pattern.DOTALL);
//    private static final Pattern linuxProcessPattern = Pattern.compile("^.+?(\\d+?).+?(java+)$", Pattern.DOTALL);
    private static final Pattern linuxProcessPattern = Pattern.compile("^.+?(\\d+?)[^\\d].+?(java.+)$", Pattern.DOTALL);

    public List<OsProcessBean> getProcessList(TargetSystemBean targetSystemBean) {
        Optional<TargetSystemBean> targetSystemBeanObj = Optional.ofNullable(targetSystemBean);
        OsEnum osType = targetSystemBeanObj.map(tt -> tt.getOs()).orElse(isWindowsOS ? OsEnum.WINDOWS : OsEnum.LINUX);
        Function<TargetSystemBean, List<String>> executeProcessFunction = targetSystemBean == null ? this::runProcessInBackgroungAndGetResponseDataOnly : this::runSshProcess;
        if (osType.equals(OsEnum.WINDOWS)) {
            TargetSystemBean targetSystemBean_ = targetSystemBeanObj.orElse(new TargetSystemBean(GET_PROCESS_LIST_WIN));
            targetSystemBean_.setCommands(GET_PROCESS_LIST_WIN);
            List<String> responseData = executeProcessFunction.apply(targetSystemBean_);
            if (responseData == null) {
                return null;
            }
            List<OsProcessBean> processList = responseData.stream().map(str -> {
                Matcher winMatcher = win32ProcessPattern.matcher(str);
                if (winMatcher.find()) {
                    String processName = winMatcher.group(1);
                    String processCmd = winMatcher.group(2);
                    String processIdStr = winMatcher.group(3);
                    return new OsProcessBean(processName, Integer.valueOf(processIdStr), processCmd);
                } else {
                    Matcher winMatcherWithoutCmd = win32ProcessPatternWithoutCmd.matcher(str);
                    while (winMatcherWithoutCmd.find()) {
                        String processName = winMatcherWithoutCmd.group(1);
                        String processIdStr = winMatcherWithoutCmd.group(2);
                        return new OsProcessBean(processName, Integer.valueOf(processIdStr), null);
                    }
                }
                return null;
            }).filter(bean -> bean != null && bean.getProcessCmd() != null && bean.getProcessName().contains("java")
                    && bean.getProcessName().contains("recreate"))
                    .collect(Collectors.toList());

            return processList;
        } else if (osType.equals(OsEnum.LINUX)) {
            TargetSystemBean targetSystemBean_ = targetSystemBeanObj.orElse(new TargetSystemBean(GET_PROCESS_LIST_LIN));
            targetSystemBean_.setCommands(GET_PROCESS_LIST_LIN);
            List<String> responseData = executeProcessFunction.apply(targetSystemBean_);
            if (responseData == null) {
                return null;
            }
            List<OsProcessBean> processList = responseData.stream().map(str -> {
                Matcher linuxMatcher = linuxProcessPattern.matcher(str);
                if (linuxMatcher.find()) {
                    String processName = linuxMatcher.group(1);
                    String processCmd = linuxMatcher.group(2);
                    String processIdStr = processName;
                    return new OsProcessBean(processName, Integer.valueOf(processIdStr), processCmd);
                }
                return null;
            }).filter(bean -> bean != null && bean.getProcessCmd() != null && bean.getProcessCmd().contains("java"))
                    .collect(Collectors.toList());

            return processList;
        }
        return null;
    }

    public boolean executeRemoteProcess(TargetSystemBean targetSystemBean) {
        Optional<TargetSystemBean> targetSystemBeanObj = Optional.ofNullable(targetSystemBean);
        OsEnum osType = targetSystemBeanObj.map(tt -> tt.getOs()).orElse(isWindowsOS ? OsEnum.WINDOWS : OsEnum.LINUX);
        Function<TargetSystemBean, List<String>> executeProcessFunction = targetSystemBean == null ? this::runProcessInBackgroungAndGetResponseDataOnly : this::runSshProcess;
        executeProcessFunction.apply(targetSystemBean);
        return true; // Для совместимости!
    }

    private List<String> runProcessInBackgroungAndGetResponseDataOnly(TargetSystemBean targetSystemBean) {
        try {
            RunProcessResultBean runProcessInBackgroung = runProcessInBackgroung(targetSystemBean.getCommands());
            List<String> responseData = runProcessInBackgroung.getResponseData();
            return responseData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> runSshProcess(TargetSystemBean targetSystemBean) {
        try {
            SshClient sshClient = new SshClient(targetSystemBean.getHost(), targetSystemBean.getUser(), targetSystemBean.getPassword(), targetSystemBean.getPort());
            return sshClient.execCommand(targetSystemBean.getCommands());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteOldJars(TargetSystemBean targetSystemBean, String remoteDirName, Pattern dirnamePattern) {
        try {
            Optional<TargetSystemBean> targetSystemBeanObj = Optional.ofNullable(targetSystemBean);
            OsEnum osType = targetSystemBeanObj.map(tt -> tt.getOs()).orElse(isWindowsOS ? OsEnum.WINDOWS : OsEnum.LINUX);
            Function<TargetSystemBean, List<String>> executeProcessFunction = targetSystemBean == null ? this::runProcessInBackgroungAndGetResponseDataOnly : this::runSshProcess;
//            SshClient sshClient = new SshClient(targetSystemBean.getHost(), targetSystemBean.getUser(), targetSystemBean.getPassword(), targetSystemBean.getPort());

            String[] commands = null;
            if (osType.equals(OsEnum.WINDOWS)) {
                commands = new String[]{"dir " + targetSystemBean.getWorkingDir()};
            } else if (osType.equals(OsEnum.LINUX)) {
                commands = new String[]{"ls -l " + targetSystemBean.getWorkingDir()};
            }
            targetSystemBean.setCommands(commands);
            List<String> dirList = executeProcessFunction.apply(targetSystemBean);
            dirList.stream().filter(file -> dirnamePattern.matcher(file).matches()).forEach(file -> {
                if (!file.contains(remoteDirName)) {

                    String[] commandsForDelete = null;
                    if (targetSystemBean.getOs().equals(OsEnum.WINDOWS)) {
                        commandsForDelete = new String[]{"rmdir " + targetSystemBean.getWorkingDir() + " /s /q"};
                    } else if (targetSystemBean.getOs().equals(OsEnum.LINUX)) {
                        commandsForDelete = new String[]{"rm -rf " + targetSystemBean.getWorkingDir()};
                    }
                    targetSystemBean.setCommands(commandsForDelete);
//                    executeProcessFunction.apply(targetSystemBean);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadNewVersion(TargetSystemBean targetSystemBean, String remoteDirName, File recreateJar, File pmpConfigPath) {
        try {
            SshClient sshClient = new SshClient(targetSystemBean.getHost(), targetSystemBean.getUser(), targetSystemBean.getPassword(), targetSystemBean.getPort());

            sshClient.execCommand(new String[]{"mkdir " + targetSystemBean.getRemoteWorkingDirFullPath()});
            sshClient.execCommand(new String[]{"mkdir " + targetSystemBean.getRemoteLibDirFullPath()});
            sshClient.execCommand(new String[]{"mkdir " + targetSystemBean.getRemoteConfDirFullPath()});
            targetSystemBean.setJarPath(targetSystemBean.getRemoteWorkingDirFullPath() + recreateJar.getName());
            targetSystemBean.setConfPath(targetSystemBean.getRemoteConfDirFullPath() + pmpConfigPath.getName());
            sshClient.scpTo(targetSystemBean.getJarPath(), recreateJar.getAbsolutePath());
            sshClient.scpTo(targetSystemBean.getConfPath(), pmpConfigPath.getAbsolutePath());
            File libDir = new File(recreateJar.getParentFile().getAbsolutePath() + "/lib");
            for (File libFile : libDir.listFiles()) {
                sshClient.scpTo(targetSystemBean.getRemoteLibDirFullPath() + libFile.getName(), libFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean runProcessInCmd(String[] commands) throws InterruptedException, IOException {
        return runProcess(commands, false, true).isResult();
    }

    public RunProcessResultBean runProcessInBackgroung(String[] commands) throws InterruptedException, IOException {
        return runProcess(commands, true, false);
    }

    public RunProcessResultBean runProcess(String[] commands, boolean agregateResult, boolean runInCmd) throws InterruptedException, IOException {
        List<String> runCommands = new ArrayList<>();
        boolean result = true;
        OutputConsumer outputConsumer = null;
        if (isWindowsOS) {
            if (runInCmd) {
                runCommands.addAll(Arrays.asList("cmd.exe", "/c", "start", "/wait"));
            } else {
                runCommands.addAll(Arrays.asList("cmd.exe", "/c"));
            }
            runCommands.addAll(Arrays.asList(commands));
        } else {
            runCommands.addAll(Arrays.asList(commands));
        }
        Process process = new ProcessBuilder(runCommands)
                .redirectErrorStream(true)
                .start();
        outputConsumer = new OutputConsumer(process, agregateResult);
        outputConsumer.start();
        Thread.sleep(2000);
        if (!process.isAlive()) {
            log.error("No start process recreate!!!");
            result = false;
        }
        while (process.isAlive()) {
            process.waitFor();
        }
        if (outputConsumer.isAlive()) {
            outputConsumer.join();
        }
        return new RunProcessResultBean(result, outputConsumer.getProcessOutput(), outputConsumer.getResponseData());
    }

}
