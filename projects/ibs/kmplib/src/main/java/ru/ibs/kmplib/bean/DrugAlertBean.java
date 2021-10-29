package ru.ibs.kmplib.bean;

import ru.ibs.kmplib.request.bean.Drugs;

/**
 *
 * @author me
 */
public class DrugAlertBean extends Drugs {

	private final String alert;

	public DrugAlertBean(Drugs drugs, String alert) {
		super(drugs.getSchedule(), drugs.isScreen(), drugs.getType(), drugs.getCode(), drugs.getName());
		this.alert = alert;
	}

	public String getAlert() {
		return alert;
	}
}
