package ru.ibs.pmp.medicalcaserepairer.bean;

/**
 * @author NAnishhenko
 */
public class ParcelInvoiceDiagnosisQueryBean {

    private final Long statisticsId;
    private final Long revisionId;
    private final Long nullInvoicesCountOfparcelS;
    private final Long nullInvoicesCountOfparcelSXX;

    public ParcelInvoiceDiagnosisQueryBean(Long statisticsId, Long revisionId, Long nullInvoicesCountOfparcelS, Long nullInvoicesCountOfparcelSXX) {
        this.statisticsId = statisticsId;
        this.revisionId = revisionId;
        this.nullInvoicesCountOfparcelS = nullInvoicesCountOfparcelS;
        this.nullInvoicesCountOfparcelSXX = nullInvoicesCountOfparcelSXX;
    }

    public Long getStatisticsId() {
        return statisticsId;
    }

    public Long getRevisionId() {
        return revisionId;
    }

    public Long getNullInvoicesCountOfparcelS() {
        return nullInvoicesCountOfparcelS;
    }

    public Long getNullInvoicesCountOfparcelSXX() {
        return nullInvoicesCountOfparcelSXX;
    }

}
