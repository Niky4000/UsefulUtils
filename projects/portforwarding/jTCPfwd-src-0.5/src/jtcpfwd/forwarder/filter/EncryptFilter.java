package jtcpfwd.forwarder.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jtcpfwd.util.BinaryExpression;
import jtcpfwd.util.NumberExpression;

public class EncryptFilter extends Filter {

	public static final String SYNTAX = "Encrypt,<algorithm>[/<mode>/<padding>],<keydata>";

	public static Class[] getRequiredClasses() {
		return new Class[] {
				NumberExpression.class, NumberExpression.NumberRange.class,
				BinaryExpression.class };
	}

	protected final String algorithm;
	protected final SecretKeySpec key;
	protected final BinaryExpression expression;

	public EncryptFilter(String rule) throws Exception {
		int pos = rule.indexOf(',');
		String spec = rule.substring(0, pos);
		algorithm = spec + (spec.indexOf('/') == -1 ? "/CFB8/NoPadding" : "");
		this.expression = new BinaryExpression(rule.substring(pos + 1), "iv", true);
		key = new SecretKeySpec(expression.getValue(), algorithm.split("/")[0]);
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			IvParameterSpec ivSpec = null;
			byte[] iv = expression.getSecondaryValue();
			if (iv != null) {
				if (expression.getMarkedSecondaryBytes() == null) {
					ivSpec = new IvParameterSpec(iv);
				} else {
					byte[] tempIV = expression.getRandomSecondaryValue(new SecureRandom());
					sameDirection.write(tempIV);
					ivSpec = new IvParameterSpec(tempIV);
				}
			}
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			return new CipherOutputStream(sameDirection, cipher);
		} catch (GeneralSecurityException ex) {
			IOException ex2 = new IOException("Unable to initialize cipher");
			ex2.initCause(ex);
			throw ex2;
		}
	}
}