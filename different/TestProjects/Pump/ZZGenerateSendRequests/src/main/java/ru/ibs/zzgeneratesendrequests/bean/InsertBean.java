package ru.ibs.zzgeneratesendrequests.bean;

import java.sql.Date;
import java.util.Objects;

/**
 * @author NAnishhenko
 */
public class InsertBean {

    private final Long lpuId;
    private final Date period;
    private final String insertString;

    public InsertBean(Long lpuId, Date period, String insertString) {
        this.lpuId = lpuId;
        this.period = period;
        this.insertString = insertString;
    }

    public Long getLpuId() {
        return lpuId;
    }

    public Date getPeriod() {
        return period;
    }

    public String getInsertString() {
        return insertString;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.lpuId);
        hash = 47 * hash + Objects.hashCode(this.period);
        hash = 47 * hash + Objects.hashCode(this.insertString);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InsertBean other = (InsertBean) obj;
        if (!Objects.equals(this.insertString, other.insertString))
            return false;
        if (!Objects.equals(this.lpuId, other.lpuId))
            return false;
        if (!Objects.equals(this.period, other.period))
            return false;
        return true;
    }

}
