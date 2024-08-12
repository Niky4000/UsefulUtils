package ru.ibs.pmp.medicalcaserepairer.bean;

import java.math.BigDecimal;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.api.model.MedicalCaseAud;

/**
 * @author NAnishhenko
 */
public class MedicalCaseWrapper {

    private MedicalCase medicalCase;
    private MedicalCaseAud medicalCaseAud;
    private Long versionNumberOfMedicalCase;

    public MedicalCaseWrapper(MedicalCase medicalCase) {
        this.medicalCase = medicalCase;
    }

    public MedicalCaseWrapper(MedicalCaseAud medicalCaseAud) {
        this.medicalCaseAud = medicalCaseAud;
    }

    public MedicalCaseWrapper(Object[] objs) {
        MedicalCase medicalCase = new MedicalCase();
        Long medicalCaseId = ((Number) objs[0]).longValue();
        BigDecimal billFlkErrorAmount = objs[1] != null ? (BigDecimal) objs[1] : BigDecimal.ZERO;
        BigDecimal caseAmount = objs[2] != null ? (BigDecimal) objs[2] : BigDecimal.ZERO;
        Long versionNumber = objs[3] != null ? ((Number) objs[3]).longValue() : null;
        medicalCase.setId(medicalCaseId);
        medicalCase.setBillFlkErrorAmount(billFlkErrorAmount);
        medicalCase.setCaseAmount(caseAmount);
        this.versionNumberOfMedicalCase = versionNumber;
        this.medicalCase = medicalCase;
    }

    public Long getId() {
        if (medicalCase != null) {
            return medicalCase.getId();
        } else if (medicalCaseAud != null) {
            return medicalCaseAud.getPmpMedicalCaseAudPK().getId();
        }
        return null;
    }

    public Long getRev() {
        if (medicalCase != null) {
            return null;
        } else if (medicalCaseAud != null) {
            return medicalCaseAud.getPmpMedicalCaseAudPK().getRev();
        }
        return null;
    }

    public void setBillFlkErrorAmount(BigDecimal billFlkErrorAmount) {
        if (medicalCase != null) {
            medicalCase.setBillFlkErrorAmount(billFlkErrorAmount);
        } else if (medicalCaseAud != null) {
            medicalCaseAud.setBillFlkErrorAmount(billFlkErrorAmount);
        }
    }

    public BigDecimal getBillFlkErrorAmount() {
        if (medicalCase != null) {
            return medicalCase.getBillFlkErrorAmount();
        } else if (medicalCaseAud != null) {
            return medicalCaseAud.getBillFlkErrorAmount();
        }
        return null;
    }

    public BigDecimal getCaseAmount() {
        if (medicalCase != null) {
            return medicalCase.getCaseAmount();
        } else if (medicalCaseAud != null) {
            return medicalCaseAud.getCaseAmount();
        }
        return null;
    }

    public Long getVersionNumberOfMedicalCase() {
        return versionNumberOfMedicalCase;
    }

}
