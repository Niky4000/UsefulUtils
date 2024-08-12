package ru.ibs.utils.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ImmutableMap не поддерживает null-values! Данная реализация по использованию
 * похожа, но поддерживает null-values!
 *
 * @author me
 */
public class MapBuilder<K, V> {

	private Map<K, V> map;

	private MapBuilder() {
		map = new HashMap<>();
	}

	private MapBuilder(Supplier<Map<K, V>> mapSupplier) {
		map = mapSupplier.get();
	}

	public static <K, V> MapBuilder<K, V> builder() {
		return new MapBuilder<K, V>();
	}

	public static <K, V> MapBuilder<K, V> builder(Supplier<Map<K, V>> mapSupplier) {
		return new MapBuilder<K, V>(mapSupplier);
	}

	public MapBuilder<K, V> put(K key, V value) {
		map.put(key, value);
		return this;
	}

	public Map<K, V> build() {
		return map;
	}
}
