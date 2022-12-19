package ru.ibs.docxtestproject.bean;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean {

	private Integer filId;
	private String iotd;
	private Long billId;
	private String quarter;
	private Double excessSum;
	private Double serviceSum;
	private Double notAcceptedServiceSum;
	private Double notAcceptedServiceSumBeforeRepeatedCheck;
	private Double holdedServiceSum;
	private Double serviceSumThatShouldBeHoldedOnTheNextPeriod;

//fil_id
//iotd
//Bill_id
//QUARTER
//SUM_PRE
//SUM_FILL
//SUM_PRE
//SUM_ALL
//«0»
	public RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean() {
	}

	public RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean(Object[] array) {
		this.filId = array[0] != null ? ((Number) array[0]).intValue() : null;
		this.iotd = array[1] != null ? (String) array[1] : null;
		this.billId = array[2] != null ? ((Number) array[2]).longValue() : null;
		this.quarter = array[3] != null ? (String) array[3] : null;
		this.excessSum = array[4] != null ? ((Number) array[4]).doubleValue() : null;
		this.serviceSum = array[5] != null ? ((Number) array[5]).doubleValue() : null;
		this.notAcceptedServiceSum = array[5] != null ? ((Number) array[5]).doubleValue() : null;
		this.notAcceptedServiceSumBeforeRepeatedCheck = array[6] != null ? ((Number) array[6]).doubleValue() : null;
		this.holdedServiceSum = array[7] != null ? ((Number) array[7]).doubleValue() : null;
		this.serviceSumThatShouldBeHoldedOnTheNextPeriod = array[8] != null ? ((Number) array[8]).doubleValue() : null;
	}

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

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
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
