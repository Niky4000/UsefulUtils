package ru.ibs.docxtestproject.bean;

import java.util.Date;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean {

	private Date dateCrt;
	private Date dateSend;
	private Integer num;
	private String periodStr;
	private String lastDay;
	private String moName;

	public RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean() {
	}

	public RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean(Object[] array) {
		this.dateCrt = array[0] != null ? (Date) array[0] : null;
		this.dateSend = array[1] != null ? (Date) array[1] : null;
		this.num = array[2] != null ? ((Number) array[2]).intValue() : null;
		this.periodStr = array[3] != null ? (String) array[3] : null;
		this.lastDay = array[4] != null ? (String) array[4] : null;
		this.moName = array[5] != null ? (String) array[5] : null;
	}

	public Date getDateCrt() {
		return dateCrt;
	}

	public void setDateCrt(Date dateCrt) {
		this.dateCrt = dateCrt;
	}

	public Date getDateSend() {
		return dateSend;
	}

	public void setDateSend(Date dateSend) {
		this.dateSend = dateSend;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getPeriodStr() {
		return periodStr;
	}

	public void setPeriodStr(String periodStr) {
		this.periodStr = periodStr;
	}

	public String getLastDay() {
		return lastDay;
	}

	public void setLastDay(String lastDay) {
		this.lastDay = lastDay;
	}

	public String getMoName() {
		return moName;
	}

	public void setMoName(String moName) {
		this.moName = moName;
	}
}
