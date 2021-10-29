package ru.ibs.kmplib.bean;

/**
 *
 * @author me
 */
public class CodeNameAndVersionDictionary {

	private final String code;
	private final String name;
	private final String versionId;

	public CodeNameAndVersionDictionary(String code, String name, String versionId) {
		this.code = code;
		this.name = name;
		this.versionId = versionId;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getVersionId() {
		return versionId;
	}
}
