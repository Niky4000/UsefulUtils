package ru.ibs.docxtestproject.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean {

	private Integer filId;
	private String iotd;
	private Long billId;
	private Date period;
	private Double excessSum;
	private Double serviceSum;
	private Double notAcceptedServiceSum;
	private Double notAcceptedServiceSumBeforeRepeatedCheck;
	private Double holdedServiceSum;
	private Double serviceSumThatShouldBeHoldedOnTheNextPeriod;

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

	public Double getExcessSum() {
		return excessSum;
	}

	public void setExcessSum(Double excessSum) {
		this.excessSum = excessSum;
	}

	public Double getServiceSum() {
		return serviceSum;
	}

	public void setServiceSum(Double serviceSum) {
		this.serviceSum = serviceSum;
	}

	public Double getNotAcceptedServiceSum() {
		return notAcceptedServiceSum;
	}

	public void setNotAcceptedServiceSum(Double notAcceptedServiceSum) {
		this.notAcceptedServiceSum = notAcceptedServiceSum;
	}

	public Double getNotAcceptedServiceSumBeforeRepeatedCheck() {
		return notAcceptedServiceSumBeforeRepeatedCheck;
	}

	public void setNotAcceptedServiceSumBeforeRepeatedCheck(Double notAcceptedServiceSumBeforeRepeatedCheck) {
		this.notAcceptedServiceSumBeforeRepeatedCheck = notAcceptedServiceSumBeforeRepeatedCheck;
	}

	public Double getHoldedServiceSum() {
		return holdedServiceSum;
	}

	public void setHoldedServiceSum(Double holdedServiceSum) {
		this.holdedServiceSum = holdedServiceSum;
	}

	public Double getServiceSumThatShouldBeHoldedOnTheNextPeriod() {
		return serviceSumThatShouldBeHoldedOnTheNextPeriod;
	}

	public void setServiceSumThatShouldBeHoldedOnTheNextPeriod(Double serviceSumThatShouldBeHoldedOnTheNextPeriod) {
		this.serviceSumThatShouldBeHoldedOnTheNextPeriod = serviceSumThatShouldBeHoldedOnTheNextPeriod;
	}
}
