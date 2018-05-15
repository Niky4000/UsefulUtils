package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.Date;

/**
 * @author NAnishhenko
 */
public class RamBean {

    private final Long ramUsage;
    private final Date created;
    private final Long serviceCount;

    public RamBean(Long ramUsage, Date created, Long serviceCount) {
        this.ramUsage = ramUsage;
        this.created = created;
        this.serviceCount = serviceCount;
    }

    public Long getRamUsage() {
        return ramUsage;
    }

    public Date getCreated() {
        return created;
    }

    public Long getServiceCount() {
        return serviceCount;
    }

}
