package ru.ibs.kmplib.request.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author me
 */
public class Options {

	@JsonProperty("IncludeInsignificantInactiveIngredients")
	private boolean includeInsignificantInactiveIngredients;
	@JsonProperty("IncludeMonographs")
	private boolean includeMonographs;
	@JsonProperty("DopingAlerts")
	private DopingAlerts dopingAlerts;

	public Options() {
	}

	public Options(boolean includeInsignificantInactiveIngredients, boolean includeMonographs, DopingAlerts dopingAlerts) {
		this.includeInsignificantInactiveIngredients = includeInsignificantInactiveIngredients;
		this.includeMonographs = includeMonographs;
		this.dopingAlerts = dopingAlerts;
	}

	public boolean isIncludeInsignificantInactiveIngredients() {
		return includeInsignificantInactiveIngredients;
	}

	public void setIncludeInsignificantInactiveIngredients(boolean includeInsignificantInactiveIngredients) {
		this.includeInsignificantInactiveIngredients = includeInsignificantInactiveIngredients;
	}

	public boolean isIncludeMonographs() {
		return includeMonographs;
	}

	public void setIncludeMonographs(boolean includeMonographs) {
		this.includeMonographs = includeMonographs;
	}

	public DopingAlerts getDopingAlerts() {
		return dopingAlerts;
	}

	public void setDopingAlerts(DopingAlerts dopingAlerts) {
		this.dopingAlerts = dopingAlerts;
	}
}
