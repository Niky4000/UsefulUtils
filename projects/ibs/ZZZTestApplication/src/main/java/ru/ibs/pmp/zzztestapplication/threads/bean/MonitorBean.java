package ru.ibs.pmp.zzztestapplication.threads.bean;

/**
 * @author NAnishhenko
 */
public class MonitorBean {

    private final MonitorBeanEnum monitorBeanEnum;
    private final String message;

    public MonitorBean(MonitorBeanEnum monitorBeanEnum, String message) {
        this.monitorBeanEnum = monitorBeanEnum;
        this.message = message;
    }

    public enum MonitorBeanEnum {
        COUNT, CLOSE
    }

    public MonitorBeanEnum getMonitorBeanEnum() {
        return monitorBeanEnum;
    }

    public String getMessage() {
        return message;
    }
}
