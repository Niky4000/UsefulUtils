package com.ibs.zzztestapplication2;

import com.ibs.bean.ComparableBean;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SomeClass {

	public static void main(String[] args) {
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
//
//		printPar(3);
//		System.out.println(makeChange(50, 25));
//		testSingleThread();
		testCompareChain();
	}

	private static void testCompareChain() {
		List<ComparableBean> list = Arrays.asList(new ComparableBean(2, "name"), new ComparableBean(0, "name"), new ComparableBean(0, "name"));
		Collections.sort(list);
		System.out.println(list);
	}

	private void methodToSynchronize() {

	}

	private static void testSingleThread() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(getRunnable());
		executor.submit(getRunnable());
		executor.submit(getRunnable());
		System.out.println("testSingleThread!");
		executor.shutdown();
	}

	private static Runnable getRunnable() {
		return () -> {
			try {
				System.out.println("Started!");
				Thread.sleep(4000);
				System.out.println("Working!");
				Thread.sleep(4000);
				System.out.println("Finished!");
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();

			}
		};
	}

	public static int makeChange(int n, int denom) {
		int next_denom = 0;
		switch (denom) {
			case 25:
				next_denom = 10;
				break;
			case 10:
				next_denom = 5;
				break;
			case 5:
				next_denom = 1;
				break;
			case 1:
				return 1;
		}
		int ways = 0;
		for (int i = 0; i * denom <= n; i++) {
			ways += makeChange(n - i * denom, next_denom);
		}
		return ways;
	}

	public static void printPar(int l, int r, char[] str, int count) {
		if (l < 0 || r < l) {
			return; // invalid state
		}
		if (l == 0 && r == 0) {
			System.out.println(str); // found one, so print it
		} else {
			if (l > 0) { // try a left paren, if there are some available
				str[count] = '(';
				printPar(l - 1, r, str, count + 1);
			}
			if (r > l) { // try a right paren, if there’s a matching left
				str[count] = ')';
				printPar(l, r - 1, str, count + 1);
			}
		}
	}

	public static void printPar(int count) {
		char[] str = new char[count * 2];
		printPar(count, count, str, 0);
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
