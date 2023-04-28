package com.ibs.zzztestapplication2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ConfigsHandler {

	public static void handleAsyncConfigs() throws IOException {
		Map<String, String> properties = loadPropertiesAsMap(new String(Files.readAllBytes(new File("/home/me/GIT/ferzl/ferzlConfigs/async-service.properties").toPath())));
		Map<String, String> configs = getConfigsFromYml(new String(Files.readAllBytes(new File("/home/me/GIT/ferzl/async-service/src/main/resources/application.yml").toPath())));
		handleConfigs(configs, properties);
	}

	public static void handleReportConfigs() throws IOException {
		Map<String, String> properties = loadPropertiesAsMap(new String(Files.readAllBytes(new File("/home/me/GIT/ferzl/ferzlConfigs/mpi_reporter.properties").toPath())));
		Map<String, String> configs = getConfigsFromYml(new String(Files.readAllBytes(new File("/home/me/GIT/ferzl/mpi-reporter/src/main/resources/application.yml").toPath())));
		handleConfigs(configs, properties);
	}

	private static Map<String, String> loadPropertiesAsMap(String propertiesStr) {
		String[] split = propertiesStr.split("\n");
		Map<String, String> configs = new LinkedHashMap<>(split.length);
		for (String str_ : split) {
			if (str_.trim().length() == 0 || str_.trim().startsWith("#")) {
				continue;
			}
			String[] str2 = str_.split("=");
			configs.put(str2[0], str2[1]);
		}
		return configs;
	}

	private static void handleConfigs(Map<String, String> configs, Map<String, String> properties) {
//		properties.forEach((name, value) -> {
//			if (!configs.containsKey(name)) {
//				System.out.println(name + "=" + value);
//			}
//		});
		configs.forEach((name, value) -> {
			if (!properties.containsKey(name)) {
				System.out.println(name + "=" + value);
			}
		});
	}

	private static Map<String, String> getConfigsFromYml(String yml) {
		String[] split = yml.split("\n");
		Map<String, String> configs = new LinkedHashMap<>(split.length);
		LinkedList<String> previousConfigs = new LinkedList<>();
		for (String str_ : split) {
			if (str_.trim().length() == 0 || str_.trim().startsWith("#")) {
				continue;
			}
			String[] str2 = str_.split(":");
			String name = str2[0].trim();
			String value = str2.length >= 2 ? str2[1].trim().replace("${", "") : null;
			String defaultValue = str2.length == 3 ? str2[2].trim().replace("}", "") : null;
			int configLevel = getConfigLevel(str2[0]);
//			System.out.println(str2[0]);
			if (configLevel < previousConfigs.size()) {
				previousConfigs.set(configLevel, name);
			} else {
				previousConfigs.add(name);
			}
			Iterator<String> iterator = previousConfigs.descendingIterator();
			while (previousConfigs.size() - 1 > configLevel) {
				iterator.next();
				iterator.remove();
			}
			if (value != null) {
				configs.put(previousConfigs.stream().reduce((s1, s2) -> s1 + "." + s2).get(), defaultValue != null ? defaultValue : value);
			}
		}
		return configs;
	}

	private static char[] SPACE = "  ".toCharArray();

	private static int getConfigLevel(String str) {
		int countNulls = 0, countOnes = 0;
		char[] toCharArray = str.toCharArray();
		int level = 0;
		for (int i = 0; i < toCharArray.length; i += SPACE.length) {
			boolean equal = true;
			for (int j = 0; j < SPACE.length; j++) {
				if (SPACE[j] != toCharArray[i]) {
					equal = false;
				}
			}
			if (equal) {
				level++;
			} else {
				break;
			}
		}
		return level;
	}

	private static void swap(int a, int b) {
		a = a ^ b;
		b = a ^ b;
		a = a ^ b;
		System.out.println("a=" + a + " b=" + b + "!");
	}
}
