package com.ibs.zzztestapplication2;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.ibs.bean.ComparableBean;
import com.ibs.sortings.MergeSort;
import com.ibs.sortings.QuickSort;
import static com.ibs.utils.StringSimilarity.printSimilarity;
import com.tinkof.CalculateSum;
import com.tinkof.Combination;
import com.tinkof.Combination2;
import com.tinkof.Compression;
import com.tinkof.Filter;
import com.tinkof.FiveLetters;
import com.tinkof.Letters;
import com.tinkof.Pangram;
import com.tinkof.RecursivePrinter;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
//
//		printPar(3);
//		System.out.println(makeChange(50, 25));
//		testSingleThread();
//		testCompareChain();
		// Wed, 29 Mar 2023 14:54:33 GMT
//		System.out.println(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date()));
//		testSimilarity();
//		testCache();
//		System.out.println(LocalDate.of(2022, 12, 12).toString());
//		completableFutureTest();
//		int[] arr = new int[]{3, 2, 1, 4};
//		QuickSort.quickSort(arr, 0, arr.length - 1);
//System.out.println(IntStream.of(arr).mapToObj(i -> Integer.valueOf(i)).collect(Collectors.toList()));		
//		int[] arr = new int[]{1, 5, 8, 10, 12};
//		int[] arr2 = new int[]{2, 3};
//		int[] merge = MergeSort.merge(arr, arr2);
//		System.out.println(IntStream.of(merge).mapToObj(i -> Integer.valueOf(i)).collect(Collectors.toList()));
//		CalculateSum.calk();
//		Pangram.testPangram();
//		Letters.test();
//		Combination.test();
//		List<int[]> generate = Combination2.generate(5, 2);
//		for (int[] r : generate) {
//			System.out.println(r[0] + " " + r[1]);
//		}
//		RecursivePrinter.test();
//		Compression.test();
//		Filter.test();
//		FiveLetters.test();
//		renameFiles(new File("/home/me/Обучающие видео/JAVA – получи Чёрный Пояс!/"));
		getToken("\"{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzZXNzaW9uLWlkIjoiYXN5bmMuc2VydmljZUBydGstZWxlbWVudC5ydSJ9.6N6eAoQjDgyOL8rjIH3hARFFwl3CZ9H7DEb9QfmPfcA\"}\"");
	}

	private static final String TOKEN_HEAD = "{\"token\":\"";

	private static String getToken(String sendPost) {
		int indexOf = sendPost.indexOf(TOKEN_HEAD) + TOKEN_HEAD.length();
		int lastIndexOf = sendPost.indexOf("\"}");
		String token = sendPost.substring(indexOf, lastIndexOf);
		return token;
	}

	private static void testIterable() {
//		class TestList<T> implements Iterable<T> {
//
//			@Override
//			public Iterator iterator() {
//				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//			}
//		}
//		TestList<Object> list = new TestList<>();
//		for (Object : list) {
//
//		}
	}

	private static void renameFiles(File dir) {
		String badPart = "[MEGASLIV.BIZ] ";
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				renameFiles(file);
			}
			rename(file, badPart);
		}
	}

	private static void rename(File file, String badPart) {
		File parentDir = file.getParentFile();
		String fileName = file.getName();
		if (fileName.contains(badPart)) {
			File newFile = new File(parentDir.getAbsolutePath() + File.separator + fileName.replace(badPart, ""));
			file.renameTo(newFile);
		}
	}

	private static void completableFutureTest() throws Exception {
		CompletableFuture<String> supplyAsync = CompletableFuture.<String>supplyAsync(() -> {
			System.out.println("1");
			waitSomeTime(4);
			if (false) {
				throw new RuntimeException();
			}
			return "Hello ";
		}).handleAsync((s, ex) -> {
			if (s != null) {
				return s + "! Hello again! ";
			} else {
				return "Exception occured! ";
			}
		});
		CompletableFuture<String> supplyAsync2 = CompletableFuture.<String>supplyAsync(() -> {
			System.out.println("2");
			waitSomeTime(10);
			return "World";
		});
		CompletableFuture<String> supplyAsync3 = CompletableFuture.<String>supplyAsync(() -> {
			System.out.println("3");
			waitSomeTime(20);
			return "!!!";
		});
		CompletableFuture<String> future = supplyAsync.thenCombineAsync(supplyAsync2, (s1, s2) -> s1 + s2).thenCombine(supplyAsync3, (s1, s2) -> s1 + s2);
//		CompletableFuture<String> future = supplyAsync.thenComposeAsync(k -> supplyAsync2).thenApplyAsync(s -> k + s).thenCombine(supplyAsync3, (s1, s2) -> s1 + s2);
//		String str = future.getNow("No result!");
		String str = future.get();
//		String str = Stream.of(supplyAsync, supplyAsync2, supplyAsync3).map(CompletableFuture::join).collect(Collectors.joining());
		System.out.println(str);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 4, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(Integer.MAX_VALUE));
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		forkJoinPool.invoke(null);
	}

	private static void waitSomeTime(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	private static void testCache() {
		final int TIME_TO_LIVE = 1;
		final com.github.benmanes.caffeine.cache.Cache<String, Integer> cache = Caffeine.<String, Map<String, String>>newBuilder().expireAfterWrite(TIME_TO_LIVE, TimeUnit.HOURS).build();
		createThread(getSomeRunnableForCacheTest(cache), "thread1");
		createThread(getSomeRunnableForCacheTest(cache), "thread2");
		createThread(getSomeRunnableForCacheTest(cache), "thread3");
		createThread(getSomeRunnableForCacheTest(cache), "thread4");
	}

	private static Thread createThread(Runnable runnable, String name) {
		Thread thread = new Thread(runnable);
		thread.setName(name);
		thread.start();
		return thread;
	}

	private static Runnable getSomeRunnableForCacheTest(com.github.benmanes.caffeine.cache.Cache<String, Integer> cache) {
		return () -> {
			computeSomethingInTheCache(cache, "test");
			computeSomethingInTheCache(cache, "test2");
			computeSomethingInTheCache(cache, "test3");
			computeSomethingInTheCache(cache, "test4");
		};
	}

	private static void computeSomethingInTheCache(com.github.benmanes.caffeine.cache.Cache<String, Integer> cache, String key) {
		Integer value = cache.asMap().computeIfAbsent(key, s -> {
			try {
				System.out.println(getCurrentTime() + " " + Thread.currentThread().getName() + ": Waiting...");
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
			}
			System.out.println(getCurrentTime() + " " + Thread.currentThread().getName() + ": Computing...");
			return Double.valueOf(Math.random() * 100).intValue();
		});
		System.out.println(getCurrentTime() + " " + Thread.currentThread().getName() + ": value: " + value + "!");
	}

	private static String getCurrentTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
	}

	public static void testSimilarity() {
		printSimilarity("", "");
		printSimilarity("1234567890", "1");
		printSimilarity("1234567890", "123");
		printSimilarity("1234567890", "1234567");
		printSimilarity("1234567890", "1234567890");
		printSimilarity("1234567890", "1234567980");
		printSimilarity("47/2010", "472010");
		printSimilarity("47/2010", "472011");
		printSimilarity("47/2010", "AB.CDEF");
		printSimilarity("47/2010", "4B.CDEFG");
		printSimilarity("47/2010", "AB.CDEFG");
		printSimilarity("The quick fox jumped", "The fox jumped");
		printSimilarity("The quick fox jumped", "The fox");
		printSimilarity("kitten", "sitting");
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
