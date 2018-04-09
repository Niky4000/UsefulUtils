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
@Service
public class ExecuteUtilsImpl implements ExecuteUtils {

//    private static final String[] GET_PROCESS_LIST_WIN = new String[]{"TASKLIST"};
    private static final String[] GET_PROCESS_LIST_WIN = new String[]{"WMIC path win32_process get Caption,Processid,Commandline"};
    private static final String[] GET_PROCESS_LIST_LIN = new String[]{"ps -eF | grep recreate"};
    private static final String[] GET_PROCESS_LIST_AIX = new String[]{"ps -ef | grep recreate"};
    private static final Logger log = LoggerFactory.getLogger(ExecuteThread.class);
    private static final Pattern win32ProcessPattern = Pattern.compile("^(.+?)\\s(.+?)\\s(\\d+?)$", Pattern.DOTALL);
    private static final Pattern win32ProcessPatternWithoutCmd = Pattern.compile("^(.+?)\\s(\\d+?)$", Pattern.DOTALL);
//    private static final Pattern linuxProcessPattern = Pattern.compile("^.+?(\\d+?).+?(java+)$", Pattern.DOTALL);
    private static final Pattern linuxProcessPattern = Pattern.compile("^.+?(\\d+?)[^\\d].+?(java.+)$", Pattern.DOTALL);

    @Override
    public List<OsProcessBean> getProcessList(TargetSystemBeanWrapper targetSystemBean) {
        Optional<TargetSystemBeanWrapper> targetSystemBeanObj = Optional.ofNullable(targetSystemBean);
        OsEnum osType = targetSystemBeanObj.map(tt -> tt.getOs()).orElse(isWindowsOS ? OsEnum.WINDOWS : OsEnum.LINUX);
        Function<TargetSystemBeanWrapper, List<String>> executeProcessFunction = targetSystemBean == null ? this::runProcessInBackgroungAndGetResponseDataOnly : this::runSshProcess;
        if (osType.equals(OsEnum.WINDOWS)) {
            TargetSystemBeanWrapper targetSystemBean_ = targetSystemBeanObj.orElse(new TargetSystemBeanWrapper(GET_PROCESS_LIST_WIN));
            targetSystemBean_.setCommands(GET_PROCESS_LIST_WIN);
            return handleWindowsResponseData(executeProcessFunction, targetSystemBean_);
        } else if (osType.equals(OsEnum.LINUX)) {
            TargetSystemBeanWrapper targetSystemBean_ = targetSystemBeanObj.orElse(new TargetSystemBeanWrapper(GET_PROCESS_LIST_LIN));
            targetSystemBean_.setCommands(GET_PROCESS_LIST_LIN);
            List<String> responseData = executeProcessFunction.apply(targetSystemBean_);
            return handleLinuxResponseData(responseData);
        } else if (osType.equals(OsEnum.AIX)) {
            TargetSystemBeanWrapper targetSystemBean_ = targetSystemBeanObj.orElse(new TargetSystemBeanWrapper(GET_PROCESS_LIST_AIX));
            targetSystemBean_.setCommands(GET_PROCESS_LIST_AIX);
            List<String> responseData = executeProcessFunction.apply(targetSystemBean_);
            return handleLinuxResponseData(responseData);
        }
        return null;
    }

    private List<OsProcessBean> handleLinuxResponseData(List<String> responseData) {
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
        }).filter(bean -> bean != null && bean.getProcessCmd() != null && bean.getProcessCmd().contains("java") && bean.getProcessCmd().contains("recreate") && !bean.getProcessCmd().contains("-Dcommon.cas.base"))
                .collect(Collectors.toList());

        return processList;
    }

    private List<OsProcessBean> handleWindowsResponseData(Function<TargetSystemBeanWrapper, List<String>> executeProcessFunction, TargetSystemBeanWrapper targetSystemBean_) {
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
        }).filter(bean -> bean != null && bean.getProcessCmd() != null && bean.getProcessCmd().contains("java") && bean.getProcessCmd().contains("recreate") && !bean.getProcessCmd().contains("-Dcommon.cas.base"))
                .collect(Collectors.toList());

        return processList;
    }

    @Override
    public boolean executeRemoteProcess(TargetSystemBeanWrapper targetSystemBean) {
        Optional<TargetSystemBean> targetSystemBeanObj = Optional.ofNullable(targetSystemBean);
        OsEnum osType = targetSystemBeanObj.map(tt -> tt.getOs()).orElse(isWindowsOS ? OsEnum.WINDOWS : OsEnum.LINUX);
        Function<TargetSystemBeanWrapper, List<String>> executeProcessFunction = targetSystemBean == null ? this::runProcessInBackgroungAndGetResponseDataOnly : this::runSshProcess;
        executeProcessFunction.apply(targetSystemBean);
        return true; // Для совместимости!
    }

    private List<String> runProcessInBackgroungAndGetResponseDataOnly(TargetSystemBeanWrapper targetSystemBean) {
        try {
            RunProcessResultBean runProcessInBackgroung = runProcessInBackgroung(targetSystemBean.getCommands());
            List<String> responseData = runProcessInBackgroung.getResponseData();
            return responseData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> runSshProcess(TargetSystemBeanWrapper targetSystemBean) {
        try {
            SshClient sshClient = new SshClient(targetSystemBean.getHost(), targetSystemBean.getUser(), targetSystemBean.getPassword(), targetSystemBean.getPort(), !targetSystemBean.getOs().equals(OsEnum.AIX));
            return sshClient.execCommand(targetSystemBean.getCommands());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Long getFreeMemory(TargetSystemBeanWrapper targetSystemBean) {
        Optional<TargetSystemBean> targetSystemBeanObj = Optional.ofNullable(targetSystemBean);
        OsEnum osType = targetSystemBeanObj.map(tt -> tt.getOs()).orElse(isWindowsOS ? OsEnum.WINDOWS : OsEnum.LINUX);
        Function<TargetSystemBeanWrapper, List<String>> executeProcessFunction = targetSystemBean == null ? this::runProcessInBackgroungAndGetResponseDataOnly : this::runSshProcess;
        String[] commands = null;
        if (osType.equals(OsEnum.WINDOWS)) {
            commands = new String[]{"wmic OS get FreePhysicalMemory /Value"};
        } else if (osType.equals(OsEnum.LINUX)) {
            commands = new String[]{"cat /proc/meminfo"};
        } else if (osType.equals(OsEnum.AIX)) {
            commands = new String[]{"svmon -G -O unit=MB"};
        }
        targetSystemBean.setCommands(commands);
        List<String> freeMemoryList = executeProcessFunction.apply(targetSystemBean);
        if (osType.equals(OsEnum.WINDOWS)) {
            for (String str : freeMemoryList) {
                if (str.contains("FreePhysicalMemory=")) {
                    Matcher matcher = Pattern.compile("^FreePhysicalMemory=([\\d]+?)[^\\d]*$").matcher(str);
                    if (matcher.find()) {
                        Long freeMemory = Long.valueOf(matcher.group(1));
                        return freeMemory / 1024L; // Megabytes
                    }
                    break;
                }
            }
        } else if (osType.equals(OsEnum.LINUX)) {
            for (String str : freeMemoryList) {
                if (str.contains("MemFree:")) {
                    Matcher matcher = Pattern.compile("^[^\\d]+?([\\d]+?)[^\\d]+$").matcher(str);
                    if (matcher.find()) {
                        Long freeMemory = Long.valueOf(matcher.group(1));
                        return freeMemory / 1024L; // Megabytes
                    }
                    break;
                }
            }
        } else if (osType.equals(OsEnum.AIX)) {
            for (String str : freeMemoryList) {
                if (str.contains("memory")) {
                    String digit = "[\\d\\.]+?\\s";
                    Matcher matcher = Pattern.compile("^memory\\s" + digit + digit + "(" + digit + ")" + ".+$").matcher(str);
                    if (matcher.find()) {
                        String digitStr = matcher.group(1);
                        Long freeMemory = digitStr.contains(".") ? Long.valueOf(digitStr.substring(0, digitStr.indexOf("."))) : Long.valueOf(digitStr);
                        return freeMemory; // Megabytes
                    }
                    break;
                }
            }
        }
        return 0L;
    }

    @Override
    public List<String> deleteOldJars(TargetSystemBeanWrapper targetSystemBean, String remoteDirName, Pattern dirnamePattern) {
        List<String> deletedFolder = new ArrayList<>();
        try {
            Optional<TargetSystemBean> targetSystemBeanObj = Optional.ofNullable(targetSystemBean);
            OsEnum osType = targetSystemBeanObj.map(tt -> tt.getOs()).orElse(isWindowsOS ? OsEnum.WINDOWS : OsEnum.LINUX);
            Function<TargetSystemBeanWrapper, List<String>> executeProcessFunction = targetSystemBean == null ? this::runProcessInBackgroungAndGetResponseDataOnly : this::runSshProcess;
            String[] commands = null;
            if (osType.equals(OsEnum.WINDOWS)) {
                commands = new String[]{"dir " + targetSystemBean.getWorkingDir()};
            } else if (osType.equals(OsEnum.LINUX) || osType.equals(OsEnum.AIX)) {
                commands = new String[]{"ls -l " + targetSystemBean.getWorkingDir()};
            }
            targetSystemBean.setCommands(commands);
            List<String> dirList = executeProcessFunction.apply(targetSystemBean);
            dirList.stream().filter(file -> dirnamePattern.matcher(file).matches()).forEach(file -> {
                Matcher matcher = dirnamePattern.matcher(file);
                if (matcher.find()) {
                    String dirName = matcher.group(1);
                    if (!targetSystemBean.getRemoteWorkingDirFullPath().contains(dirName)) {
                        String[] commandsForDelete = null;
                        if (targetSystemBean.getOs().equals(OsEnum.WINDOWS)) {
                            commandsForDelete = new String[]{"rmdir " + targetSystemBean.getWorkingDir() + targetSystemBean.getS() + dirName + " /s /q"};
                        } else if (targetSystemBean.getOs().equals(OsEnum.LINUX) || targetSystemBean.getOs().equals(OsEnum.AIX)) {
                            commandsForDelete = new String[]{"rm -rf " + targetSystemBean.getWorkingDir() + targetSystemBean.getS() + dirName};
                        }
                        targetSystemBean.setCommands(commandsForDelete);
                        executeProcessFunction.apply(targetSystemBean);
                        deletedFolder.add(commandsForDelete[0]);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deletedFolder;
    }

    @Override
    public void uploadNewVersion(TargetSystemBeanWrapper targetSystemBean, String remoteDirName, File recreateJar, File pmpConfigPath) {
        try {
            SshClient sshClient = new SshClient(targetSystemBean.getHost(), targetSystemBean.getUser(), targetSystemBean.getPassword(), targetSystemBean.getPort(), !targetSystemBean.getOs().equals(OsEnum.AIX));
            sshClient.execCommand(new String[]{"mkdir " + targetSystemBean.getWorkingDir()});
            sshClient.execCommand(new String[]{"mkdir " + targetSystemBean.getRemoteWorkingDirFullPath()});
            sshClient.execCommand(new String[]{"mkdir " + targetSystemBean.getRemoteLibDirFullPath()});
            sshClient.execCommand(new String[]{"mkdir " + targetSystemBean.getRemoteConfDirFullPath()});
            sshClient.execCommand(new String[]{"mkdir " + targetSystemBean.getRemoteDbfDirFullPath()});
//            targetSystemBean.setJarPath(targetSystemBean.getRemoteWorkingDirFullPath() + recreateJar.getName());
//            targetSystemBean.setConfPath(targetSystemBean.getRemoteConfDirFullPath() + pmpConfigPath.getName());
            String configs = new String(Files.readAllBytes(pmpConfigPath.toPath()));

            Matcher matcher = Pattern.compile("^dbf.outputdir=.+$", Pattern.MULTILINE).matcher(configs);
            String modifiedConfigs = matcher.replaceFirst("dbf.outputdir=" + targetSystemBean.getRemoteDbfDirFullPath().replace("\\\\", "\\").replace("\\", "\\\\\\\\"));

            Matcher matcher2 = Pattern.compile("^runtime.pmp.recreate-executor.jarPath=.+$", Pattern.MULTILINE).matcher(modifiedConfigs);
            String modifiedConfigs2 = matcher2.replaceFirst("runtime.pmp.recreate-executor.jarPath=" + targetSystemBean.getJarPath().replace("\\\\", "\\").replace("\\", "\\\\\\\\"));

            Matcher matcher3 = Pattern.compile("^runtime.pmp.recreate-executor.configPath=.+$", Pattern.MULTILINE).matcher(modifiedConfigs2);
            String modifiedConfigs3 = matcher3.replaceFirst("runtime.pmp.recreate-executor.configPath=" + (targetSystemBean.getRemoteConfDirFullPath() + targetSystemBean.getS() + pmpConfigPath.getName()).replace("\\\\", "\\").replace("\\", "\\\\\\\\"));

            String modifiedConfigs4;
            Matcher matcher4 = Pattern.compile("^runtime.recreate.tmpdir=.+$", Pattern.MULTILINE).matcher(modifiedConfigs3);
            if (targetSystemBean.getCachePath() != null) {
                modifiedConfigs4 = matcher4.replaceFirst("runtime.recreate.tmpdir=" + targetSystemBean.getCachePath().replace("\\\\", "\\").replace("\\", "\\\\\\\\"));
            } else {
                modifiedConfigs4 = matcher4.replaceFirst("");
            }

            String s = FileSystems.getDefault().getSeparator();
            File tmpFileConfFile = new File(pmpConfigPath.getParent() + s + pmpConfigPath.getName() + "_tmp");
            if (tmpFileConfFile.exists()) {
                tmpFileConfFile.delete();
            }
            Files.write(tmpFileConfFile.toPath(), modifiedConfigs4.getBytes(), StandardOpenOption.CREATE_NEW);
            sshClient.scpTo(targetSystemBean.getJarPath(), recreateJar.getAbsolutePath());
            sshClient.scpTo(targetSystemBean.getConfPath(), tmpFileConfFile.getAbsolutePath());
            tmpFileConfFile.delete();
            File libDir = new File(recreateJar.getParentFile().getAbsolutePath() + "/lib");
            for (File libFile : libDir.listFiles()) {
                sshClient.scpTo(targetSystemBean.getRemoteLibDirFullPath() + libFile.getName(), libFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean runProcessInCmd(String[] commands) throws InterruptedException, IOException {
        return runProcess(commands, false, true).isResult();
    }

    @Override
    public RunProcessResultBean runProcessInBackgroung(String[] commands) throws InterruptedException, IOException {
        return runProcess(commands, true, false);
    }

    @Override
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

    public void killAllProcesses(TargetSystemBeanWrapper targetSystemBean) {
        List<OsProcessBean> processList = getProcessList(targetSystemBean);
        Optional<TargetSystemBeanWrapper> targetSystemBeanObj = Optional.ofNullable(targetSystemBean);
        OsEnum osType = targetSystemBeanObj.map(tt -> tt.getOs()).orElse(isWindowsOS ? OsEnum.WINDOWS : OsEnum.LINUX);
        Function<TargetSystemBeanWrapper, List<String>> executeProcessFunction = targetSystemBean == null ? this::runProcessInBackgroungAndGetResponseDataOnly : this::runSshProcess;
        if (osType.equals(OsEnum.AIX)) {
            for (OsProcessBean processBean : processList) {
                targetSystemBean.setCommands(new String[]{"kill -9 " + processBean.getProcessId().toString()});
                executeProcessFunction.apply(targetSystemBean);
            }
        }
    }
}
