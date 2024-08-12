package ru.ibs.pmp.zzztestapplication.bean;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Этот объект содержит максимальное число процессов для ФЛК на instance БД
 * согласно метрике Ивана и предназначен для помещения в приоритетную очередь!
 * Это позволит равномерно раскидывать процессы ФЛК по instance'ам БД!
 *
 * @author pump
 */
public final class InstanceQueueBean implements Comparable<InstanceQueueBean> {

    private final String instanceName;
    private AtomicInteger processesCount;

    public InstanceQueueBean(String instanceName, AtomicInteger processesCount) {
        this.instanceName = instanceName;
        this.processesCount = processesCount;
    }

    Integer getProcessesCount() {
        return processesCount.get();
    }

    public void setProcessesCount(Integer processesCount) {
        this.processesCount.set(processesCount);
    }

    public String getRealInstanceName() {
        return instanceName;
    }

    public String getInstanceName() {
        int getAndDecrement = processesCount.getAndDecrement();
        if (getAndDecrement > 0) {
            System.out.println(instanceName + " was gotten!" + getText(getAndDecrement));
            System.out.println("InstanceQueueBean: " + instanceName + " " + getAndDecrement + "!");
            return instanceName;
        } else {
            if (getAndDecrement < 0) {
                processesCount.set(0);
            }
            System.out.println("Maximum processes count is reached according to dynamic algorithm for " + instanceName + "!");
            return null;
        }
    }

    private String getText(int decrementAndGet) {
        if (decrementAndGet > 1) {
            return " " + decrementAndGet + " of such metrics remain!";
        } else if (decrementAndGet == 1) {
            return " Only one of such metric remains!";
        } else {
            return " No one of such metric remains!";
        }
    }

    @Override
    public int compareTo(InstanceQueueBean o) {
        int processCount_=-this.getProcessesCount().compareTo(o.getProcessesCount());
        if(processCount_==0){
            return this.getInstanceName().compareTo(o.getInstanceName());
        }
        else{
            return processCount_;
        }
    }
}
