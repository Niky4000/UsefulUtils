package ru.ibs.testmultithreading.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.ibs.testmultithreading.utils.ThreadUtils;

public class CollectionTests {

	public void testCopyOnWrite() {
		CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
		ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 20; i++) {
				String str = "Hello " + i + "!";
				list.add(str);
				System.out.println("Thread: " + str);
				ThreadUtils.waitSomeTime(1);
			}
		});
		ThreadUtils.waitSomeTime(4);
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			ThreadUtils.waitSomeTime(2);
			System.out.println(iterator.next());
		}
	}

	public void testCopyOnWrite2() {
		CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();
		ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 20; i++) {
				String str = "Hello " + i + "!";
				set.add(str);
				System.out.println("Thread: " + str);
				ThreadUtils.waitSomeTime(1);
			}
		});
		ThreadUtils.waitSomeTime(4);
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			ThreadUtils.waitSomeTime(2);
			System.out.println(iterator.next());
		}
	}

	public void testConcurrentHashMap() {
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		Thread thread = ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 20; i++) {
				String str = "Hello " + i + "!";
				map.put(str, str);
				System.out.println("Thread: " + str);
				ThreadUtils.waitSomeTime(1);
			}
		});
		ThreadUtils.waitSomeTime(4);
		Iterator<String> iterator = map.keySet().iterator();
		List<String> list = new ArrayList<>();
		while (iterator.hasNext()) {
			ThreadUtils.waitSomeTime(2);
			String next = iterator.next();
			System.out.println(next);
			list.add(next);
			iterator.remove();
		}
		ThreadUtils.join(thread);
		Collections.sort(list);
		System.out.println("list size = " + list.size() + "!");
		System.out.println("list = " + list.toString() + "!");
		System.out.println("map size = " + map.size() + "!");
		System.out.println("map = " + map.toString() + "!");
	}

	public void testConcurrentHashMap2() {
		ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
		Thread thread = ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 20; i++) {
				map.put(i, i);
				System.out.println("Thread: " + i);
				ThreadUtils.waitSomeTime(1);
			}
		});
		ThreadUtils.waitSomeTime(4);
		map.remove(4);
//		map.computeIfAbsent(1, k -> {
//			return null;
//		});
		Thread thread2 = ThreadUtils.createStoppedThread(() -> {
			map.compute(4, (k, v) -> {
				System.out.println("Thread 222!");
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread 222 finished!");
				return k * 10 + (v != null ? v : 80000);
			});
		});
		Thread thread3 = ThreadUtils.createStoppedThread(() -> {
			map.compute(4, (k, v) -> {
				System.out.println("Thread 444!");
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread 444 finished!");
				return k * 100 + (v != null ? v : 20000);
			});
		});
		thread2.start();
		thread3.start();
		ThreadUtils.join(thread2);
		ThreadUtils.join(thread3);
		System.out.println("Returned value: " + map.get(4));
	}

	public void testConcurrentHashMap3() {
		ConcurrentSkipListMap<Integer, Integer> map = new ConcurrentSkipListMap<>();
		TreeMap<Integer, Integer> treeMap = new TreeMap<>();
		for (int i = 10; i <= 20; i++) {
			map.put(i, i);
			System.out.println("Thread: " + i);
		}
		map.remove(12);
		map.merge(12, 1212, (k, v) -> {
			return k + v;
		});
		Map.Entry<Integer, Integer> ceilingEntry = map.ceilingEntry(5);
		Integer ceilingKey = map.ceilingKey(5);
		Map.Entry<Integer, Integer> floorEntry = map.floorEntry(80);
		Integer floorKey = map.floorKey(80);
		ConcurrentNavigableMap<Integer, Integer> headMap = map.headMap(14);
		ConcurrentNavigableMap<Integer, Integer> tailMap = map.tailMap(18);
		Map.Entry<Integer, Integer> higherEntry = map.higherEntry(17);
		Map.Entry<Integer, Integer> lowerEntry = map.lowerEntry(11);
		Thread thread1 = ThreadUtils.createStoppedThread(() -> {
			map.merge(10, 1000, (k, v) -> {
				System.out.println("Thread 222!");
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread 222 finished! k = " + k + " v = " + v);
				return k + v;
			});
		});
		Thread thread2 = ThreadUtils.createStoppedThread(() -> {
			map.merge(10, 2000, (k, v) -> {
				System.out.println("Thread 444!");
				ThreadUtils.waitSomeTime(4);
				System.out.println("Thread 444 finished! k = " + k + " v = " + v);
				return k + v;
			});
		});
		thread1.start();
		thread2.start();
		ThreadUtils.join(thread1);
		ThreadUtils.join(thread2);
		System.out.println("Value = " + map.get(10));
	}

	public void testConcurrentHashSet() {
		ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<>();

	}

	public void testConcurrentQueue() throws InterruptedException {
		ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<>();
		ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
		ConcurrentLinkedDeque<Integer> deque = new ConcurrentLinkedDeque<>();
		ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(5);
		for (int i = 0; i < 6; i++) {
			System.out.println(arrayBlockingQueue.offer(i));
		}
	}

	public void testConcurrentQueue2() throws InterruptedException {
		LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue();
		for (int i = 0; i < 200; i++) {
			System.out.println(queue.offer(i));
		}
	}

	public void testConcurrentQueue3() throws InterruptedException {
		PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue();
		for (int i = 0; i < 200; i++) {
			System.out.println(queue.offer(i));
		}
	}

	public void testConcurrentQueue4() throws InterruptedException {
		SynchronousQueue<Integer> queue = new SynchronousQueue<>();
		Thread thread1 = ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 10; i++) {
				while (true) {
					try {
						System.out.println(i + " was put!");
						queue.put(i);
						break;
					} catch (InterruptedException ex) {
						continue;
					}
				}
			}
		});
		Thread thread2 = ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 10; i++) {
				while (true) {
					try {
						System.out.println(queue.take() + " was taken!");
						break;
					} catch (InterruptedException ex) {
						continue;
					}
				}
			}
		});
		thread1.join();
		thread2.join();
	}

	public void testConcurrentQueue5() throws InterruptedException {
		LinkedTransferQueue<Integer> queue = new LinkedTransferQueue<>();
		queue.put(1000);
		queue.put(2000);
		queue.put(3000);
		queue.put(4000);
		Thread thread1 = ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 10; i++) {
				while (true) {
					try {
						System.out.println(i + " was put!");
						queue.transfer(i);
						break;
					} catch (InterruptedException ex) {
						continue;
					}
				}
			}
		});
		Thread thread2 = ThreadUtils.createThread(() -> {
			for (int i = 1; i <= 14; i++) {
				while (true) {
					try {
						ThreadUtils.waitSomeTime(4);
						System.out.println(queue.take() + " was taken!");
						break;
					} catch (InterruptedException ex) {
						continue;
					}
				}
			}
		});
		thread1.join();
		thread2.join();
	}
}
