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
	private Integer qtAll;
	private Double sumAll;
	private Integer qtAll1;
	private Double sumAll1;
	private Integer qtAll2;
	private Double sumAll2;
	private Integer qtAll3;
	private Double sumAll3;
	private Integer qtAll4;
	private Double sumAll4;

//fil_id
//iotd
//Bill_id
//QUARTER
//SUM_PRE
//SUM_FILL
//SUM_PRE
//SUM_ALL
//«0»
//qt_all
//sum_all
//qt_all1
//sum_all1
//qt_all2
//sum_all2
//qt_all3
//sum_all3
//qt_all4
//sum_all4
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
		this.qtAll = array[9] != null ? ((Number) array[9]).intValue() : null;
		this.sumAll = array[10] != null ? ((Number) array[10]).doubleValue() : null;
		this.qtAll1 = array[11] != null ? ((Number) array[11]).intValue() : null;
		this.sumAll1 = array[12] != null ? ((Number) array[12]).doubleValue() : null;
		this.qtAll2 = array[13] != null ? ((Number) array[13]).intValue() : null;
		this.sumAll2 = array[14] != null ? ((Number) array[14]).doubleValue() : null;
		this.qtAll3 = array[15] != null ? ((Number) array[15]).intValue() : null;
		this.sumAll3 = array[16] != null ? ((Number) array[16]).doubleValue() : null;
		this.qtAll4 = array[17] != null ? ((Number) array[17]).intValue() : null;
		this.sumAll4 = array[18] != null ? ((Number) array[18]).doubleValue() : null;
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

	public Integer getQtAll() {
		return qtAll;
	}

	public void setQtAll(Integer qtAll) {
		this.qtAll = qtAll;
	}

	public Double getSumAll() {
		return sumAll;
	}

	public void setSumAll(Double sumAll) {
		this.sumAll = sumAll;
	}

	public Integer getQtAll1() {
		return qtAll1;
	}

	public void setQtAll1(Integer qtAll1) {
		this.qtAll1 = qtAll1;
	}

	public Double getSumAll1() {
		return sumAll1;
	}

	public void setSumAll1(Double sumAll1) {
		this.sumAll1 = sumAll1;
	}

	public Integer getQtAll2() {
		return qtAll2;
	}

	public void setQtAll2(Integer qtAll2) {
		this.qtAll2 = qtAll2;
	}

	public Double getSumAll2() {
		return sumAll2;
	}

	public void setSumAll2(Double sumAll2) {
		this.sumAll2 = sumAll2;
	}

	public Integer getQtAll3() {
		return qtAll3;
	}

	public void setQtAll3(Integer qtAll3) {
		this.qtAll3 = qtAll3;
	}

	public Double getSumAll3() {
		return sumAll3;
	}

	public void setSumAll3(Double sumAll3) {
		this.sumAll3 = sumAll3;
	}

	public Integer getQtAll4() {
		return qtAll4;
	}

	public void setQtAll4(Integer qtAll4) {
		this.qtAll4 = qtAll4;
	}

	public Double getSumAll4() {
		return sumAll4;
	}

	public void setSumAll4(Double sumAll4) {
		this.sumAll4 = sumAll4;
	}
}
