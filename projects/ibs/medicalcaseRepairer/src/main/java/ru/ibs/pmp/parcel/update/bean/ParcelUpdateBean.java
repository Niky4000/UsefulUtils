package ru.ibs.pmp.parcel.update.bean;

import java.math.BigDecimal;

/**
 * @author NAnishhenko
 */
public class ParcelUpdateBean {

    private final Long id;
    private final Long versionNumber;
    private final Long invoiceId;
    private BigDecimal invoiceSum;
    private Long medicalCaseId;
    private String medicalCaseType;

    public ParcelUpdateBean(Long id,Long invoiceId, Long versionNumber) {
        this.id = id;
        this.invoiceId=invoiceId;
        this.versionNumber = versionNumber;
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
