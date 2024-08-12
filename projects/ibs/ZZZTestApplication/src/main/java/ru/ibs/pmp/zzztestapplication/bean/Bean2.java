/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.zzztestapplication.bean;

/**
 *
 * @author me
 */
public class Bean2 {

	static int i = 0;
	int k = 0;

	{
		i = 22;
		k = 10;
	}

	public Bean2() {
		i = 88;
		k = 20;
	}

	static {
		i = 44;
	}

	static class SubBean21 {

		public void doSomethingAgain() {
			i = 80;
//			k = 20;
		}
	}

	class SubBean22 {

		public void doSomethingAgain() {
			i = 80;
			k = 20;
			Bean2.this.k = 80;
		}
	}
}
