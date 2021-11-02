package ru.ibs.kmplib.bean;

/**
 *
 * @author me
 */
public class UpdateBean implements Comparable<UpdateBean> {

	protected final Long id;
	private final String alert;

	public UpdateBean(Long id, String alert) {
		this.id = id;
		this.alert = alert;
	}

	public Long getId() {
		return id;
	}

	public String getAlert() {
		return alert;
	}

	@Override
	public int compareTo(UpdateBean o) {
		return this.id.compareTo(o.getId());
	}
}
