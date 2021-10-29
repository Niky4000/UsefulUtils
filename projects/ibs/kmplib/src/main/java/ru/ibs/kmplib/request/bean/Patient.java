package ru.ibs.kmplib.request.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author me
 */
public class Patient {

	@JsonProperty("BirthDate")
	private String birthDate;
	@JsonProperty("Gender")
	private String gender;

	public Patient() {
	}

	public Patient(String birthDate, String gender) {
		this.birthDate = birthDate;
		this.gender = gender;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
