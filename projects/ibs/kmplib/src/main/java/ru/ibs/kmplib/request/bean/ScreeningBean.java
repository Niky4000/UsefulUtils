package ru.ibs.kmplib.request.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author me
 */
public class ScreeningBean {

	@JsonProperty("ScreeningTypes")
	private String screeningTypes;
	@JsonProperty("Patient")
	private Patient patient;
	@JsonProperty("Drugs")
	private List<Drugs> drugsList;
	@JsonProperty("Allergies")
	private List<Allergies> allergiesList;
	@JsonProperty("Diseases")
	private List<Diseases> diseasesList;
	@JsonProperty("Options")
	private Options options;
	@JsonProperty("IncludeFinishedDrugs")
	private boolean includeFinishedDrugs;

	public ScreeningBean() {
	}

	public ScreeningBean(String screeningTypes, Patient patient, List<Drugs> drugsList, List<Allergies> allergiesList, List<Diseases> diseasesList, Options options, boolean includeFinishedDrugs) {
		this.screeningTypes = screeningTypes;
		this.patient = patient;
		this.drugsList = drugsList;
		this.allergiesList = allergiesList;
		this.diseasesList = diseasesList;
		this.options = options;
		this.includeFinishedDrugs = includeFinishedDrugs;
	}

	public String getScreeningTypes() {
		return screeningTypes;
	}

	public void setScreeningTypes(String screeningTypes) {
		this.screeningTypes = screeningTypes;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public List<Drugs> getDrugsList() {
		return drugsList;
	}

	public void setDrugsList(List<Drugs> drugsList) {
		this.drugsList = drugsList;
	}

	public List<Allergies> getAllergiesList() {
		return allergiesList;
	}

	public void setAllergiesList(List<Allergies> allergiesList) {
		this.allergiesList = allergiesList;
	}

	public List<Diseases> getDiseasesList() {
		return diseasesList;
	}

	public void setDiseasesList(List<Diseases> diseasesList) {
		this.diseasesList = diseasesList;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public boolean isIncludeFinishedDrugs() {
		return includeFinishedDrugs;
	}

	public void setIncludeFinishedDrugs(boolean includeFinishedDrugs) {
		this.includeFinishedDrugs = includeFinishedDrugs;
	}
}
