package ru.ibs.pmp.module.recreate.exec.bean;

/**
 * @author NAnishhenko
 */
public class TargetSystemBean {

    private final int DEFAULT_QUOTA = 5;

    private final OsEnum os;
    private final int quota;
    private final String host;
    private final String user;
    private final String password;
    private final Integer port;
    private final String workingDir;
    private String jarPath;
    private String confPath;
    private String[] commands;
    private String remoteWorkingDirFullPath;
    private String remoteLibDirFullPath;
    private String remoteConfDirFullPath;

    public TargetSystemBean(OsEnum os, int quota, String host, String user, String password, Integer port, String workingDir) {
        this.os = os;
        this.quota = quota;
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.workingDir = workingDir;
    }

    public TargetSystemBean(String[] commands) {
        this.os = null;
        this.quota = DEFAULT_QUOTA;
        this.host = null;
        this.user = null;
        this.password = null;
        this.port = null;
        this.workingDir = null;
        this.commands = commands;
    }

    public OsEnum getOs() {
        return os;
    }

    public int getQuota() {
        return quota;
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

    public Integer getPort() {
        return port;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getConfPath() {
        return confPath;
    }

    public void setConfPath(String confPath) {
        this.confPath = confPath;
    }

    public String getRemoteWorkingDirFullPath() {
        return remoteWorkingDirFullPath;
    }

    public void setRemoteWorkingDirFullPath(String remoteWorkingDirFullPath) {
        this.remoteWorkingDirFullPath = remoteWorkingDirFullPath;
    }

    public String getRemoteLibDirFullPath() {
        return remoteLibDirFullPath;
    }

    public void setRemoteLibDirFullPath(String remoteLibDirFullPath) {
        this.remoteLibDirFullPath = remoteLibDirFullPath;
    }

    public String getRemoteConfDirFullPath() {
        return remoteConfDirFullPath;
    }

    public void setRemoteConfDirFullPath(String remoteConfDirFullPath) {
        this.remoteConfDirFullPath = remoteConfDirFullPath;
    }

    public String[] getCommands() {
        return commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    public String getS() {
        if (os.equals(OsEnum.LINUX)) {
            return "/";
        } else if (os.equals(OsEnum.WINDOWS)) {
            return "\\";
        } else {
            return null;
        }
    }
}
