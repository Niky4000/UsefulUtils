package ru.ibs.pmp.zzztestapplication.bean;

import java.util.Date;

/**
 * @author NAnishhenko
 */
public class OrganizationsThatDidNotSendBillsBean {

    private final Long lpuId;
    private final Date period;
    private final String parameters;

    public OrganizationsThatDidNotSendBillsBean(Long lpuId, Date period, String parameters) {
        this.lpuId = lpuId;
        this.period = period;
        this.parameters = parameters;
    }

    public Long getLpuId() {
        return lpuId;
    }

    public Date getPeriod() {
        return period;
    }

    public String getParameters() {
        return parameters;
    }

}
