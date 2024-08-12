package ru.ibs.kmplib.request.bean;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author me
 */
public class CacheKey<T> {

	private final Date created;
	private final T value;

	public CacheKey(T value) {
		this.created = new Date();
		this.value = value;
	}

	public Date getCreated() {
		return created;
	}

	public T getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + Objects.hashCode(this.value);
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
		final CacheKey<?> other = (CacheKey<?>) obj;
		if (!Objects.equals(this.value, other.value))
			return false;
		return true;
	}
}
