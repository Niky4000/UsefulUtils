package ru.ibs.pmp.parcel.update.bean;

/**
 * @author NAnishhenko
 */
public class MedicalCaseBean {

    private final Long medicalCaseId;
    private final String medicalCaseType;

    public MedicalCaseBean(Long medicalCaseId, String medicalCaseType) {
        this.medicalCaseId = medicalCaseId;
        this.medicalCaseType = medicalCaseType;
    }

    public Long getMedicalCaseId() {
        return medicalCaseId;
    }

    public String getMedicalCaseType() {
        return medicalCaseType;
    }

}
