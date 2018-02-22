package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.Date;

/**
 * @author NAnishhenko
 */
public class QueueLpuBean {

    private final Date period;
    private final String type;
    private final String lpuId;

    public QueueLpuBean(Date period, String type, String lpuId) {
        this.period = period;
        this.type = type;
        this.lpuId = lpuId;
    }

    public Date getPeriod() {
        return period;
    }

    public String getType() {
        return type;
    }

    public String getLpuId() {
        return lpuId;
    }

}
