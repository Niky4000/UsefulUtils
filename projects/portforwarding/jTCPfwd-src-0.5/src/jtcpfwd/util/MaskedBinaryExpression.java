package jtcpfwd.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A {@link BinaryExpression} that uses the secondary name "mask" in non-random
 * mode to specify a bit mask that is ANDed with the expression to test.
 * Optional bytes at the end of the mask mean that the string to match may also
 * be shorter. Optional bytes at other positions are invalid.
 * 
 * <p>
 * Alternatively, this class can match based on a regular expression, by using
 * <tt>regex:<i>someRegex</i></tt> as the syntax. No additional transforms are
 * parsed in this case.
 */
public class MaskedBinaryExpression {

	private final Pattern regex;
	private final byte[] bytes, mask;
	private final int minLength;

	/**
	 * Create a new masked binary expression.
	 */
	public MaskedBinaryExpression(String expression) throws IOException {
		if (expression.startsWith("regex:")) {
			regex = Pattern.compile(expression.substring(6));
			minLength = -1;
			bytes = mask = null;
		} else {
			BinaryExpression binex = new BinaryExpression(expression, "mask", false);
			bytes = binex.getValue();
			byte[] maskTemp = binex.getSecondaryValue();
			if (maskTemp == null) {
				mask = new byte[bytes.length];
				Arrays.fill(mask, (byte) 0xff);
			} else if (maskTemp.length < bytes.length) {
				mask = new byte[bytes.length];
				System.arraycopy(maskTemp, 0, mask, 0, maskTemp.length);
				Arrays.fill(mask, maskTemp.length, mask.length, (byte) 0xff);
			} else if (maskTemp.length > bytes.length) {
				throw new IOException("Mask length " + maskTemp.length + " is larger than value length " + bytes.length);
			} else {
				mask = maskTemp;
			}
			boolean[] markedBytes = binex.getMarkedSecondaryBytes();
			int minLengthTemp = bytes.length;
			if (markedBytes != null) {
				minLengthTemp = markedBytes.length;
				for (int i = 0; i < markedBytes.length; i++) {
					if (markedBytes[i]) {
						minLengthTemp = Math.min(minLengthTemp, i);
					} else if (minLengthTemp != markedBytes.length) {
						throw new IOException("Mandatory bytes after optional bytes are not supported!");
					}
				}
			}
			minLength = minLengthTemp;
			regex = null;
		}
	}

	/**
	 * Test whether this expression matches the given bytes.
	 */
	public boolean matches(byte[] value) throws IOException {
		if (regex != null) {
			return regex.matcher(new String(value, "ISO-8859-1")).matches();
		}
		if (value.length > bytes.length || value.length < minLength)
			return false;
		for (int i = 0; i < value.length; i++) {
			if ((value[i] & mask[i]) != (bytes[i] & mask[i]))
				return false;
		}
		return true;
	}

	public byte[] getSampleValue() {
		if (regex != null)
			throw new IllegalStateException("Sample value for regex not supported");
		return bytes;
	}
}
