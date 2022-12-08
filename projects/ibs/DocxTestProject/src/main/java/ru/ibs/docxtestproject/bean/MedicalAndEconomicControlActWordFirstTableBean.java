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
//     LAST_DAY VARCHAR2(20)
	private String lastDay;
//     DS      VARCHAR2(6)
	private String ds;

	public MedicalAndEconomicControlActWordFirstTableBean() {
	}

	public MedicalAndEconomicControlActWordFirstTableBean(Object[] array) {
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

	public String getLastDay() {
		return lastDay;
	}

	public void setLastDay(String lastDay) {
		this.lastDay = lastDay;
	}

	public String getDs() {
		return ds;
	}

	public void setDs(String ds) {
		this.ds = ds;
	}
}
