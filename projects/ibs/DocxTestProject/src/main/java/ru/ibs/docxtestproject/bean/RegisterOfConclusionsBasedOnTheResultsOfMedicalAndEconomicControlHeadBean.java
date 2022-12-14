package ru.ibs.docxtestproject.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean {

	private Date period;
	private String periodStr;
	private String lpuFullName;
	private Integer caseIdCount;
	private Double serviceSum;
	private Integer caseIdCount2Stationary;
	private Double serviceSum2Stationary;
	private Integer caseIdCount3NotAccepted;
	private Double serviceSum3NotAccepted;
	private Date lastSendingDate;

	public Date getPeriod() {
		return period;
	}

	public void setPeriod(Date period) {
		this.period = period;
	}

	public String getPeriodStr() {
		return periodStr;
	}

	public void setPeriodStr(String periodStr) {
		this.periodStr = periodStr;
	}

	public String getLpuFullName() {
		return lpuFullName;
	}

	public void setLpuFullName(String lpuFullName) {
		this.lpuFullName = lpuFullName;
	}

	public int getLastDayOfPeriod() {
		Calendar instance = Calendar.getInstance();
		instance.setTime(period);
		return instance.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public Integer getCaseIdCount() {
		return caseIdCount;
	}

	public void setCaseIdCount(Integer caseIdCount) {
		this.caseIdCount = caseIdCount;
	}

	public Double getServiceSum() {
		return serviceSum;
	}

	public void setServiceSum(Double serviceSum) {
		this.serviceSum = serviceSum;
	}

	public Integer getCaseIdCount2Stationary() {
		return caseIdCount2Stationary;
	}

	public void setCaseIdCount2Stationary(Integer caseIdCount2Stationary) {
		this.caseIdCount2Stationary = caseIdCount2Stationary;
	}

	public Double getServiceSum2Stationary() {
		return serviceSum2Stationary;
	}

	public void setServiceSum2Stationary(Double serviceSum2Stationary) {
		this.serviceSum2Stationary = serviceSum2Stationary;
	}

	public Integer getCaseIdCount3NotAccepted() {
		return caseIdCount3NotAccepted;
	}

	public void setCaseIdCount3NotAccepted(Integer caseIdCount3NotAccepted) {
		this.caseIdCount3NotAccepted = caseIdCount3NotAccepted;
	}

	public Double getServiceSum3NotAccepted() {
		return serviceSum3NotAccepted;
	}

	public void setServiceSum3NotAccepted(Double serviceSum3NotAccepted) {
		this.serviceSum3NotAccepted = serviceSum3NotAccepted;
	}

	public String getLastSendingDate() {
		return lastSendingDate != null ? new SimpleDateFormat("dd.MM.yyyy").format(lastSendingDate) : "";
	}

	public void setLastSendingDate(Date lastSendingDate) {
		this.lastSendingDate = lastSendingDate;
	}
}
