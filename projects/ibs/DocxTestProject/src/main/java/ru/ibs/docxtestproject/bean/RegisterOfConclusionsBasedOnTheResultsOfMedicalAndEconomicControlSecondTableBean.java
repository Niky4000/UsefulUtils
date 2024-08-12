package ru.ibs.docxtestproject.bean;

import java.util.Date;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean {

	private String uslOk;
	private Integer filId;
	private Long billId;
	private String iotd;
	private String dateMn;
	private String policyNumber;
	private String territoryCode;
	private String errorCode;
	private Double serviceSum;
	private String financialSanctionsCode;
	private String otherErrorCodes;

//	usl_ok,fil_id,bill_id,iotd,date_mm,sn_pol,cod_terr,err_code_1,service_sum,cod_fine,err_other
	public RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean() {
	}

	public RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean(Object[] array) {
		if (array != null) {
			this.uslOk = array[0] != null ? (String) array[0] : null;
			this.filId = array[1] != null ? ((Number) array[1]).intValue() : null;
			this.billId = array[2] != null ? ((Number) array[2]).longValue() : null;
			this.iotd = array[3] != null ? (String) array[3] : null;
			this.dateMn = array[4] != null ? (String) array[4] : null;
			this.policyNumber = array[5] != null ? (String) array[5] : null;
			this.territoryCode = array[6] != null ? (String) array[6] : null;
			this.errorCode = array[7] != null ? (String) array[7] : null;
			this.serviceSum = array[8] != null ? ((Number) array[8]).doubleValue() : null;
			this.financialSanctionsCode = array[9] != null ? (String) array[9] : null;
			this.otherErrorCodes = array[10] != null ? (String) array[10] : null;
		}
	}

	public String getUslOk() {
		return uslOk;
	}

	public void setUslOk(String uslOk) {
		this.uslOk = uslOk;
	}

	public Integer getFilId() {
		return filId;
	}

	public void setFilId(Integer filId) {
		this.filId = filId;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}

	public String getIotd() {
		return iotd;
	}

	public void setIotd(String iotd) {
		this.iotd = iotd;
	}

	public String getDateMn() {
		return dateMn;
	}

	public void setDateMn(String dateMn) {
		this.dateMn = dateMn;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getTerritoryCode() {
		return territoryCode;
	}

	public void setTerritoryCode(String territoryCode) {
		this.territoryCode = territoryCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Double getServiceSum() {
		return serviceSum;
	}

	public void setServiceSum(Double serviceSum) {
		this.serviceSum = serviceSum;
	}

	public String getFinancialSanctionsCode() {
		return financialSanctionsCode;
	}

	public void setFinancialSanctionsCode(String financialSanctionsCode) {
		this.financialSanctionsCode = financialSanctionsCode;
	}

	public String getOtherErrorCodes() {
		return otherErrorCodes;
	}

	public void setOtherErrorCodes(String otherErrorCodes) {
		this.otherErrorCodes = otherErrorCodes;
	}
}
