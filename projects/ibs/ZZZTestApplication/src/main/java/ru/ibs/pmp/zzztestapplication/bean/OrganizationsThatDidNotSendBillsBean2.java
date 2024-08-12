package ru.ibs.pmp.zzztestapplication.bean;

/**
 * @author NAnishhenko
 */
public class OrganizationsThatDidNotSendBillsBean2 {

    private final Long lpuId;
    private final Long billId;
    private final String ogrn;

    public OrganizationsThatDidNotSendBillsBean2(Long lpuId, Long billId, String ogrn) {
        this.lpuId = lpuId;
        this.billId = billId;
        this.ogrn = ogrn;
    }

    public Long getLpuId() {
        return lpuId;
    }

    public Long getBillId() {
        return billId;
    }

    public String getOgrn() {
        return ogrn;
    }

}
