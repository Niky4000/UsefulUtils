package ru.ibs.pmp.module.pmp.bill.recreate.exec.bean;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author NAnishhenko
 */
public class JammedBean {

    private final int lpuId;
    private final Date period;

    public JammedBean(int lpuId, Date period) {
        this.lpuId = lpuId;
        this.period = period;
    }

    public int getLpuId() {
        return lpuId;
    }

    public Date getPeriod() {
        return period;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.lpuId;
        hash = 53 * hash + Objects.hashCode(this.period);
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
        final JammedBean other = (JammedBean) obj;
        if (this.lpuId != other.lpuId) {
            return false;
        }
        if (!Objects.equals(this.period, other.period)) {
            return false;
        }
        return true;
    }

}
