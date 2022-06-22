package ru.ibs.rate.limiter;

import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;

public class Strings {

	public static String lenientFormat(
		String template, Object... args) {
		template = String.valueOf(template);
		if (args == null) {
			args = new Object[]{"(Object[])null"};
		} else {
			for (int i = 0; i < args.length; i++) {
				args[i] = lenientToString(args[i]);
			}
		}
		StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
		int templateStart = 0;
		int i = 0;
		while (i < args.length) {
			int placeholderStart = template.indexOf("%s", templateStart);
			if (placeholderStart == -1) {
				break;
			}
			builder.append(template, templateStart, placeholderStart);
			builder.append(args[i++]);
			templateStart = placeholderStart + 2;
		}
		builder.append(template, templateStart, template.length());
		if (i < args.length) {
			builder.append(" [");
			builder.append(args[i++]);
			while (i < args.length) {
				builder.append(", ");
				builder.append(args[i++]);
			}
			builder.append(']');
		}
		return builder.toString();
	}

	private static String lenientToString(Object o) {
		if (o == null) {
			return "null";
		}
		try {
			return o.toString();
		} catch (Exception e) {
			String objectToString = o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
			Logger.getLogger("com.google.common.base.Strings")
				.log(WARNING, "Exception during lenientFormat for " + objectToString, e);
			return "<" + objectToString + " threw " + e.getClass().getName() + ">";
		}
	}
}
