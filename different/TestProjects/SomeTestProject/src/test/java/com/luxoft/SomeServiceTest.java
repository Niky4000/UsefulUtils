/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author me
 */
public class SomeServiceTest {

	@Test
	public void testSomeService() throws InterruptedException {
		int elementsCount = 4;
		SomeServiceImpl<Integer> someServiceImpl = new SomeServiceImpl<>(elementsCount);
		someServiceImpl.push(1);
		someServiceImpl.push(2);
		someServiceImpl.push(3);
		someServiceImpl.push(8);
		someServiceImpl.push(8);
		someServiceImpl.push(8);
		someServiceImpl.push(8);
		someServiceImpl.push(8);
		someServiceImpl.push(8);
		someServiceImpl.push(8);
		someServiceImpl.push(4);
		someServiceImpl.push(5);
		Collection<Integer> list = someServiceImpl.top();
		ArrayList<Integer> arrayList = new ArrayList<>(list);
		Assert.assertTrue(list.size() == elementsCount);
		Assert.assertTrue(arrayList.get(3).equals(8) && arrayList.get(2).equals(5));
	}
}
