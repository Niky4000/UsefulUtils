/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author me
 */
public class SomeServiceImpl<T extends Comparable> implements SomeService<T> {

	protected final int maxValuesToReturn;
	protected int threshold;
	private ConcurrentSkipListSet<T> collection = new ConcurrentSkipListSet<>();
	private Object obj;

	public SomeServiceImpl(int maxValuesToReturn) {
		this.maxValuesToReturn = maxValuesToReturn;
	}

	@Override
	public void push(T val) {
		if (collection.add(val) && collection.size() > maxValuesToReturn) {
			T lastElement = collection.pollFirst();
		}
		synchronized (obj) {
			obj.notifyAll();
		}
	}

	@Override
	public Collection<T> top() throws InterruptedException {
		synchronized (obj) {
			while (collection.size() < maxValuesToReturn) {
				obj.wait();
			}
		}
		return new ArrayList<>(collection);
	}

}
