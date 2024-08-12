/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.zzztestapplication.interfaces;

/**
 *
 * @author me
 */
public class SecondAndFirst implements FirstInterface, SecondInterface {

	@Override
	public String some(String hello, String hello2) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String someMethod(String str) {
		return SecondInterface.super.someMethod(str); //To change body of generated methods, choose Tools | Templates.
	}

}
