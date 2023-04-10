package com.ibs.bean;

import com.ibs.utils.CompareChain;

public class ComparableBean implements Comparable<ComparableBean> {

	private final int id;
	private final String name;

	public ComparableBean(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(ComparableBean obj) {
		return CompareChain.compare(this, obj, ComparableBean::getId,ComparableBean::getName);
	}

	@Override
	public String toString() {
		return "ComparableBean{" + "id=" + id + ", name=" + name + '}';
	}
}
