package jtcpfwd.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * An expression that can be used to build or match binary byte arrays.
 * 
 * <p>
 * This is useful when binary data (like encryption keys) should be configurable
 * in a text file, where the user can decide which format (hex, base64, etc.) he
 * wants to use to provide the data.
 * 
 * <p>
 * Therefore, the user can give a list of transformations (separated by colons),
 * followed by data to be transformed. Each transformation is applied on the
 * result of the previous transformation. Therefore,
 * <tt>hex:base64:NzQ2NTczNzQ=</tt> will result in <tt>test</tt>.
 * 
 * <p>
 * An empty transformation name will stop parsing transformations; therefore, to
 * match the literal (UTF-8) bytes <tt>C:\&gt;</tt>, the expression
 * <tt>:C:\&gt;</tt> can be used. If no encoding is given, everything is encoded
 * as UTF-8. An explicit encoding can be given by using the <tt>encode-</tt>
 * <i>&lt;encoding&gt;</i><tt>:</tt> transformation.
 * 
 * <p>
 * Optionally, a secondary expression can be parsed at the same time, for
 * example an initialization vector for an encryption key or a mask for matching
 * data. In case of a mask, {@link MaskedBinaryExpression} might have been the
 * better choice.
 * 
 * <p>
 * The following tranformations are supported:
 * 
 * <dl>
 * <dt><b><tt>hex</tt></b></dt>
 * <dd>decode hexadecimal, like <tt>hex:444a32</tt> -&gt; <tt>DJ2</tt></dd>
 * <dt><b><tt>base64</tt></b></dt>
 * <dd>decode base64, like <tt>base64:SGVsbG8=</tt> -&gt; <tt>Hello</tt></dd>
 * <dt><b><tt>sha1</tt></b></dt>
 * <dd>Encode SHA1, like <tt>sha1:test</tt> -&gt;
 * <tt>hex:a94a8fe5ccb19ba61c4c0873d391e987982fbbd3</tt></dd>
 * <dt><b><tt>md5</tt></b></dt>
 * <dd>Encode MD5, like <tt>md5:test</tt> -&gt;
 * <tt>hex:098f6bcd4621d373cade4e832627b4f6</tt></dd>
 * <dt><b><tt>hash-</tt><i>algorithm</i></b></dt>
 * <dd>Encode any hash algorithm; <tt>hash-MD5</tt> is equivalent to
 * <tt>md5</tt>, <tt>hash-SHA-1</tt> is equivalent to <tt>sha1</tt></dd>
 * <dt><b><tt>split</tt></b></dt>
 * <dd>Split the expression into multiple expressions, delimited by a custom
 * character directly following the colon after <tt>split</tt>, like
 * <tt>split:#Area#hex:35#1</tt> -&gt; <tt>Area51</tt></dd>
 * <dt><b><tt>unescape</tt></b></dt>
 * <dd>Parse Java escapes, like <tt>unescape:\r\n</tt> -&gt; <tt>hex:0d0a</tt>,
 * or <tt>unescape:Some\u20acfor\44</tt> -&gt; <tt>Some€for$</tt></dd>
 * <dt><b><tt>encode-</tt><i>charset</i></b></dt>
 * <dd>Encode in a given charset, like <tt>encode-ISO-8859-15:€</tt> -&gt;
 * <tt>hex:a4</tt>, or <tt>encode-UTF-8:€</tt> -&gt; <tt>hex:E282AC</tt></dd>
 * <dt><b><tt>map</tt></b></dt>
 * <dd>Select a given range of characters (or more than one) out of another
 * expression, like <tt>map1-4+5-9o+9+3+3:987654321</tt> -&gt;
 * <tt>9876531177</tt>. The result of a <i>map</i> expression may be up to 4KB
 * long.</dd>
 * <dt><b><i>secondaryName</i></b></dt>
 * <dd>Similar to map, but store the result as the secondary value. When in
 * <i>random</i> mode, also supports expressions like 4r for 4 random
 * characters; when not in <i>random</i> mode, it can use <tt>3-5?</tt>, meaning
 * that characters 3 to 5 are copied to the secondary value, but marked as
 * optional.</dd>
 * </dl>
 */
public class BinaryExpression {

	/**
	 * Parse a binary expression with no secondary expression.
	 */
	public static byte[] parseBinaryExpression(String expression) throws IOException {
		return new BinaryExpression(expression, null, false).getValue();
	}

	private final byte[] value, secondaryValue;
	private final boolean secondaryValid, secondaryRandom;
	private final boolean[] secondaryBits;

	/**
	 * Create a new binary expression with optional secondary expressions
	 * 
	 * @param expression
	 *            Expression to parse
	 * @param secondaryName
	 *            Name used in the expression to denote secondary expression
	 *            (like <tt>mask</tt> or <tt>iv</tt>)
	 * @param secondaryRandom
	 *            Whether the secondary expression may contain random bytes
	 *            (like an iv); if not, the marked bytes are optional instead of
	 *            random
	 */
	public BinaryExpression(String expression, String secondaryName, boolean secondaryRandom) throws IOException {
		Stack/* <String> */transforms = new Stack();
		int pos;
		while ((pos = expression.indexOf(':')) != -1) {
			String transform = expression.substring(0, pos);
			transforms.push(transform);
			expression = expression.substring(pos + 1);
			if (transform.length() == 0) {
				transforms.pop();
				break;
			} else if (transform.equals("split")) {
				break;
			}
		}

		byte[] value = expression.getBytes("UTF-8");
		byte[] secondaryValue = null;
		boolean[] secondaryBits = null;

		try {
			while (!transforms.isEmpty()) {
				String transform = (String) transforms.pop();
				if (transform.equals("hex")) {
					if (value.length % 2 != 0)
						throw new IllegalArgumentException("invalid hex length");
					byte[] valueNew = new byte[value.length / 2];
					for (int j = 0; j < valueNew.length; j++) {
						valueNew[j] = (byte) Integer.parseInt((char) value[j * 2] + "" + (char) value[j * 2 + 1], 16);
					}
					value = valueNew;
				} else if (transform.equals("base64")) {
					String base64Text = new String(value, "UTF-8");
					value = decodeBase64(base64Text);
				} else if (transform.equals("sha1")) {
					value = MessageDigest.getInstance("SHA-1").digest(value);
				} else if (transform.equals("md5")) {
					value = MessageDigest.getInstance("MD5").digest(value);
				} else if (transform.equals("split")) {
					String parts = new String(value, "UTF-8");
					StringTokenizer st = new StringTokenizer(parts.substring(1), "" + parts.charAt(0));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					while (st.hasMoreTokens()) {
						baos.write(parseBinaryExpression(st.nextToken()));
					}
					value = baos.toByteArray();
				} else if (transform.equals("unescape")) {
					value = unescape(new String(value, "UTF-8"), "").getBytes("UTF-8");
				} else if (transform.startsWith("hash-")) {
					value = MessageDigest.getInstance(transform.substring(5)).digest(value);
				} else if (transform.startsWith("encode-")) {
					value = new String(value, "UTF-8").getBytes(transform.substring(7));
				} else if (transform.startsWith("map")) {
					byte[] tmp = new byte[4096];
					int count = 0;
					String[] exprs = transform.substring(3).split("\\+");
					for (int j = 0; j < exprs.length; j++) {
						if (exprs[j].length() == 0)
							continue;
						NumberExpression expr = new NumberExpression(exprs[j]);
						for (int k = expr.getMinimum(); k <= expr.getMaximum(); k++) {
							if (expr.matches(k))
								tmp[count++] = value[k - 1];
						}
					}
					value = new byte[count];
					System.arraycopy(tmp, 0, value, 0, count);
				} else if (secondaryName != null && transform.startsWith(secondaryName)) {
					boolean bitsUsedTemp = false;
					byte[] tmp = new byte[4096];
					boolean[] tmpbit = new boolean[4096];
					int count = 0;
					String[] exprs = transform.substring(secondaryName.length()).split("\\+");
					for (int j = 0; j < exprs.length; j++) {
						if (secondaryRandom && exprs[j].endsWith("r")) {
							for (int k = 0; k < Integer.parseInt(exprs[j].substring(0, exprs[j].length() - 1)); k++) {
								tmpbit[count++] = true;
								bitsUsedTemp = true;
							}
						} else {
							boolean optional = false;
							if (!secondaryRandom && exprs[j].endsWith("?")) {
								optional = true;
								bitsUsedTemp = true;
								exprs[j] = exprs[j].substring(0, exprs[j].length() - 1);
							}
							NumberExpression expr = new NumberExpression(exprs[j]);
							for (int k = expr.getMinimum(); k <= expr.getMaximum(); k++) {
								if (expr.matches(k)) {
									tmp[count] = value[k - 1];
									tmpbit[count] = optional;
									count++;
								}
							}
						}
					}
					secondaryValue = new byte[count];
					System.arraycopy(tmp, 0, secondaryValue, 0, count);
					if (bitsUsedTemp) {
						secondaryBits = new boolean[count];
						System.arraycopy(tmpbit, 0, secondaryBits, 0, count);
					} else {
						secondaryBits = null;
					}
				} else {
					throw new IllegalArgumentException("Unsupported transform: " + transform);
				}
			}
		} catch (NoSuchAlgorithmException ex) {
			IOException ioex = new IOException("Cannot create algorithm");
			ioex.initCause(ex);
			throw ioex;
		}
		this.value = value;
		this.secondaryValue = secondaryValue;
		this.secondaryRandom = secondaryRandom;
		this.secondaryValid = secondaryName != null;
		this.secondaryBits = secondaryBits;
	}

	private byte[] decodeBase64(String base64Text) throws IOException {
		return new sun.misc.BASE64Decoder().decodeBuffer(base64Text);
	}

	/**
	 * Unescape Java escape sequences like \n or octal or unicode escapes.
	 * 
	 * @param string
	 *            The string to unescape
	 * @param literalChars
	 *            List of characters (like quotation marks) that should return
	 *            themselves instead of producing an error. A backslash will
	 *            always return itself regardless whether it is in this list or
	 *            not.
	 */
	public static String unescape(String string, String literalChars) throws IOException {
		char[] chars = string.toCharArray();
		StringBuffer sb = new StringBuffer(chars.length);
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != '\\') {
				sb.append(chars[i]);
				continue;
			}
			i++;
			if (i == chars.length)
				throw new IOException("Cannot unescape backslash at end of string");

			switch (chars[i]) {
			case 'b':
				sb.append('\b');
				break;
			case 't':
				sb.append('\t');
				break;
			case 'n':
				sb.append('\n');
				break;
			case 'f':
				sb.append('\f');
				break;
			case 'r':
				sb.append('\r');
				break;
			case '\\':
				sb.append('\\');
				break;

			case 'u':
				if (chars.length < i + 5)
					throw new IOException("Invalid unicode escape: " + string.substring(i - 1));
				try {
					String unicodeChars = string.substring(i + 1, i + 5);
					sb.append((char) Integer.parseInt(unicodeChars, 16));
					i += 4;
				} catch (NumberFormatException ex) {
					throw new IOException("Invalid unicode escape: " + string.substring(i - 1, i + 5));
				}
				break;

			default:
				if (literalChars.indexOf(chars[i]) != -1) {
					sb.append(chars[i]);
					break;
				}
				String octalValue = "";
				while (i < chars.length && chars[i] >= '0' && chars[i] <= '7' && (octalValue.length() < 2 || (octalValue.length() == 2 && octalValue.charAt(0) <= '3'))) {
					octalValue += chars[i];
					i++;
				}
				if (octalValue.length() == 0) {
					throw new IOException("Invalid escape sequence: " + string.substring(i - 1, i + 1));
				}
				i--;
				sb.append((char) Integer.parseInt(octalValue, 8));
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * Return the value of the parsed expression.
	 */
	public byte[] getValue() {
		return value;
	}

	/**
	 * Return the secondary value, if any.
	 */
	public byte[] getSecondaryValue() {
		if (!secondaryValid)
			throw new IllegalStateException("No secondary value parsed");
		return secondaryValue;
	}

	/**
	 * Return the bytes marked (as optional or random) in the secondary value,
	 * if any.
	 */
	public boolean[] getMarkedSecondaryBytes() {
		if (!secondaryValid)
			throw new IllegalStateException("No secondary value parsed");
		return secondaryBits;
	}

	/**
	 * Compute a new random secondary value. This works only if the secondary
	 * value was parsed in random mode.
	 * 
	 * @param randomSource
	 *            source for the randomness
	 */
	public byte[] getRandomSecondaryValue(Random randomSource) {
		if (!secondaryValid)
			throw new IllegalStateException("No secondary value parsed");
		if (!secondaryRandom)
			throw new IllegalStateException("Not parsed in random mode");
		if (secondaryBits == null)
			return secondaryValue;

		byte[] result = new byte[secondaryValue.length];
		for (int i = 0; i < result.length; i++) {
			if (secondaryBits[i])
				result[i] = (byte) randomSource.nextInt(256);
			else
				result[i] = secondaryValue[i];
		}
		return result;
	}
}
