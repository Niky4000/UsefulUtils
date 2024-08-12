package ru.ibs.pmp.smo.report.model;

/**
 *
 * @author me
 */
public class ActMeePdfDataTable3 {

//    ACT_ID NUMBER,
//	private String actId;
//    RN VARCHAR2(200),
	private String rn;
//    SN_POL VARCHAR2(200),
	private String snPol;
//    C_I VARCHAR2(200),
	private String ci;
//    START_DATE VARCHAR2(200),
	private String startDate;
//    END_DATE VARCHAR2(200),
	private String endDate;
//    DS VARCHAR2(200),
	private String ds;
//    PAYED_SUM VARCHAR2(200)
	private String payerSum;

	public String getRn() {
		return rn;
	}

	public void setRn(String rn) {
		this.rn = rn;
	}

	public String getSnPol() {
		return snPol;
	}

	public void setSnPol(String snPol) {
		this.snPol = snPol;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDs() {
		return ds;
	}

	public void setDs(String ds) {
		this.ds = ds;
	}

	public String getPayerSum() {
		return payerSum;
	}

	public void setPayerSum(String payerSum) {
		this.payerSum = payerSum;
	}
}
