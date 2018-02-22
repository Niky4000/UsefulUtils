/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.Date;

/**
 *
 * @author NAnishhenko
 */
public class ProcessBean {

    String operationMode;
    String moId;
    String periodYear;
    String periodMonth;
    String type;
    Date period;
    String parameters;
    String userId;
    TargetSystemBeanWrapper targetSystemBean;

    public String getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public String getMoId() {
        return moId;
    }

    public void setMoId(String moId) {
        this.moId = moId;
    }

    public String getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(String periodYear) {
        this.periodYear = periodYear;
    }

    public String getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(String periodMonth) {
        this.periodMonth = periodMonth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TargetSystemBeanWrapper getTargetSystemBean() {
        return targetSystemBean;
    }

    public void setTargetSystemBean(TargetSystemBeanWrapper targetSystemBean) {
        this.targetSystemBean = targetSystemBean;
    }

}
