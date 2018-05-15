package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.Date;
import java.util.Objects;
import ru.ibs.pmp.api.model.Bill;

/**
 * @author NAnishhenko
 */
public class StuckBean {

    private final String lpuId;
    private final Date period;
    private final String type;
    private final Bill.BillStatus billStatus;

    public StuckBean(String lpuId, Date period, String type, Bill.BillStatus billStatus) {
        this.lpuId = lpuId;
        this.period = period;
        this.type = type;
        this.billStatus = billStatus;
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

    public Bill.BillStatus getBillStatus() {
        return billStatus;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.lpuId);
        hash = 43 * hash + Objects.hashCode(this.period);
        hash = 43 * hash + Objects.hashCode(this.type);
        hash = 43 * hash + Objects.hashCode(this.billStatus);
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
