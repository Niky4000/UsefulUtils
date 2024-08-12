package ru.ibs.kmplib.request.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author me
 */
public class Schedule {

	@JsonProperty("FirstAdministration")
	private String firstAdministration;
	@JsonProperty("LastAdministration")
	private String lastAdministration;

	public Schedule() {
	}

	public Schedule(String firstAdministration, String lastAdministration) {
		this.firstAdministration = firstAdministration;
		this.lastAdministration = lastAdministration;
	}

	public String getFirstAdministration() {
		return firstAdministration;
	}

	public void setFirstAdministration(String firstAdministration) {
		this.firstAdministration = firstAdministration;
	}

	public String getLastAdministration() {
		return lastAdministration;
	}

	public void setLastAdministration(String lastAdministration) {
		this.lastAdministration = lastAdministration;
	}
}
