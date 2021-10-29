package ru.ibs.kmplib.response.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.ibs.kmplib.request.bean.Allergies;
import ru.ibs.kmplib.request.bean.Drugs;

/**
 *
 * @author me
 */
public class Items {

	@JsonProperty("Diseases")
	private List<Diseases> diseasesList;
	@JsonProperty("Severity")
	private Severity severity;
	@JsonProperty("Condition")
	private Condition condition;
	@JsonProperty("Management")
	private Management management;
	@JsonProperty("Documentation")
	private Documentation documentation;
	@JsonProperty("Onset")
	private Onset onset;
	@JsonProperty("Kind")
	private String kind;
	@JsonProperty("Allergies")
	private List<Allergies> allergiesList;
	@JsonProperty("AllergenClass")
	private AllergenClass allergenClass;
	@JsonProperty("Code")
	private String code;
	@JsonProperty("Alert")
	private String alert;
	@JsonProperty("ProfessionalMonograph")
	private String professionalMonograph;
	@JsonProperty("Drugs")
	private List<Drugs> drugsList;

	public List<Diseases> getDiseasesList() {
		return diseasesList;
	}

	public void setDiseasesList(List<Diseases> diseasesList) {
		this.diseasesList = diseasesList;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Management getManagement() {
		return management;
	}

	public void setManagement(Management management) {
		this.management = management;
	}

	public Documentation getDocumentation() {
		return documentation;
	}

	public void setDocumentation(Documentation documentation) {
		this.documentation = documentation;
	}

	public Onset getOnset() {
		return onset;
	}

	public void setOnset(Onset onset) {
		this.onset = onset;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public List<Allergies> getAllergiesList() {
		return allergiesList;
	}

	public void setAllergiesList(List<Allergies> allergiesList) {
		this.allergiesList = allergiesList;
	}

	public AllergenClass getAllergenClass() {
		return allergenClass;
	}

	public void setAllergenClass(AllergenClass allergenClass) {
		this.allergenClass = allergenClass;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getProfessionalMonograph() {
		return professionalMonograph;
	}

	public void setProfessionalMonograph(String professionalMonograph) {
		this.professionalMonograph = professionalMonograph;
	}

	public List<Drugs> getDrugsList() {
		return drugsList;
	}

	public void setDrugsList(List<Drugs> drugsList) {
		this.drugsList = drugsList;
	}
}
