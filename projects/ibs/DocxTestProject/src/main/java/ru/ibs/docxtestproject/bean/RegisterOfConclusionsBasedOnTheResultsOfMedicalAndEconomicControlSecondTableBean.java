package ru.ibs.docxtestproject.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean {

	private Integer filId;
	private String iotd;
	private Long billId;
	private Date period;
	private String policyNumber;
	private Integer territoryCode;
	private String violationCode;
	private Double serviceSum;
	private Integer financialSanctionsCode;
	private Double financialSanctionsSum;
	private String otherViolationCodes;

	public Integer getFilId() {
		return filId;
	}

	public void setFilId(Integer filId) {
		this.filId = filId;
	}

	public String getIotd() {
		return iotd;
	}

	public void setIotd(String iotd) {
		this.iotd = iotd;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}

	public Date getPeriod() {
		return period;
	}

	public void setPeriod(Date period) {
		this.period = period;
	}

	public String getMonth() {
		return new SimpleDateFormat("MM").format(period);
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public Integer getTerritoryCode() {
		return territoryCode;
	}

	public void setTerritoryCode(Integer territoryCode) {
		this.territoryCode = territoryCode;
	}

	public String getViolationCode() {
		return violationCode;
	}

	public void setViolationCode(String violationCode) {
		this.violationCode = violationCode;
	}

	public Double getServiceSum() {
		return serviceSum;
	}

	public void setServiceSum(Double serviceSum) {
		this.serviceSum = serviceSum;
	}

	public Integer getFinancialSanctionsCode() {
		return financialSanctionsCode;
	}

	public void setFinancialSanctionsCode(Integer financialSanctionsCode) {
		this.financialSanctionsCode = financialSanctionsCode;
	}

	public Double getFinancialSanctionsSum() {
		return financialSanctionsSum;
	}

	public void setFinancialSanctionsSum(Double financialSanctionsSum) {
		this.financialSanctionsSum = financialSanctionsSum;
	}

	public String getOtherViolationCodes() {
		return otherViolationCodes;
	}

	public void setOtherViolationCodes(String otherViolationCodes) {
		this.otherViolationCodes = otherViolationCodes;
	}
}
