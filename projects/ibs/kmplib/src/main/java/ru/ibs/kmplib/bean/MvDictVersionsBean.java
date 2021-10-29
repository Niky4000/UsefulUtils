package ru.ibs.kmplib.bean;

import java.util.Date;

/**
 *
 * @author me
 */
public class MvDictVersionsBean {

	private final String mkb10Ver;
	private final String medicamentVer;
	private final Date period;

	public MvDictVersionsBean(String mkb10Ver, String medicamentVer, Date period) {
		this.mkb10Ver = mkb10Ver;
		this.medicamentVer = medicamentVer;
		this.period = period;
	}

	public String getMkb10Ver() {
		return mkb10Ver;
	}

	public String getMedicamentVer() {
		return medicamentVer;
	}

	public Date getPeriod() {
		return period;
	}
}
