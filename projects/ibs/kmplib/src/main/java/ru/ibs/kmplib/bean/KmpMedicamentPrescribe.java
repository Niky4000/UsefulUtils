package ru.ibs.kmplib.bean;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Основной объект данных БД!
 *
 * @author me
 */
public class KmpMedicamentPrescribe implements Comparable<KmpMedicamentPrescribe> {

	private final Long id;
	private final String sid;
	private Date dateInj;
	private Date truncatedDateInj;
	private String name;
	private Diagnosis diagnosis;
	private String alert;

	public KmpMedicamentPrescribe(Long id, String sid, Date dateInj, Diagnosis diagnosis) {
		this.id = id;
		this.sid = sid;
		this.dateInj = dateInj;
		this.truncatedDateInj = DateUtils.truncate(dateInj, Calendar.MONTH);
		this.diagnosis = diagnosis;
	}

	public KmpMedicamentPrescribe(Long id, String sid, String name, Diagnosis diagnosis) {
		this.id = id;
		this.sid = sid;
		this.name = name;
		this.diagnosis = diagnosis;
	}

	public Long getId() {
		return id;
	}

	public String getSid() {
		return sid;
	}

	public Date getDateInj() {
		return dateInj;
	}

	/**
	 * Получить период!
	 *
	 * @return
	 */
	public Date getTruncatedDateInj() {
		return truncatedDateInj;
	}

	public void setDateInj(Date dateInj) {
		this.dateInj = dateInj;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Diagnosis getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(Diagnosis diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	@Override
	public int compareTo(KmpMedicamentPrescribe o) {
		return this.id.compareTo(o.getId());
	}
}
