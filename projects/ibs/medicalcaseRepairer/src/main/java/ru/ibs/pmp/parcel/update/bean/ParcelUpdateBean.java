package ru.ibs.pmp.parcel.update.bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author NAnishhenko
 */
public class ParcelUpdateBean {

    private final Long id;
    private final Long versionNumber;
    private final Long invoiceId;
    private final Date period;
    private BigDecimal invoiceSum;
    private Long medicalCaseId;
    private String medicalCaseType;

    public ParcelUpdateBean(Long id, Long invoiceId, Long versionNumber, Date period) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.versionNumber = versionNumber;
        this.period = period;
    }

    public Long getId() {
        return id;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public Date getPeriod() {
        return period;
    }

    public BigDecimal getInvoiceSum() {
        return invoiceSum;
    }

    public void setInvoiceSum(BigDecimal invoiceSum) {
        this.invoiceSum = invoiceSum;
    }

    public Long getMedicalCaseId() {
        return medicalCaseId;
    }

    public void setMedicalCaseId(Long medicalCaseId) {
        this.medicalCaseId = medicalCaseId;
    }

    public String getMedicalCaseType() {
        return medicalCaseType;
    }

    public void setMedicalCaseType(String medicalCaseType) {
        this.medicalCaseType = medicalCaseType;
    }

}
