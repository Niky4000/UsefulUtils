/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tinkof;

import java.util.Arrays;
import java.util.List;

public class CalculateSum {

	public static void calk() {
//		BufferedReader inputDataBR = new BufferedReader(new InputStreamReader(System.in));

		List<Double> persents = Arrays.asList(0.1d, 0.02d);
		List<Double> purchases = Arrays.asList(100d, 300d);

		double sum = 0;
		for (int i = 0; i < purchases.size(); i++) {
			sum = sum + purchases.get(i) * persents.get(i);
		}
		System.out.println(sum);
	}
}
