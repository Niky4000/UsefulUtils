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
public class Bean1 implements Interface1, Interface2 {

	@Override
	public void doSomething() {
		Interface1.super.doSomething();
		Interface2.super.doSomething();
	}
}
