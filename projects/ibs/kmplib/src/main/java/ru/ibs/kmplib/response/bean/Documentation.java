package ru.ibs.kmplib.response.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author me
 */
public class Documentation {

	@JsonProperty("Level")
	private Long level;
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
