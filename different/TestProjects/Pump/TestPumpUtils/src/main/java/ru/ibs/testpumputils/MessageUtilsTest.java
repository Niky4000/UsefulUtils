package ru.ibs.testpumputils;

import ru.ibs.pmp.api.utils.MessageUtils;

/**
 *
 * @author me
 */
public class MessageUtilsTest {

	public static void testMessageUtils() {
		System.out.println(MessageUtils.parseInvoiceWarningMessageToRussianString("This bill should be recreated! It will contain services for 946846.71 rubles more."));
	}
}
