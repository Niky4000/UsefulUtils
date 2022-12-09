package ru.ibs.docxtestproject.bean;

import java.util.Date;

public class MedicalAndEconomicControlActWordSecondTableBean {

//      R_N      NUMBER
	private Long rn;
//     PERIOD   DATE
	private Date period;
//     LPU_ID    NUMBER
	private Long lpuId;
//     PARCEL_ID NUMBER
	private Long parcelId;
//     RECID       VARCHAR2(20)
	private String recid;
//     SN_POL      VARCHAR2(20)
	private String snPol;
//     USL_OK   VARCHAR2(20)
	private String uslOk;
//     DATE_BEGIN  VARCHAR2(20)
	private String dateBegin;
//     DATE_END    VARCHAR2(20)
	private String dateEnd;
//     PROF_COD    VARCHAR(30)
	private String profCode;
//     PROF     VARCHAR2(20)
	private String prof;
//     OD         VARCHAR(30)
	private String code;
//     COUNT_ERR   NUMBER
	private Long countErr;
//     ERR_CODE    VARCHAR(30)
	private String errCode;
//     ERR_CODE_1  VARCHAR(30)
	private String errCode1;
//     ERR_CODE_2  VARCHAR(30)
	private String errCode2;
//     ERR_CODE_3  VARCHAR(30) 
	private String errCode3;
//     ERR_CODE_4  VARCHAR(30)
	private String errCode4;
//     ERR_CODE_5  VARCHAR(30)
	private String errCode5;
//     SANK_SUM    VARCHAR(30)
	private String sankSum;
//     ERR_SUM  NUMBER 
	private Double errSum;
//     FINE_SUM    VARCHAR(30)
	private String fineSum;
//     DS      VARCHAR2(6)
	private String ds;

	public MedicalAndEconomicControlActWordSecondTableBean() {
	}

	public MedicalAndEconomicControlActWordSecondTableBean(Object[] array) {
		rn = array[0] != null ? ((Number) array[0]).longValue() : null;
		period = array[1] != null ? (Date) array[1] : null;
		lpuId = array[2] != null ? ((Number) array[2]).longValue() : null;
		parcelId = array[3] != null ? ((Number) array[3]).longValue() : null;
		recid = array[4] != null ? (String) array[4] : null;
		snPol = array[5] != null ? (String) array[5] : null;
		uslOk = array[6] != null ? (String) array[6] : null;
		dateBegin = array[7] != null ? (String) array[7] : null;
		dateEnd = array[8] != null ? (String) array[8] : null;
		profCode = array[9] != null ? (String) array[9] : null;
		prof = array[10] != null ? (String) array[10] : null;
		code = array[11] != null ? (String) array[11] : null;
		countErr = array[12] != null ? ((Number) array[12]).longValue() : null;
		errCode = array[13] != null ? (String) array[13] : null;
		errCode1 = array[14] != null ? (String) array[14] : null;
		errCode2 = array[15] != null ? (String) array[15] : null;
		errCode3 = array[16] != null ? (String) array[16] : null;
		errCode4 = array[17] != null ? (String) array[17] : null;
		errCode5 = array[18] != null ? (String) array[18] : null;
		sankSum = array[19] != null ? (String) array[19] : null;
		errSum = array[20] != null ? ((Number) array[20]).doubleValue() : null;
		fineSum = array[21] != null ? (String) array[21] : null;
		ds = array[22] != null ? (String) array[22] : null;
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

	public String getRecid() {
		return recid;
	}

	public void setRecid(String recid) {
		this.recid = recid;
	}

	public String getSnPol() {
		return snPol;
	}

	public void setSnPol(String snPol) {
		this.snPol = snPol;
	}

	public String getUslOk() {
		return uslOk;
	}

	public void setUslOk(String uslOk) {
		this.uslOk = uslOk;
	}

	public String getDateBegin() {
		return dateBegin;
	}

	public void setDateBegin(String dateBegin) {
		this.dateBegin = dateBegin;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getProfCode() {
		return profCode;
	}

	public void setProfCode(String profCode) {
		this.profCode = profCode;
	}

	public String getProf() {
		return prof;
	}

	public void setProf(String prof) {
		this.prof = prof;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getCountErr() {
		return countErr;
	}

	public void setCountErr(Long countErr) {
		this.countErr = countErr;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrCode1() {
		return errCode1;
	}

	public void setErrCode1(String errCode1) {
		this.errCode1 = errCode1;
	}

	public String getErrCode2() {
		return errCode2;
	}

	public void setErrCode2(String errCode2) {
		this.errCode2 = errCode2;
	}

	public String getErrCode3() {
		return errCode3;
	}

	public void setErrCode3(String errCode3) {
		this.errCode3 = errCode3;
	}

	public String getErrCode4() {
		return errCode4;
	}

	public void setErrCode4(String errCode4) {
		this.errCode4 = errCode4;
	}

	public String getErrCode5() {
		return errCode5;
	}

	public void setErrCode5(String errCode5) {
		this.errCode5 = errCode5;
	}

	public String getSankSum() {
		return sankSum;
	}

	public void setSankSum(String sankSum) {
		this.sankSum = sankSum;
	}

	public Double getErrSum() {
		return errSum;
	}

	public void setErrSum(Double errSum) {
		this.errSum = errSum;
	}

	public String getFineSum() {
		return fineSum;
	}

	public void setFineSum(String fineSum) {
		this.fineSum = fineSum;
	}

	public String getDs() {
		return ds;
	}

	public void setDs(String ds) {
		this.ds = ds;
	}
}
