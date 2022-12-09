package ru.ibs.docxtestproject.bean;

import java.util.Date;

public class MedicalAndEconomicControlActWordFirstTableBean {

//      R_N      NUMBER
	private Long rn;
//     PERIOD   DATE
	private Date period;
//     LPU_ID    NUMBER
	private Long lpuId;
//     PARCEL_ID NUMBER
	private Long parcelId;
//     PROF     VARCHAR2(20)
	private String prof;
//     USL_OK   VARCHAR2(20)
	private String uslOk;
//     OPL_QT   NUMBER 
	private Double oplQt;
//     OPL_SUM  NUMBER 
	private Double oplSum;
//     ERR_QT   NUMBER 
	private Double errQt;
//     ERR_SUM  NUMBER 
	private Double errSum;
//     ITOG_QT  NUMBER 
	private Double itogQt;
//     ITOG_SUM NUMBER
	private Double itogSum;

	public MedicalAndEconomicControlActWordFirstTableBean() {
	}

	public MedicalAndEconomicControlActWordFirstTableBean(Object[] array) {
		rn = array[0] != null ? ((Number) array[0]).longValue() : null;
		period = array[1] != null ? (Date) array[1] : null;
		lpuId = array[2] != null ? ((Number) array[2]).longValue() : null;
		parcelId = array[3] != null ? ((Number) array[3]).longValue() : null;
		prof = array[4] != null ? (String) array[4] : null;
		uslOk = array[5] != null ? (String) array[5] : null;
		oplQt = array[6] != null ? ((Number) array[6]).doubleValue() : null;
		oplSum = array[7] != null ? ((Number) array[7]).doubleValue() : null;
		errQt = array[8] != null ? ((Number) array[8]).doubleValue() : null;
		errSum = array[9] != null ? ((Number) array[9]).doubleValue() : null;
		itogQt = array[10] != null ? ((Number) array[10]).doubleValue() : null;
		itogSum = array[11] != null ? ((Number) array[11]).doubleValue() : null;
	}

	public Long getRn() {
		return rn;
	}

	public void setRn(Long rn) {
		this.rn = rn;
	}

	public Date getPeriod() {
		return period;
	}

	public void setPeriod(Date period) {
		this.period = period;
	}

	public Long getLpuId() {
		return lpuId;
	}

	public void setLpuId(Long lpuId) {
		this.lpuId = lpuId;
	}

	public Long getParcelId() {
		return parcelId;
	}

	public void setParcelId(Long parcelId) {
		this.parcelId = parcelId;
	}

	public String getProf() {
		return prof;
	}

	public void setProf(String prof) {
		this.prof = prof;
	}

	public String getUslOk() {
		return uslOk;
	}

	public void setUslOk(String uslOk) {
		this.uslOk = uslOk;
	}

	public Double getOplQt() {
		return oplQt;
	}

	public void setOplQt(Double oplQt) {
		this.oplQt = oplQt;
	}

	public Double getOplSum() {
		return oplSum;
	}

	public void setOplSum(Double oplSum) {
		this.oplSum = oplSum;
	}

	public Double getErrQt() {
		return errQt;
	}

	public void setErrQt(Double errQt) {
		this.errQt = errQt;
	}

	public Double getErrSum() {
		return errSum;
	}

	public void setErrSum(Double errSum) {
		this.errSum = errSum;
	}

	public Double getItogQt() {
		return itogQt;
	}

	public void setItogQt(Double itogQt) {
		this.itogQt = itogQt;
	}

	public Double getItogSum() {
		return itogSum;
	}

	public void setItogSum(Double itogSum) {
		this.itogSum = itogSum;
	}
}
