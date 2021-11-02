package ru.ibs.kmplib.bean;

/**
 *
 * @author me
 */
public class RangeUpdateBean extends UpdateBean {

	private final Long id2;

	public RangeUpdateBean(Long id, Long id2, String alert) {
		super(id, alert);
		this.id2 = id2;
	}

	public Long getId2() {
		return id2;
	}
}
