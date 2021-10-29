package ru.ibs.kmplib.request.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author me
 */
public class Allergies {

	@JsonProperty("Screen")
	private boolean screen;
	@JsonProperty("Type")
	private String type;
	@JsonProperty("Code")
	private String code;
	@JsonProperty("Name")
	private String name;

	public Allergies() {
	}

	public Allergies(boolean screen, String type, String code, String name) {
		this.screen = screen;
		this.type = type;
		this.code = code;
		this.name = name;
	}

	public boolean isScreen() {
		return screen;
	}

	public void setScreen(boolean screen) {
		this.screen = screen;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
