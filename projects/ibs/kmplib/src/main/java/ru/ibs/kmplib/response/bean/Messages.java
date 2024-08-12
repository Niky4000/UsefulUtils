package ru.ibs.kmplib.response.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author me
 */
public class Messages {

	@JsonProperty("Code")
	private String code;
	@JsonProperty("Kind")
	private String kind;
	@JsonProperty("Text")
	private String text;
	@JsonProperty("Data")
	private Data data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
}
