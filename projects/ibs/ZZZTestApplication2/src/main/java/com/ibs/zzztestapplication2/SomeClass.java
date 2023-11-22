package com.ibs.zzztestapplication2;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.ibs.bean.ComparableBean;
import com.ibs.bean.MyEnum;
import com.ibs.bean.SomeTestStaticClass;
import static com.ibs.utils.StringSimilarity.printSimilarity;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SomeClass {

    private static final String COMMA = ",";

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
//		completableFutureTest2();
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
//		getToken("\"{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzZXNzaW9uLWlkIjoiYXN5bmMuc2VydmljZUBydGstZWxlbWVudC5ydSJ9.6N6eAoQjDgyOL8rjIH3hARFFwl3CZ9H7DEb9QfmPfcA\"}\"");
//		ConfigsHandler.handleAsyncConfigs();
//		ConfigsHandler.handleReportConfigs();
//        ConfigsHandler.handleMpiServiceConfigs();
//		Integer k = 22222;
//		System.out.println(k == 22222);
//		testPutIfAbsent();
//		testMapCompute();
//		testLocalDateToString();
//		Object obj = new Object();
//		obj.hashCode();
//		Set<String> allQueryEventTypesSet = Set.of("4,2,3,5,1,4,4".split(COMMA));
//		System.out.println(allQueryEventTypesSet);
//		testFunctionalInterface();
//		System.out.println(handleName("КИРГИЗИЯ,.: Киргизская Республика"));
//		testMapOfMethod();
//		testCreateLinkedHashMap();
//		tinkovTest();
//		testMap();
//		testSet();
//		testExecutor();
//        testPriorityQueue();
//        int[] arr = new int[]{6, 7, 3, 1, 2, 5, 8, 4, 12, 50, 34, 48, 92, 154, 22, 20};
////        int[] arr = new int[]{6, 7, 3, 1, 2, 5, 8, 4};
////        QuickSort2.sort(arr, 0, arr.length - 1);
//        int[] sort = MergeSort2.sort(arr, 0, arr.length - 1);
//        for (int i = 0; i < sort.length; i++) {
//            System.out.println(sort[i]);
//        }
//        Integer i = 2000000000;
//        Integer j = 2000000000;
//        System.out.println(i * j);
//        Long i = 2000000000000000000L;
//        Long j = 2000000000000000000L;
//        System.out.println(i * j);
//        BigInteger bigInteger = new BigInteger("");
//        BigDecimal bigDecimal = new BigDecimal("");
//        int n = 3;
//        String[] arr = new String[n * 2];
//        createRightParenthese(arr, n, n, 0);
//        int[] arr = new int[]{7, 4, 3, 5, 8, 2, 1, 6};
//        insertionSortImperative(arr);
//        IntStream.of(arr).forEach(i -> System.out.println(i));
//        System.out.println(getAllPossibleCombinations(new int[]{1, 2, 3, 4, 5}));
//        System.out.println(getAllPossibleCombinations(new int[]{1, 2, 3}));
//        System.out.println(getAllPossibleCombinationsForClosestNeighbours(new int[]{1, 2, 3}));
        queueTest();
    }

    private static void queueTest() {
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(1);
        queue.add(2);
        queue.add(3);
        queue.add(4);
        queue.addFirst(200);
        queue.addLast(400);
        System.out.println(queue);
        Integer poll = queue.poll();
        Integer poll2 = queue.poll();
        Integer poll3 = queue.poll();
        Integer poll4 = queue.poll();
        Integer poll5 = queue.pollLast();
        System.out.println(queue);
        MyEnum[] values = MyEnum.values();
        System.out.println(values);
    }

    private static final Set<Integer> allowedNeighbourDifference = Set.of(-1, 0, 1);

    private static ArrayList<ArrayList<Integer>> getAllPossibleCombinationsForClosestNeighbours(int[] arr) {
        ArrayList<ArrayList<Integer>> out = new ArrayList<>();
        ArrayList<Integer> list = new ArrayList<>();
        list.add(arr[0]);
        out.add(list);
        for (int i = 1; i < arr.length; i++) {
            ArrayList<ArrayList<Integer>> addLater = new ArrayList<>();
            for (ArrayList<Integer> subList : out) {
                for (int j = 0; j <= subList.size(); j++) {
                    if (j < subList.size()) {
                        Integer left = subList.get(j - 1 < 0 ? j : j - 1);
                        Integer right = subList.get(j + 1 < subList.size() ? j + 1 : j);
                        if (allowedNeighbourDifference.contains(arr[i] - left) || allowedNeighbourDifference.contains(arr[i] - right)) {
                            ArrayList<Integer> clone = (ArrayList<Integer>) subList.clone();
                            clone.add(j, arr[i]);
                            addLater.add(clone);
                        }
                    } else {
                        Integer left = subList.get(j - 1 < 0 ? j : j - 1);
                        if (allowedNeighbourDifference.contains(arr[i] - left)) {
                            subList.add(arr[i]);
                            addLater.add(subList);
                        }
                        break;
                    }
                }
            }
            out = addLater;
        }
        return out;
    }

    private static ArrayList<ArrayList<Integer>> getAllPossibleCombinations(int[] arr) {
        ArrayList<ArrayList<Integer>> out = new ArrayList<>();
        ArrayList<Integer> list = new ArrayList<>();
        list.add(arr[0]);
        out.add(list);
        for (int i = 1; i < arr.length; i++) {
            ArrayList<ArrayList<Integer>> addLater = new ArrayList<>();
            for (ArrayList<Integer> subList : out) {
                for (int j = 0; j <= subList.size(); j++) {
                    if (j < subList.size()) {
                        ArrayList<Integer> clone = (ArrayList<Integer>) subList.clone();
                        clone.add(j, arr[i]);
                        addLater.add(clone);
                    } else {
                        subList.add(arr[i]);
                        addLater.add(subList);
                        break;
                    }
                }
            }
            out = addLater;
        }
        return out;
    }

    public static void insertionSortImperative(int[] input) {
        for (int i = 1; i < input.length; i++) {
            int key = input[i];
            int j = i - 1;
            while (j >= 0 && input[j] > key) {
                input[j + 1] = input[j];
                j = j - 1;
            }
            input[j + 1] = key;
        }
    }

    private static String arrayToString(int[] arr) {
        return IntStream.range(0, arr.length).map(i -> arr[i]).mapToObj(i -> i + "").reduce("", (d1, d2) -> d1 + " " + d2);
    }

    private static final String l = "(";
    private static final String r = ")";

    private static void createRightParenthese(String[] arr, int left, int right, int index) {
        // ()()
        // (())
        //
        // ((()))
        if (left < 0 || right < 0) {
            return;
        }
        if (left == 0 && right == 0) {
            System.out.println(Stream.of(arr).reduce("", (s1, s2) -> s1 + s2));
        }
        if (left > 0) {
            arr[index] = l;
            createRightParenthese(arr, left - 1, right, index + 1);
        }
        if (right > left) {
            arr[index] = r;
            createRightParenthese(arr, left, right - 1, index + 1);
        }
    }

    private static void testPriorityQueue() {
        AtomicInteger i = new AtomicInteger(0);
        i.incrementAndGet();
        i.incrementAndGet();
        LongAdder longAdder = new LongAdder();
        longAdder.add(1L);
        longAdder.add(1L);
        PriorityQueue<String> priorityQueue = new PriorityQueue<String>();
        priorityQueue.add("123");
        priorityQueue.add("AEZA");
        priorityQueue.add("ZELDA");
        priorityQueue.add("4444");
        priorityQueue.add("XENON");
        priorityQueue.add("ZELDA");
        priorityQueue.add("ZELDA");
        priorityQueue.add("AEZA");
        priorityQueue.add("AEZA");
        while (!priorityQueue.isEmpty()) {
            System.out.println(priorityQueue.poll());
        }
    }

    private static void testExecutor() throws InterruptedException, ExecutionException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>();
        List<String> putIfAbsent = map.putIfAbsent("1", new ArrayList<>());
        List<String> putIfAbsent2 = map.putIfAbsent("1", Arrays.asList("1", "2"));
        List<String> putIfAbsent3 = map.putIfAbsent("1", Arrays.asList("1", "2", "3", "4"));
        ExecutorService threadPoolExecutor2 = Executors.newWorkStealingPool();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 18, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2)) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println(Thread.currentThread().getName() + " is going to be executed!");
                super.beforeExecute(t, r);
            }

            @Override
            protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                return super.newTaskFor(callable); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
                return super.newTaskFor(runnable, value); //To change body of generated methods, choose Tools | Templates.
            }

        };
        int prestartAllCoreThreads = threadPoolExecutor.prestartAllCoreThreads();
        boolean prestartCoreThread = threadPoolExecutor.prestartCoreThread();
        threadPoolExecutor.setCorePoolSize(10);
        int prestartAllCoreThreads2 = threadPoolExecutor.prestartAllCoreThreads();
        threadPoolExecutor.setCorePoolSize(2);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 18; i++) {
                BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
                Future<String> future = threadPoolExecutor.submit(() -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + " started!");
                        Thread.sleep(20000);
                        System.out.println(Thread.currentThread().getName() + " finished!");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    return "Hello!";
                });
                Field workQueueField = ThreadPoolExecutor.class.getDeclaredField("workQueue");
                workQueueField.setAccessible(true);
                BlockingQueue workQueue = (BlockingQueue) workQueueField.get(threadPoolExecutor);
                System.out.println("Task was added! queueSize = " + queue.size() + "! workQueueSize = " + workQueue.size() + "!");
                Thread.sleep(200);
            }
            Thread.sleep(40000);
        }
//		System.out.println(future.get());
        Thread.sleep(40000);
        threadPoolExecutor.shutdown();
    }

    private static void testMap() {
        Map<String, String> treeMap = new TreeMap<>();
        Map<String, String> map0 = new HashMap<>();
        Map<String, String> map = new HashMap<>(4, 1f);
        Map<String, String> map2 = new HashMap<>(4);
        Map<String, String> map3 = new HashMap<>(4, 2f);
        for (int i = 0; i < 8; i++) {
            map0.put(i + "", i + "");
        }
        System.out.println(map0.toString());
    }

    private static void testSet() {
        ArrayDeque<String> arrayDeque = new ArrayDeque<String>();
        Set<String> set = new HashSet<>(4, 1f);
        Set<String> set2 = new HashSet<>(4);
        Set<String> set3 = new HashSet<>(4, 2f);
        for (int i = 0; i < 8; i++) {
            set3.add(i + "");
        }
        System.out.println(set3.toString());
    }

    private static void tinkovTest() {
        Set<Integer> set1 = new HashSet(Arrays.asList(1, 2, 4, 5));
        Set<Integer> set2 = new HashSet(Arrays.asList(3, 3, 4));
        Set<Integer> set3 = new HashSet(Arrays.asList(2, 3, 4, 5, 6));
        set1.retainAll(set2);
        set1.retainAll(set3);
        System.out.println(set1);
    }

    private static void testCreateLinkedHashMap() {
        Map<String, String> additionanFederalRegions = createLinkedHashMap(new String[]{"9", "10"}, "Байконур", "Федеральный округ №");
        additionanFederalRegions.entrySet().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
        Map<String, List<Map.Entry<String, String>>> additionalRussianRegions = createLinkedHashMap(new String[]{"9", "10"}, List.of(new AbstractMap.SimpleEntry<>("55000", "г. Байконур")), List.of(new AbstractMap.SimpleEntry<>("21000", "Донецкая Народная Республика"), new AbstractMap.SimpleEntry<>("43000", "Луганская Народная Республика"), new AbstractMap.SimpleEntry<>("23000", "Запорожская область"), new AbstractMap.SimpleEntry<>("74000", "Херсонская область")));
        additionalRussianRegions.entrySet().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
        Map<String, Integer> map = createLinkedHashMap(new String[]{"9", "10"}, 88, 55);
        Map<String, SomeClass> map2 = createLinkedHashMap(new String[]{"9", "10"}, new SomeClass(), new SomeClass());
        map.entrySet().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }

    private static <T> Map<String, T> createLinkedHashMap(String[] key, T... value) {
        if (key.length != value.length) {
            throw new RuntimeException("createLinkedHashMap: key.length != value.length!");
        }
        Map<String, T> map = new LinkedHashMap<>(key.length);
        for (int i = 0; i < key.length; i++) {
            map.put(key[i], value[i]);
        }
        return map;
    }

    private static void testMapOfMethod() {
        Map<String, String> map = Map.of("1", "A", "2", "B", "3", "C", "4", "D", "5", "E", "6", "F", "7", "G", "8", "H", "9", "I", "10", "J");
        map.values().forEach(System.out::println);
        System.out.println("----------");
        map.entrySet().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }

    private static void testStaticClasses() {
        SomeTestStaticClass someTestStaticClass = new SomeTestStaticClass();
//		SomeTestStaticClass.SomeInternalClass2 someInternalClass2 = someTestStaticClass.new SomeInternalClass2();
//		SomeTestStaticClass.SomeInternalClass someInternalClass = new SomeTestStaticClass.SomeInternalClass();
    }

    private static String handleName(String name) {
        return Stream.of(name.split(" ")).filter(str -> !isCapitalized(str)).reduce((s1, s2) -> s1 + " " + s2).get();
    }

    private static boolean isCapitalized(String str) {
        String replaceAll = Pattern.compile("[^А-Яа-я]").matcher(str).replaceAll("");
        return Pattern.compile("^[А-Я]+$").matcher(replaceAll).matches();
    }

    private static void testCollections() {
        CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<String>();
        CopyOnWriteArraySet<String> copyOnWriteArraySet = new CopyOnWriteArraySet<String>();
        ConcurrentSkipListMap<String, String> concurrentSkipListMap = new ConcurrentSkipListMap<String, String>();
        ConcurrentSkipListSet<String> concurrentSkipListSet = new ConcurrentSkipListSet<String>();
        TreeSet<String> treeSet = new TreeSet<String>();
        ArrayDeque<String> arrayDeque = new ArrayDeque<String>();
        ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
        ConcurrentLinkedDeque concurrentLinkedDeque = new ConcurrentLinkedDeque();
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(10);
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<String>();
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<String, String>();
    }

    private static void testFunctionalInterface() throws IOException {
        ClassFunctionException io = (s1, s2, s3) -> {
            File file = new File(s1);
            if (file.exists()) {
                file.delete();
            }
            Files.write(file.toPath(), (s2 + s3).getBytes(), StandardOpenOption.CREATE_NEW);
            return "";
        };
        io.getSomething("/tmp/someFile", "Hello ", "World!!!");
    }

    private static void testLocalDateToString() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
        Instant instant = Instant.now();
        System.out.println(instant.toString());
    }

    private static void testMapCompute() {
        Map<String, List<Integer>> map = new HashMap<>();
        List list1 = map.compute("he", (key, valueList) -> {
            List list = valueList != null ? valueList : new ArrayList<>();
            list.add(0);
            list.add(2);
            return list;
        });
        List list2 = map.compute("he", (key, valueList) -> {
            List list = valueList != null ? valueList : new ArrayList<>();
            list.add(4);
            list.add(8);
            return list;
        });
        List<Integer> list3 = map.computeIfAbsent("he", key -> {
            return new ArrayList<>();
        });
        List<Integer> list4 = map.computeIfAbsent("te", key -> {
            List<Integer> list = new ArrayList<>();
            list.add(8888);
            return list;
        });
        System.out.println(list1 == list2);
        System.out.println(list1 == list3);
        System.out.println(list4);
        System.out.println(map);
    }

    private static void testPutIfAbsent() {
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("te", new ArrayList());
        List list_ = map.get("te");
        list_.add(new Object());
        list_.add(new Object());
        List list2_ = map.put("te", new ArrayList());
        System.out.println(list_ == list2_);
        map.putIfAbsent("he", new ArrayList<>());
        List<Integer> list = map.get("he");
        list.add(4);
        list.add(8);
        map.putIfAbsent("he", new ArrayList<>());
        List<Integer> list2 = map.get("he");
        list.add(2);
        list.add(0);
        System.out.println(list == list2);
        System.out.println(list);
        System.out.println(map);
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
//		CompletableFuture<String> future = supplyAsync.thenCombineAsync(supplyAsync2, (s1, s2) -> s1 + s2).thenCombine(supplyAsync3, (s1, s2) -> s1 + s2);
        CompletableFuture<String> future = supplyAsync.thenComposeAsync(k -> supplyAsync2).thenCombine(supplyAsync3, (s1, s2) -> s1 + s2);
//		String str = future.getNow("No result!");
        String str = future.get();
//		String str = Stream.of(supplyAsync, supplyAsync2, supplyAsync3).map(CompletableFuture::join).collect(Collectors.joining());
        System.out.println(str);
//		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 4, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(Integer.MAX_VALUE));
//		ForkJoinPool forkJoinPool = new ForkJoinPool();
//		forkJoinPool.invoke(null);
    }

    private static void completableFutureTest2() throws Exception {
        CompletableFuture<String> supplyAsync = CompletableFuture.<String>supplyAsync(() -> {
            System.out.println("++++");
            waitSomeTime(8);
            return "Hello!";
        });
//		CompletableFuture<String> supplyAsync2 = CompletableFuture.<String>supplyAsync(() -> {
//			waitSomeTime(4);
//			System.out.println("----");
//			return "World!";
//		});
//		CompletableFuture<String> thenCombine = supplyAsync.thenCombine(supplyAsync2, (s1, s2) -> s1 + " " + s2);
        CompletableFuture<String> thenCompose = supplyAsync.thenCompose(s -> {
            return CompletableFuture.<String>supplyAsync(() -> {
                waitSomeTime(4);
                System.out.println("----");
                return s + " World!";
            });
        });
        System.out.println(thenCompose.get());
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
