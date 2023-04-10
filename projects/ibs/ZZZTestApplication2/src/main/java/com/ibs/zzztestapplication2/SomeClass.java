package com.ibs.zzztestapplication2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SomeClass {

	public static void main(String[] args) throws Exception {
		//		System.out.println(nh(12345678.22d));
//		System.out.println(nh(18.22d));
//		System.out.println(nh(8.22d));
//		System.out.println(nh(2.2d));
//		System.out.println(nh(0d));
//		int[] a = new int[10];
//		arrayMethod(a);
//		System.out.println(a.length);
//		printArray(a);
//		Java java = new Java();
//		java.subject("Hi");
//		java.subject(20);
//		List<Integer> list = new ArrayList<>();
//		list.add(10);
//		listMethod(list);
//		System.out.println(list);
//		int[] b = new int[10];
//		b[0] = 10;
//		arrayMethod2(b);
//		printArray(b);
//		handleBooks();
//		swap(40, 50);
//		handleAsyncConfigs();
		String headerAuthorization = System.getenv("HEADER_AUTHORIZATION");
		System.out.println(headerAuthorization);
	}

	private static void handleAsyncConfigs() throws IOException {
		Map<String, String> properties = loadPropertiesAsMap(new String(Files.readAllBytes(new File("/home/me/GIT/ferzl/ferzlConfigs/async-service.properties").toPath())));
		Map<String, String> configs = getConfigsFromYml(new String(Files.readAllBytes(new File("/home/me/GIT/ferzl/async-service/src/main/resources/application.yml").toPath())));
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

	private static void handleBooks() {
		File bookPath = new File("/home/me/Downloads/Светик книжки 2023/");
		Map<String, List<File>> map = new TreeMap<>(Stream.of(bookPath.listFiles()).collect(Collectors.groupingBy(file -> removeExtension(file.getName()))));
		for (Entry<String, List<File>> entry : map.entrySet()) {
			String fileName = entry.getKey();
			List<File> fileList = entry.getValue();
			deleteFilesIfSuchFilesExist(fileList, "pdf", list1 -> deleteFilesIfSuchFilesExist(list1, "epub", list2 -> deleteFilesIfSuchFilesExist(list2, "fb2", list3 -> deleteFilesIfSuchFilesExist(list3, "mobi", null))));
			System.out.println(fileName + " " + fileList.size() + "!");
		}
	}

	private static void deleteFilesIfSuchFilesExist(List<File> fileList, String extension, Consumer<List<File>> next) {
		if (fileList.size() > 1) {
			boolean doesSuchExtensionExist = isExtension(fileList, extension);
			if (doesSuchExtensionExist) {
				Iterator<File> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = iterator.next();
					if (!extension.equals(getExtension(file.getName()))) {
						file.delete();
						iterator.remove();
					}
				}
			} else if (next != null) {
				next.accept(fileList);
			}
		}
	}

	private static boolean isExtension(List<File> fileList, String extension) {
		return fileList.stream().anyMatch(file -> extension.equals(getExtension(file.getName())));
	}

	private static String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
	}

	private static String removeExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	public static void listMethod(List<Integer> list) {
		List<Integer> list2 = new ArrayList<>();
		list.add(20);
		list = list2;
		list.add(30);
		list.remove(Integer.valueOf(10));
	}

	private static void printArray(int[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}

	public static class Java<S> {

		public void subject(S arg) {
			System.out.println(arg);
		}
	}

	public static void arrayMethod2(int[] a) {
		a[1] = 20;
		a[2] = 30;
	}

	public static void arrayMethod(int[] a) {
		int[] b = new int[5];
		a = b;
	}

	private static final String zeroEnding = ".0";
	private static final String dot = ".";
	private static final DecimalFormat df = new DecimalFormat("#0.00");

	public static String nh(Number n) {
		if (n != null) {
			String str = df.format(n);
			if (str.endsWith(zeroEnding)) {
				return addSpacesBetweenThreeDigits(str.substring(0, str.length() - zeroEnding.length()));
			} else if (str.contains(dot)) {
				if (str.substring(str.indexOf(dot), str.length()).length() < 3) {
					return addSpacesBetweenThreeDigits(str + "0");
				} else {
					return addSpacesBetweenThreeDigits(str);
				}
			} else {
				return addSpacesBetweenThreeDigits(str);
			}
		} else {
			return "";
		}
	}

	public static String addSpacesBetweenThreeDigits(String digitAsString) {
		String digitAsStringWithoutSpaces = removeSpaces(digitAsString);
		StringBuilder sb = new StringBuilder();
		String addLater = "";
		int indexFrom = digitAsStringWithoutSpaces.length() - 1;
		if (digitAsStringWithoutSpaces.contains(".")) {
			addLater = digitAsStringWithoutSpaces.substring(digitAsStringWithoutSpaces.indexOf("."));
			indexFrom = digitAsStringWithoutSpaces.indexOf(".") - 1;
		}
		for (int i = 0; i <= indexFrom; i++) {
			sb.append(digitAsStringWithoutSpaces.charAt(indexFrom - i));
			if ((i + 1) % 3 == 0 && i != indexFrom) {
				sb.append(" ");
			}
		}
		sb.reverse();
		sb.append(addLater);
		return sb.toString();
	}

	private static String removeSpaces(String digitAsString) {
		return digitAsString.replace(" ", "");
	}
}
