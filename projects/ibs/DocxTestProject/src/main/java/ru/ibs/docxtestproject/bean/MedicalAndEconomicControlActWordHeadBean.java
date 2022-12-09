package ru.ibs.docxtestproject.bean;

import java.util.Date;

public class MedicalAndEconomicControlActWordHeadBean {

	private Date period;
	private String periodStr;
	private Long lpuId;
	private Long parcelId;
	private String num;
	private String dateCrt;
	private Date dateSend;
	private String moName;
	private String moMcod;
	private String smoName;
	private String smoQq;
	private Double serviceSumAll;
	private String admName;
	private Date lastDay;

	public MedicalAndEconomicControlActWordHeadBean() {
	}

	public MedicalAndEconomicControlActWordHeadBean(Object[] array) {
		period = array[0] != null ? (Date) array[0] : null;
		periodStr = array[1] != null ? (String) array[1] : null;
		lpuId = array[2] != null ? ((Number) array[2]).longValue() : null;
		parcelId = array[3] != null ? ((Number) array[3]).longValue() : null;
		num = array[4] != null ? (String) array[4] : null;
		dateCrt = array[5] != null ? (String) array[5] : null;
		dateSend = array[6] != null ? (Date) array[6] : null;
		moName = array[7] != null ? (String) array[7] : null;
		moMcod = array[8] != null ? (String) array[8] : null;
		smoName = array[9] != null ? (String) array[9] : null;
		smoQq = array[10] != null ? (String) array[10] : null;
		serviceSumAll = array[11] != null ? ((Number) array[11]).doubleValue() : null;
		admName = array[12] != null ? (String) array[12] : null;
		lastDay = array[13] != null ? (Date) array[13] : null;
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

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getDateCrt() {
		return dateCrt;
	}

	public void setDateCrt(String dateCrt) {
		this.dateCrt = dateCrt;
	}

	public Date getDateSend() {
		return dateSend;
	}

	public void setDateSend(Date dateSend) {
		this.dateSend = dateSend;
	}

	public String getMoName() {
		return moName;
	}

	public void setMoName(String moName) {
		this.moName = moName;
	}

	public String getMoMcod() {
		return moMcod;
	}

	public void setMoMcod(String moMcod) {
		this.moMcod = moMcod;
	}

	public String getSmoName() {
		return smoName;
	}

	public void setSmoName(String smoName) {
		this.smoName = smoName;
	}

	public String getSmoQq() {
		return smoQq;
	}

	public void setSmoQq(String smoQq) {
		this.smoQq = smoQq;
	}

	public Double getServiceSumAll() {
		return serviceSumAll;
	}

	public void setServiceSumAll(Double serviceSumAll) {
		this.serviceSumAll = serviceSumAll;
	}

	public String getAdmName() {
		return admName;
	}

	public void setAdmName(String admName) {
		this.admName = admName;
	}

	public Date getLastDay() {
		return lastDay;
	}

	public void setLastDay(Date lastDay) {
		this.lastDay = lastDay;
	}
}
