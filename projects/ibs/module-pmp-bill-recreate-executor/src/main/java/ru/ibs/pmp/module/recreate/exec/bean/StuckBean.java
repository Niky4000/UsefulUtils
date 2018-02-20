package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.Date;
import java.util.Objects;

/**
 * @author NAnishhenko
 */
public class StuckBean {

    private final String lpuId;
    private final Date period;
    private final String type;

    public StuckBean(String lpuId, Date period, String type) {
        this.lpuId = lpuId;
        this.period = period;
        this.type = type;
    }

    public String getLpuId() {
        return lpuId;
    }

    public Date getPeriod() {
        return period;
    }

    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.lpuId);
        hash = 53 * hash + Objects.hashCode(this.period);
        hash = 53 * hash + Objects.hashCode(this.type);
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
        final StuckBean other = (StuckBean) obj;
        if (!Objects.equals(this.lpuId, other.lpuId)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.period, other.period)) {
            return false;
        }
        return true;
    }

}
