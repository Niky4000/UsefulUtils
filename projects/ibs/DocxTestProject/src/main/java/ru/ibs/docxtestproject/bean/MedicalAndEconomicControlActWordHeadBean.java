package ru.ibs.docxtestproject.bean;

import java.util.Date;

public class MedicalAndEconomicControlActWordHeadBean {

	private Double serviceSumAll;
	private Date dateCrt;
	private Integer num;
	private String smoName;
	private String moName;
	private String periodStr;
	private String lastDay;

	public MedicalAndEconomicControlActWordHeadBean() {
	}

	public MedicalAndEconomicControlActWordHeadBean(Object[] array) {
	}

	public Double getServiceSumAll() {
		return serviceSumAll;
	}

	public void setServiceSumAll(Double serviceSumAll) {
		this.serviceSumAll = serviceSumAll;
	}

	public Date getDateCrt() {
		return dateCrt;
	}

	public void setDateCrt(Date dateCrt) {
		this.dateCrt = dateCrt;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getSmoName() {
		return smoName;
	}

	public void setSmoName(String smoName) {
		this.smoName = smoName;
	}

	public String getMoName() {
		return moName;
	}

	public void setMoName(String moName) {
		this.moName = moName;
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
}
