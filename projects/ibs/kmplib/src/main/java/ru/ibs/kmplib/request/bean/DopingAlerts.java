package ru.ibs.kmplib.request.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author me
 */
public class DopingAlerts {

	@JsonProperty("IgnoreGender")
	private boolean ignoreGender;
	@JsonProperty("IgnoreCompetitionPeriod")
	private boolean ignoreCompetitionPeriod;

	public DopingAlerts() {
	}

	public DopingAlerts(boolean ignoreGender, boolean ignoreCompetitionPeriod) {
		this.ignoreGender = ignoreGender;
		this.ignoreCompetitionPeriod = ignoreCompetitionPeriod;
	}

	public boolean isIgnoreGender() {
		return ignoreGender;
	}

	public void setIgnoreGender(boolean ignoreGender) {
		this.ignoreGender = ignoreGender;
	}

	public boolean isIgnoreCompetitionPeriod() {
		return ignoreCompetitionPeriod;
	}

	public void setIgnoreCompetitionPeriod(boolean ignoreCompetitionPeriod) {
		this.ignoreCompetitionPeriod = ignoreCompetitionPeriod;
	}
}
