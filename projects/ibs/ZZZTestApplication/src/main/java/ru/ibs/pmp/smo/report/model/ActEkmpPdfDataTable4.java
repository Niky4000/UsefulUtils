package ru.ibs.pmp.smo.report.model;

/**
 *
 * @author me
 */
public class ActEkmpPdfDataTable4 {

//    ACT_ID NUMBER,
//	private String actId;
//    RN VARCHAR2(200),
    private String rn;
//    FIL VARCHAR2(200),
    private String fil;
//    C_I VARCHAR2(200),
    private String ci;
//    OSN VARCHAR2(200),
    private String osn;
//    WITHDRAWAL_PERCENT VARCHAR2(200),
    private String withdrawalPercent;
//    WITHDRAWAL_SUM VARCHAR2(200),
    private String withdrawalSum;
//    FINE_SUM VARCHAR2(200)
    private String fineSum;

    public String getRn() {
        return rn;
    }

    public void setRn(String rn) {
        this.rn = rn;
    }

    public String getFil() {
        return fil;
    }

    public void setFil(String fil) {
        this.fil = fil;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getOsn() {
        return osn;
    }

    public void setOsn(String osn) {
        this.osn = osn;
    }

    public String getWithdrawalPercent() {
        return withdrawalPercent;
    }

    public void setWithdrawalPercent(String withdrawalPercent) {
        this.withdrawalPercent = withdrawalPercent;
    }

    public String getWithdrawalSum() {
        return withdrawalSum;
    }

    public void setWithdrawalSum(String withdrawalSum) {
        this.withdrawalSum = withdrawalSum;
    }

    public String getFineSum() {
        return fineSum;
    }

    public void setFineSum(String fineSum) {
        this.fineSum = fineSum;
    }
}
