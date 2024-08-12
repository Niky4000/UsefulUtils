package jtcpfwd.forwarder.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;

import jtcpfwd.util.BinaryExpression;
import jtcpfwd.util.NumberExpression;

public class DecryptFilter extends EncryptFilter {

	public static final String SYNTAX = "Decrypt,<algorithm>[/<mode>/<padding>],<keydata>";

	public static Class[] getRequiredClasses() {
		return new Class[] {
				EncryptFilter.class, NumberExpression.class, NumberExpression.NumberRange.class,
				BinaryExpression.class, DelayedCipherOutputStream.class };
	}

	public DecryptFilter(String rule) throws Exception {
		super(rule);
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		if (expression.getMarkedSecondaryBytes() == null)
			return new CipherOutputStream(sameDirection, setupCipher(expression.getSecondaryValue()));
		else
			return new DelayedCipherOutputStream(sameDirection, expression.getSecondaryValue().length);
	}

	protected Cipher setupCipher(byte[] iv) throws IOException {
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			IvParameterSpec ivSpec = null;
			if (iv != null) {
				ivSpec = new IvParameterSpec(iv);
			}
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
			return cipher;
		} catch (GeneralSecurityException ex) {
			IOException ex2 = new IOException("Unable to initialize cipher");
			ex2.initCause(ex);
			throw ex2;
		}
	}

	public class DelayedCipherOutputStream extends OutputStream {

		CipherOutputStream target = null;
		final OutputStream stream;
		final byte[] iv;
		int offset = 0;

		DelayedCipherOutputStream(OutputStream stream, int ivLength) {
			this.stream = stream;
			this.iv = new byte[ivLength];
		}

		public void write(int b) throws IOException {
			if (target != null) {
				target.write(b);
				return;
			}
			iv[offset++] = (byte) b;
			if (offset == iv.length) {
				target = new CipherOutputStream(stream, setupCipher(iv));
			}
		}

		public void write(byte[] b, int off, int len) throws IOException {
			if (target != null)
				target.write(b, off, len);
			else
				super.write(b, off, len);
		}

		public void flush() throws IOException {
			if (target != null)
				target.flush();
		}

		public void close() throws IOException {
			if (target != null)
				target.close();
		}
	}
}
