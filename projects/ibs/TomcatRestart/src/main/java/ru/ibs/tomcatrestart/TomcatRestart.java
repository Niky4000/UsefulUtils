package ru.ibs.tomcatrestart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author NAnishhenko
 */
public class TomcatRestart {

    private static final String CONFIG_FILE_NAME = "properties.txt";
    private static final String CONFIG_REMOTE_FILE_NAME = "properties_remote.txt";
    private String s = File.separator;
    private boolean targetSystemIsLinux = false;

    private static final int TIME_TO_WAIT = 10000;

    final String host;
    final String user;
    final String password;
    final String zipArchPath;
    final String targetUnpackDir;
    final String tomcatModulesDir;
    final String modules;
    final String pmpClientPath;
    final String sshPmpClientPath;
    final String[] deleteDirPath;
    final String[] deleteFilesPath;
    final String[] deleteDirsPath;
    final Properties properties;

    boolean forceCopy = false;

    public TomcatRestart(String configFileName) throws Exception {
        properties = new Properties();
        properties.load(new FileInputStream(new File(configFileName)));
        host = properties.getProperty("host");
        user = properties.getProperty("user");
        password = properties.getProperty("password");
        zipArchPath = properties.getProperty("zipArchPath");
        targetUnpackDir = properties.getProperty("targetUnpackDir");
        tomcatModulesDir = properties.getProperty("tomcatModulesDir");
        modules = properties.getProperty("modules");
        pmpClientPath = properties.getProperty("pmpClientPath");
        sshPmpClientPath = properties.getProperty("sshPmpClientPath");
        deleteDirPath = splitString(properties.getProperty("deleteDirPath"));
        deleteFilesPath = splitString(properties.getProperty("deleteFilesPath"));
        deleteDirsPath = splitString(properties.getProperty("deleteDirsPath"));
    }

    public static void main(String[] args) throws Exception {

        List<String> argsList = new ArrayList<>(args.length);
        Collections.addAll(argsList, args);

        TomcatRestart tomcatRestart;
        if (argsList.contains("-rl")) {
            tomcatRestart = new TomcatRestartRemote(getConfigFileName(argsList, CONFIG_REMOTE_FILE_NAME));
            tomcatRestart.setS("/");
            tomcatRestart.setTargetSystemIsLinux(true);
        } else if (argsList.contains("-r")) {
            tomcatRestart = new TomcatRestartRemote(getConfigFileName(argsList, CONFIG_REMOTE_FILE_NAME));
        } else {
            tomcatRestart = new TomcatRestart(getConfigFileName(argsList, CONFIG_FILE_NAME));
        }

        if (argsList.contains("-i")) {
            tomcatRestart.setForceCopy(true);
        }
        List<String> particularModules = null;
        if (argsList.contains("-modules")) {
            particularModules = getParticularModules(argsList);
        }

        String host = tomcatRestart.getHost();
        String user = tomcatRestart.getUser();
        String password = tomcatRestart.getPassword();
        String zipArchPath = tomcatRestart.getZipArchPath();
        String targetUnpackDir = tomcatRestart.getTargetUnpackDir();
        String tomcatModulesDir = tomcatRestart.getTomcatModulesDir();
        String modules = tomcatRestart.getModules();
        String pmpClientPath = tomcatRestart.getPmpClientPath();
        String sshPmpClientPath = tomcatRestart.getSshPmpClientPath();
        String[] deleteDirPath = tomcatRestart.getDeleteDirPath();
        String[] deleteFilesPath = tomcatRestart.getDeleteFilesPath();
        String[] deleteDirsPath = tomcatRestart.getDeleteDirsPath();

        tomcatRestart.initActions();

        for (String path : deleteDirPath) {
            tomcatRestart.deleteDir(path);
        }
        for (String path : deleteFilesPath) {
            tomcatRestart.deleteFiles(path);
        }
        for (String path : deleteDirsPath) {
            tomcatRestart.deleteDirs(path);
        }
        if (!argsList.contains("-clean")) {
            tomcatRestart.getPmpClientFromServer(tomcatModulesDir, pmpClientPath, sshPmpClientPath, host, user, password);
            if (particularModules == null) {
                tomcatRestart.copyModulesFromArch(tomcatModulesDir, targetUnpackDir, modules, zipArchPath);
            } else {
                particularModules.forEach(module -> {
                    try {
                        tomcatRestart.copyOneFile(tomcatModulesDir, new File(module));
                    } catch (Exception e) {
                        throw new RuntimeException("Copying one file " + module + " in modules mode caused Fatal Error!", e);
                    }
                });
            }
            tomcatRestart.postActions();
        }
        System.out.println("TomcatRestart finished!");
        Thread.sleep(TIME_TO_WAIT);
        System.exit(0);
    }

    private static List<String> getParticularModules(List<String> argsList) {
        int indexOf = argsList.indexOf("-modules");
        List<String> particularModules = new ArrayList<>();
        for (int i = indexOf + 1; i < argsList.size(); i++) {
            if (argsList.get(i).startsWith("-")) {
                break;
            }
            if (new File(argsList.get(i)).exists()) {
                particularModules.add(argsList.get(i));
            }
        }
        if (particularModules.isEmpty()) {
            throw new RuntimeException("There is no particular modules! This is Fatal Error!");
        }
        return particularModules;
    }

    private static String getConfigFileName(List<String> argsList, String defaultConfig) {
        if (argsList.contains("-conf")) {
            return argsList.get(argsList.indexOf("-conf") + 1);
        } else {
            return defaultConfig;
        }
    }

    protected String[] splitString(String str) {
        if (str != null) {
            if (str.contains(",")) {
                return str.split(",");
            } else {
                return new String[]{str};
            }
        } else {
            return new String[0];
        }
    }

    protected void initActions() {
    }

    protected void postActions() {
    }

    protected void copyModulesFromArch(String tomcatModulesDir, String targetUnpackDir, String modules, String zipArchPath) throws Exception {
        String targetModulesDir = targetUnpackDir + modules;
        File targetDir = new File(targetUnpackDir);
        if (!targetDir.exists()) {
            UnZip.unZipIt(zipArchPath, targetDir.getAbsolutePath());
            copyFiles(targetModulesDir, tomcatModulesDir);
        } else if (targetDir.exists() && isForceCopy()) {
            copyFiles(targetModulesDir, tomcatModulesDir);
        } else {
            System.out.println("Modules already had been updated!");
        }
    }

    protected void copyFiles(String targetModulesDir, String tomcatModulesDir) throws Exception {
        for (File file : new File(targetModulesDir).listFiles()) {
            if (!file.isDirectory()) {
                copyOneFile(tomcatModulesDir, file);
            } else {
                String targetDir = tomcatModulesDir + s + file.getName();
                recreateTargetDir(targetDir);
                copyFiles(file.getAbsolutePath(), targetDir);
            }
        }
        System.out.println("Modules updated!");
    }

    protected void recreateTargetDir(String targetDir) throws Exception {
        File dir = new File(targetDir);
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdirs();
    }

    protected void copyOneFile(String tomcatModulesDir, File file) throws IOException {
        File targetFile = new File(tomcatModulesDir + s + file.getName());
        Files.copy(file.toPath(), targetFile.toPath(), REPLACE_EXISTING);
        System.out.println(file.getAbsolutePath() + " --> copied to --> " + targetFile.getAbsolutePath());
    }

    protected void getPmpClientFromServer(String tomcatModulesDir, String pmpClientPath, String sshPmpClientPath, String host, String user, String password) {
        File pmpClient = new File(tomcatModulesDir + pmpClientPath);
        if (pmpClient.exists()) {
            pmpClient.delete();
        }
        Scp.scpFrom(host, 22, user, password, sshPmpClientPath, pmpClient.getPath());
        if (pmpClient.exists()) {
            System.out.println("PmpClient updated successfuly!");
        } else {
            System.out.println("PmpClient update failed!");
        }
    }

    protected void deleteDir(String dirStr) throws IOException {
        File dir = new File(dirStr);
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
            System.out.println("Dir Deleted: " + dir.getAbsolutePath());
        }
    }

    protected void deleteDirs(String dirStr) throws IOException {
        File dir = new File(dirStr);
        if (dir.exists()) {
            if (dir.listFiles().length > 0) {
                for (File subdir : dir.listFiles()) {
                    deleteDir(subdir.getAbsolutePath());
                }
                System.out.println("Dirs Deleted: " + dir.getAbsolutePath());
            }
        }
    }

    protected void deleteFiles(String dirStr) {
        File dir = new File(dirStr);
        if (dir.exists()) {
            if (dir.listFiles().length > 0) {
                for (File file : dir.listFiles()) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
                System.out.println("Files Deleted: " + dir.getAbsolutePath());
            }
        }
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getZipArchPath() {
        return zipArchPath;
    }

    public String getTargetUnpackDir() {
        return targetUnpackDir;
    }

    public String getTomcatModulesDir() {
        return tomcatModulesDir;
    }

    public String getModules() {
        return modules;
    }

    public String getPmpClientPath() {
        return pmpClientPath;
    }

    public String getSshPmpClientPath() {
        return sshPmpClientPath;
    }

    public String[] getDeleteDirPath() {
        return deleteDirPath;
    }

    public String[] getDeleteFilesPath() {
        return deleteFilesPath;
    }

    public String[] getDeleteDirsPath() {
        return deleteDirsPath;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public boolean isTargetSystemIsLinux() {
        return targetSystemIsLinux;
    }

    public void setTargetSystemIsLinux(boolean targetSystemIsLinux) {
        this.targetSystemIsLinux = targetSystemIsLinux;
    }

    public boolean isForceCopy() {
        return forceCopy;
    }

    public void setForceCopy(boolean forceCopy) {
        this.forceCopy = forceCopy;
    }

    public Properties getProperties() {
        return properties;
    }

}
