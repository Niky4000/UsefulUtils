package jtcpfwd.util;

import java.util.NoSuchElementException;

/**
 * 16-bit extended hamming code. Can encode 11 bits of payload and correct 1 bit
 * flip or detect up to 3 bit flips.
 */
public class Hamming16Code {

	public static void main(String[] args) {
		for (int j = 0; j < 0x800; j++) {
			int hc = generate(j);
			if (!isValid(hc) || correct(hc) != hc)
				throw new RuntimeException();
			for (int i = 1; i < 65536; i++) {
				if (has1To3BitsSet(i)) {
					if (isValid(hc ^ i))
						throw new RuntimeException(hc + "/" + (hc ^ i));
				}
			}
			for (int i = 1; i < 65536; i <<= 1) {
				if (correct(hc ^ i) != hc)
					throw new RuntimeException(hc + "/" + (hc ^ i));
			}
		}
	}

	private static boolean has1To3BitsSet(int value) {
		if (value == 0)
			return false;
		value &= value - 1;
		if (value == 0)
			return true;
		value &= value - 1;
		if (value == 0)
			return true;
		value &= value - 1;
		if (value == 0)
			return true;
		return false;
	}

	/**
	 * All integers below 16 except the powers of two.
	 */
	private static int[] MAPPINGS = { 3, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15 };

	/**
	 * Generate 16-bit extended hamming code from 11-bit input value
	 */
	public static int generate(int payload) {
		if (payload != (payload & 0x7FF))
			throw new IllegalArgumentException("Payload must be 11-bit unsigned value");
		int checkbits = 0;
		boolean parity = false;
		for (int i = 0; i < 11; i++) {
			if ((payload & (1 << i)) != 0) {
				checkbits ^= MAPPINGS[i];
				parity = !parity;
			}
		}
		for (int i = 0; i < 4; i++) {
			if ((checkbits & (1 << i)) != 0) {
				parity = !parity;
			}
		}
		return payload | (checkbits << 11) | (parity ? (1 << 15) : 0);
	}

	/**
	 * Check whether a value is a valid hamming code (i. e. it has no or more
	 * than 3 bits flipped).
	 */
	public static boolean isValid(int value) {
		return value == generate(value & 0x7FF);
	}

	/**
	 * Correct up to one flipped bit in the code.
	 * 
	 * @throws NoSuchElementException
	 *             if the code word cannot be corrected by flipping one bit
	 */
	public static int correct(int wrongValue) throws NoSuchElementException {
		// mask out all data bits
		int checkBits = (wrongValue ^ generate(wrongValue & 0x7ff)) >> 11;
		if (checkBits == 0) // value is correct
			return wrongValue;
		// check parity
		boolean parity = false;
		for (int i = 0; i < 5; i++) {
			if ((checkBits & (1 << i)) != 0) {
				parity = !parity;
			}
		}
		if (!parity)
			throw new NoSuchElementException();
		checkBits &= 0x0f;
		if (checkBits == 0) {
			// parity bit was wrong
			return (wrongValue ^ 0x8000);
		} else if ((checkBits & (checkBits - 1)) == 0) {
			// one bit wrong, i. e. check bit wrong
			return wrongValue ^ (checkBits << 11);
		} else {
			// more bits wrong, i. e. data bit wrong
			for (int i = 0; i < MAPPINGS.length; i++) {
				if (MAPPINGS[i] == checkBits)
					return wrongValue ^ (1 << i);
			}
			throw new NoSuchElementException();
		}
	}
}
