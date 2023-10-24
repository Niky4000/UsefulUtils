package com.ibs.bean;

import com.ibs.bean.SomeTestStaticClass.SomeInternalClass;
import com.ibs.bean.SomeTestStaticClass.SomeInternalClass2;

public class SomeClass2 extends SomeTestStaticClass {

	public void doSomething() {
		SomeInternalClass someInternalClass = new SomeInternalClass();
		SomeInternalClass2 someInternalClass2 = new SomeInternalClass2();
	}
}
