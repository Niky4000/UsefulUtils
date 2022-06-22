/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import ru.ibs.pmp.api.utils.MessageUtils;

/**
 *
 * @author me
 */
public class MessageUtilsTest2 {

	public static void testMessageUtils() {
		String str = "Warning. Duplicate patients: 992837171 patientType: 1 insuranceNumber: 5754820872000110 caseId: 579305570389 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 992837171 patientType: 1 insuranceNumber: 5754820872000110 caseId: 579305650366 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580334925372 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580337555361 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580337690381 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580339330389 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580340875376 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580358140365 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580374825380 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580405185396 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580405775366 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580412765387 billId: 101651079!\n"
				+ "Warning. Duplicate patients: 995010285 patientType: 1 insuranceNumber: 5754820872000110 caseId: 580418165373 billId: 101651079!";
		for (String subString : str.split("\n")) {
			System.out.println(MessageUtils.parseInvoiceWarningMessageToRussianString(subString));
		}
	}
}
