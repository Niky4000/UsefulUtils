package ru.ibs.kmplib.bean;

import java.util.Objects;

/**
 *
 * @author me
 */
public class Diagnosis {

	private final String diagnosisCode;
	private String diagnosisName;

	public Diagnosis(String diagnosisCode) {
		this.diagnosisCode = diagnosisCode;
	}

	public Diagnosis(String diagnosisCode, String diagnosisName) {
		this.diagnosisCode = diagnosisCode;
		this.diagnosisName = diagnosisName;
	}

	public String getDiagnosisCode() {
		return diagnosisCode;
	}

	public String getDiagnosisName() {
		return diagnosisName;
	}

	public void setDiagnosisName(String diagnosisName) {
		this.diagnosisName = diagnosisName;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.diagnosisCode);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Diagnosis other = (Diagnosis) obj;
		if (!Objects.equals(this.diagnosisCode, other.diagnosisCode))
			return false;
		return true;
	}
}
