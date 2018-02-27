package ru.ibs.pmp.parcel.update.bean;

/**
 * @author NAnishhenko
 */
public class InvoiceBean {

    private final Long invoiceId;
    private final Long caseId;
    private final Long invoiceSum;

    public InvoiceBean(Long invoiceId, Long caseId, Long invoiceSum) {
        this.invoiceId = invoiceId;
        this.caseId = caseId;
        this.invoiceSum = invoiceSum;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public Long getInvoiceSum() {
        return invoiceSum;
    }

}
