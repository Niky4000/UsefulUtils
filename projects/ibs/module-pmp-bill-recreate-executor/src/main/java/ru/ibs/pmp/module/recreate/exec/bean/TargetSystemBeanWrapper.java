package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.Set;

/**
 * @author NAnishhenko
 */
public class TargetSystemBeanWrapper extends TargetSystemBean {

    private String[] commands;
    private Long freeMemory;
    private Long reservedMemory;
    private Set<OsProcessBean> processSet;
    private int totalAmountOfProcesses = 0;

    public TargetSystemBeanWrapper(TargetSystemBean targetSystemBean) {
        super(targetSystemBean.getOs(), targetSystemBean.getQuota(), targetSystemBean.getHost(), targetSystemBean.getUser(), targetSystemBean.getPassword(),
                targetSystemBean.getPort(), targetSystemBean.getWorkingDir(), targetSystemBean.getMinMemoryForRecreate(), targetSystemBean.getMinMemoryForSend(),
                targetSystemBean.getJavaPath(), targetSystemBean.getCachePath(), targetSystemBean.getJarPath(), targetSystemBean.getConfPath(),
                targetSystemBean.getRemoteWorkingDirFullPath(), targetSystemBean.getRemoteLibDirFullPath(), targetSystemBean.getRemoteConfDirFullPath(),
                targetSystemBean.getRemoteDbfDirFullPath(), targetSystemBean.getRemoteDirName());
    }

    public TargetSystemBeanWrapper(String[] commands) {
        super();
        this.commands = commands;
    }

    public String[] getCommands() {
        return commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    public Long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Long getReservedMemory() {
        return reservedMemory;
    }

    public void setReservedMemory(Long reservedMemory) {
        this.reservedMemory = reservedMemory;
    }

    public Set<OsProcessBean> getProcessSet() {
        return processSet;
    }

    public void setProcessSet(Set<OsProcessBean> processSet) {
        this.processSet = processSet;
        this.totalAmountOfProcesses = processSet.size();
    }

    // Добавить один запланированный процесс к выполнению
    public void addPlanedProcessExecution() {
        totalAmountOfProcesses++;
    }

    public int getTotalAmountOfProcesses() {
        return totalAmountOfProcesses;
    }

}
