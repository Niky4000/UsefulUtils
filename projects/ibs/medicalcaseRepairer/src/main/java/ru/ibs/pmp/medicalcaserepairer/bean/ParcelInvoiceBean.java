package ru.ibs.pmp.medicalcaserepairer.bean;

/**
 * @author NAnishhenko
 */
public class ParcelInvoiceBean {

    private final Long parcelsId;
    private final Long invoiceId;

    public ParcelInvoiceBean(Long parcelsId, Long invoiceId) {
        this.parcelsId = parcelsId;
        this.invoiceId = invoiceId;
    }

    public Long getParcelsId() {
        return parcelsId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

}
