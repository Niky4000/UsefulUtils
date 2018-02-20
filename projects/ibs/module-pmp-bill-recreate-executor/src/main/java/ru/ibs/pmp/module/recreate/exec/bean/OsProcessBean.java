package ru.ibs.pmp.module.recreate.exec.bean;

/**
 * @author NAnishhenko
 */
public class OsProcessBean {

    private final String processName;
    private final Integer processId;
    private final String processCmd;

    public OsProcessBean(String processName, Integer processId, String processCmd) {
        this.processName = processName;
        this.processId = processId;
        this.processCmd = processCmd;
    }

    public String getProcessName() {
        return processName;
    }

    public Integer getProcessId() {
        return processId;
    }

    public String getProcessCmd() {
        return processCmd;
    }

}
