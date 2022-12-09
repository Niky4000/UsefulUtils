package ru.ibs.docxtestproject.bean;

import java.util.Date;

public class MedicalAndEconomicControlActWordHeadBean {

	private Date period;
	private String periodStr;
	private Long lpuId;
	private Long parcelId;
	private Integer num;
	private Date dateCrt;
	private Long dateSend;
	private Long moName;
	private Long moMcod;
	private Long smoName;
	private Long smoQq;
	private Double serviceSumAll;
	private Long admName;
	private Long lastDay;

	public MedicalAndEconomicControlActWordHeadBean() {
	}

	public MedicalAndEconomicControlActWordHeadBean(Object[] array) {
	}

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

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Date getDateCrt() {
		return dateCrt;
	}

	public void setDateCrt(Date dateCrt) {
		this.dateCrt = dateCrt;
	}

	public Long getDateSend() {
		return dateSend;
	}

	public void setDateSend(Long dateSend) {
		this.dateSend = dateSend;
	}

	public Long getMoName() {
		return moName;
	}

	public void setMoName(Long moName) {
		this.moName = moName;
	}

	public Long getMoMcod() {
		return moMcod;
	}

	public void setMoMcod(Long moMcod) {
		this.moMcod = moMcod;
	}

	public Long getSmoName() {
		return smoName;
	}

	public void setSmoName(Long smoName) {
		this.smoName = smoName;
	}

	public Long getSmoQq() {
		return smoQq;
	}

	public void setSmoQq(Long smoQq) {
		this.smoQq = smoQq;
	}

	public Double getServiceSumAll() {
		return serviceSumAll;
	}

	public void setServiceSumAll(Double serviceSumAll) {
		this.serviceSumAll = serviceSumAll;
	}

	public Long getAdmName() {
		return admName;
	}

	public void setAdmName(Long admName) {
		this.admName = admName;
	}

	public Long getLastDay() {
		return lastDay;
	}

	public void setLastDay(Long lastDay) {
		this.lastDay = lastDay;
	}
}
