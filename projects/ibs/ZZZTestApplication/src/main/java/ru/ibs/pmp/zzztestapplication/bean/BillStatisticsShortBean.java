package ru.ibs.pmp.zzztestapplication.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author NAnishhenko
 */
public class BillStatisticsShortBean {

    private final Long id;
    private final Date created;
    private final String parameters;
    private List<String> operationDescriptionList = new ArrayList<>();

    public BillStatisticsShortBean(Object[] objectArray) {
        this.id = (Long) objectArray[0];
        this.created = (Date) objectArray[1];
        this.parameters = (String) objectArray[2];
        this.operationDescriptionList.add((String) objectArray[3]);
    }

    public Long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public String getParameters() {
        return parameters;
    }

    public List<String> getOperationDescriptionList() {
        return operationDescriptionList;
    }
}
