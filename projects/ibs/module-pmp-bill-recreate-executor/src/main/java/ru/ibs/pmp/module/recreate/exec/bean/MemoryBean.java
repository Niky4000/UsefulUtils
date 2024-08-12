package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.Objects;
import ru.ibs.pmp.api.model.PmpSync;

/**
 * @author NAnishhenko
 */
public class MemoryBean implements Comparable<MemoryBean> {

    private final PmpSync pmpSync;
    private final Long possibleMemoryUsage;
    private final Long serviceCount;
    private TargetSystemBeanWrapper targetSystemBeanWrapper;

    public MemoryBean(PmpSync pmpSync, RamBean ramBean) {
        this.pmpSync = pmpSync;
        this.possibleMemoryUsage = ramBean.getRamUsage();
        this.serviceCount = ramBean.getServiceCount();
    }

    public PmpSync getPmpSync() {
        return pmpSync;
    }

    public Long getPossibleMemoryUsage() {
        return possibleMemoryUsage;
    }

    public Long getServiceCount() {
        return serviceCount;
    }

    public TargetSystemBeanWrapper getTargetSystemBeanWrapper() {
        return targetSystemBeanWrapper;
    }

    public void setTargetSystemBeanWrapper(TargetSystemBeanWrapper targetSystemBeanWrapper) {
        this.targetSystemBeanWrapper = targetSystemBeanWrapper;
    }

    @Override
    public int compareTo(MemoryBean o) {
        return this.possibleMemoryUsage < o.getPossibleMemoryUsage() ? 1 : -1;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.pmpSync);
        hash = 97 * hash + Objects.hashCode(this.possibleMemoryUsage);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MemoryBean other = (MemoryBean) obj;
        if (!Objects.equals(this.pmpSync, other.pmpSync)) {
            return false;
        }
        if (!Objects.equals(this.possibleMemoryUsage, other.possibleMemoryUsage)) {
            return false;
        }
        return true;
    }

}
