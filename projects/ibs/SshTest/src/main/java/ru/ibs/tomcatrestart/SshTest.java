package ru.ibs.tomcatrestart;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author NAnishhenko
 */
public class SshTest {

    private static final String CONFIG_FILE_NAME = "properties.txt";
    private static final String CONFIG_REMOTE_FILE_NAME = "properties_remote.txt";
    private String s = File.separator;
    private boolean targetSystemIsLinux = false;

    private static final int TIME_TO_WAIT = 10000;

    final Integer port;
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

    public SshTest(String configFileName) throws Exception {
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
        port = Integer.valueOf(getProperties().getProperty("port"));
    }

    public static void main(String[] args) throws Exception {
        new SshTest("D:\\GIT\\UsefulUtils\\projects\\ibs\\SshTest\\properties2.txt").testSsh();
        System.exit(0);
    }

    public void testSsh() throws Exception {
        System.out.println("Before SshClient! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("------------------------------");
        SshClient sshClient = new SshClient(getHost(), getUser(), getPassword(), port);
        sshClient.scpTo("D:\\tmp\\zzzz\\debug_stage1CompositeObject_5288_2017-12.bin", "D:\\tmp\\debug_stage1CompositeObject_5288_2017-12");
        sshClient.execCommand(new String[]{"java -Xmx8G -Dpmp.config.path=D:\\GIT\\rmis\\etc\\recreate_111_test\\runtime.properties -jar D:\\tmp\\recreate\\module-pmp-bill-recreate.jar -mf 4889 2017-11"});
//        Scp.createSshClient(getHost(), getUser(), getPassword(), port, new String[]{"java -Xmx4G -Dpmp.config.path=/home/mls/tomcat/modules/conf/module-pmp-bill-recreate-executor-war/runtime-recreate.properties -jar /home/mls/tomcat/webapps/module-pmp-bill-recreate.jar -mf 1863 2017-12"});
        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("------------------------------");
        System.out.println("After SshClient! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
