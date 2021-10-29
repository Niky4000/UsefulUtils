package ru.ibs.kmplib.response.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author me
 */
public class Severity {

	@JsonProperty("Level")
	private Long level;
	@JsonProperty("Type")
	private String type;
	@JsonProperty("Code")
	private String code;
	@JsonProperty("Name")
	private String name;

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
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
