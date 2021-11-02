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

	@Override
	public int compareTo(UpdateBean o) {
		if (this.getClass().equals(o.getClass())) {
			return this.id.compareTo(o.getId());
		} else if (this instanceof RangeUpdateBean && o instanceof UpdateBean) {
			return -1;
		} else if (this instanceof UpdateBean && o instanceof RangeUpdateBean) {
			return 1;
		} else {
			return super.compareTo(o);
		}
	}
}
