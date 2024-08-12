package ru.ibs.kmplib.response.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author me
 */
public class ScreeningResponseBean {

	@JsonProperty("Id")
	private String id;
	@JsonProperty("Messages")
	private List<Messages> messagesList;
	@JsonProperty("DrugDrugInteractions")
	private DrugDrugInteractions drugDrugInteractions;
	@JsonProperty("AllergicReactions")
	private AllergicReactions allergicReactions;
	@JsonProperty("AgeContraindications")
	private AgeContraindications ageContraindications;
	@JsonProperty("DiseaseContraindications")
	private DiseaseContraindications diseaseContraindications;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Messages> getMessagesList() {
		return messagesList;
	}

	public void setMessagesList(List<Messages> messagesList) {
		this.messagesList = messagesList;
	}

	public DrugDrugInteractions getDrugDrugInteractions() {
		return drugDrugInteractions;
	}

	public void setDrugDrugInteractions(DrugDrugInteractions drugDrugInteractions) {
		this.drugDrugInteractions = drugDrugInteractions;
	}

	public AllergicReactions getAllergicReactions() {
		return allergicReactions;
	}

	public void setAllergicReactions(AllergicReactions allergicReactions) {
		this.allergicReactions = allergicReactions;
	}

	public AgeContraindications getAgeContraindications() {
		return ageContraindications;
	}

	public void setAgeContraindications(AgeContraindications ageContraindications) {
		this.ageContraindications = ageContraindications;
	}

	public DiseaseContraindications getDiseaseContraindications() {
		return diseaseContraindications;
	}

	public void setDiseaseContraindications(DiseaseContraindications diseaseContraindications) {
		this.diseaseContraindications = diseaseContraindications;
	}
}
