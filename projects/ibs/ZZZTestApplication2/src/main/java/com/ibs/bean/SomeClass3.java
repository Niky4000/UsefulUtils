package com.ibs.bean;

import com.ibs.bean.SomeTestStaticClass.SomeInternalClass;
import com.ibs.bean.SomeTestStaticClass.SomeInternalClass2;

public class SomeClass3 {

	public void doSomething() {
		SomeInternalClass someInternalClass = new SomeInternalClass();
		SomeTestStaticClass someTestStaticClass = new SomeTestStaticClass();
		SomeInternalClass2 someInternalClass2 = someTestStaticClass.new SomeInternalClass2();
	}
}
