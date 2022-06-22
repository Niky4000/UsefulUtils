package ru.ibs.pmp.api.help.model;

import java.io.Serializable;

/**
 * Created by PARFENOV on 25.03.2017.
 */
public class GridSysMessageRequest implements Serializable {

	private Integer offset;
	private Integer size;

	public GridSysMessageRequest() {
	}

	public GridSysMessageRequest(Integer offset, Integer size) {
		this.offset = offset;
		this.size = size;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
}
