package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.function.Function;

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
    private final Long minMemoryForRecreate;
    private final Long minMemoryForSend;
    private final String jarPath;
    private final String confPath;
    private final String remoteWorkingDirFullPath;
    private final String remoteLibDirFullPath;
    private final String remoteConfDirFullPath;
    private final String remoteDbfDirFullPath;
    private final String remoteDirName;

    protected TargetSystemBean() {
        this.os = null;
        this.quota = 0;
        this.host = null;
        this.user = null;
        this.password = null;
        this.port = null;
        this.workingDir = null;
        this.minMemoryForRecreate = null;
        this.minMemoryForSend = null;
        this.jarPath = null;
        this.confPath = null;
        this.remoteWorkingDirFullPath = null;
        this.remoteLibDirFullPath = null;
        this.remoteConfDirFullPath = null;
        this.remoteDbfDirFullPath = null;
        this.remoteDirName = null;
    }

    public TargetSystemBean(OsEnum os, int quota, String host, String user, String password, Integer port, String workingDir, Long minMemoryForRecreate, Long minMemoryForSend,
            String jarPath, String confPath, String remoteWorkingDirFullPath, String remoteLibDirFullPath, String remoteConfDirFullPath, String remoteDbfDirFullPath, String remoteDirName) {
        this.os = os;
        this.quota = quota;
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.workingDir = workingDir;
        this.minMemoryForRecreate = minMemoryForRecreate;
        this.minMemoryForSend = minMemoryForSend;
        this.jarPath = jarPath;
        this.confPath = confPath;
        this.remoteWorkingDirFullPath = remoteWorkingDirFullPath;
        this.remoteLibDirFullPath = remoteLibDirFullPath;
        this.remoteConfDirFullPath = remoteConfDirFullPath;
        this.remoteDbfDirFullPath = remoteDbfDirFullPath;
        this.remoteDirName = remoteDirName;
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

    public Long getMinMemoryForRecreate() {
        return minMemoryForRecreate;
    }

    public Long getMinMemoryForSend() {
        return minMemoryForSend;
    }

    public String getJarPath() {
        return jarPath;
    }

    public String getConfPath() {
        return confPath;
    }

    public String getRemoteWorkingDirFullPath() {
        return remoteWorkingDirFullPath;
    }

    public String getRemoteLibDirFullPath() {
        return remoteLibDirFullPath;
    }

    public String getRemoteConfDirFullPath() {
        return remoteConfDirFullPath;
    }

    public String getRemoteDbfDirFullPath() {
        return remoteDbfDirFullPath;
    }

    public String getRemoteDirName() {
        return remoteDirName;
    }

    public static Function<OsEnum, String> s = os -> {
        if (os.equals(OsEnum.LINUX)) {
            return "/";
        } else if (os.equals(OsEnum.WINDOWS)) {
            return "\\";
        } else {
            return null;
        }
    };

    public String getS() {
        return s.apply(os);
    }
}
