package jtcpfwd.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class EnglishWordCoder {

	private final byte[] data;
	private int offset = 0;
	private Random r = new Random();
	private boolean quoteFlag, newParagraphFlag;

	private static final String[] PATTERNS = {
			"A,a",
			"A,A",
			"Aa,aa",
			"Aa,aA",
			"Aa,Aa",
			"Aa,AA",
			"AA,aa",
			"AA,aA",
	};

	private byte getByte() {
		byte ret;
		if (offset == data.length)
			ret = (byte) 0;
		else if (offset > data.length)
			// make sure it cannot become zero even if some flags get stripped
			ret = (byte) (1 + 2 * r.nextInt(127));
		else
			ret = data[offset];
		offset++;
		return ret;
	}

	public EnglishWordCoder(byte[] data) {
		this.data = data;
	}

	private String getSentence() {
		StringBuffer sb = new StringBuffer();
		int sentenceInfo = getByte() & 0xff;
		quoteFlag = (sentenceInfo & 0x80) != 0;
		newParagraphFlag = (sentenceInfo & 0x40) != 0;
		boolean commaFlag = (sentenceInfo & 0x20) != 0;
		char endmark = "?!.;".charAt((sentenceInfo & 0x18) >> 3);
		String pattern = PATTERNS[sentenceInfo & 0x07];
		int[] wordlengths = new int[pattern.length() == 3 ? 2 : 4];
		for (int i = 0; i < wordlengths.length; i += 2) {
			int tmp = getByte() & 0xff;
			wordlengths[i] = (tmp & 0x0f) + 1;
			wordlengths[i + 1] = (tmp >> 4) + 1;
		}
		int idx = 0;
		for (int i = 0; i < pattern.length(); i++) {
			switch (pattern.charAt(i)) {
			case 'A':
				sb.append(" ");
				appendWord(sb, wordlengths[idx++], true);
				break;
			case 'a':
				sb.append(" ");
				appendWord(sb, wordlengths[idx++], false);
				break;
			case ',':
				if (commaFlag)
					sb.append(",");
				break;
			default:
				throw new RuntimeException();
			}
		}
		if (idx != wordlengths.length)
			throw new RuntimeException();
		sb.append(endmark);
		return sb.delete(0, 1).toString();
	}

	private void appendWord(StringBuffer sb, int length, boolean capital) {
		if (length < 1 || length > 16)
			throw new RuntimeException();
		int start = sb.length();
		for (int i = 0; i < length; i++) {
			int syllableInfo = getByte() & 0xff;
			String vowel = "a e i o u ayoyee".substring((syllableInfo >> 5) * 2);
			vowel = vowel.substring(0, 2).trim();
			String suffix = (syllableInfo & 0x10) != 0 ? "n" : "";
			char consonant = "bcdfgjklmprstwxz".charAt(syllableInfo & 0x0f);
			sb.append(consonant + vowel + suffix);
		}
		if (capital)
			sb.setCharAt(start, Character.toUpperCase(sb.charAt(start)));
	}

	public String getPlainText() {
		StringBuffer sb = new StringBuffer();
		boolean quoteOpen = false;
		boolean sol = true;
		while (true) {
			boolean shouldStop = offset > data.length;
			boolean canStop = true;
			String sent = getSentence();
			if (shouldStop) {
				quoteFlag = quoteOpen;
				newParagraphFlag = false;
			}
			if (quoteFlag && quoteOpen) {
				if (sb.charAt(sb.length() - 1) == ';') {
					sb.delete(sb.length() - 1, sb.length());
					sb.append("\",");
				} else {
					sb.append("\"");
				}
				quoteOpen = false;
				quoteFlag = false;
			}
			if (!sol)
				sb.append(" ");
			sol = false;
			if (quoteFlag && !quoteOpen) {
				sb.append("\"");
				quoteOpen = true;
			}
			sb.append(sent);
			if (sent.endsWith(";"))
				canStop = false;
			if (newParagraphFlag) {
				if (quoteOpen) {
					if (sb.charAt(sb.length() - 1) == ';') {
						sb.delete(sb.length() - 1, sb.length());
						sb.append(".\" -\n");
					} else {
						sb.append("\"\n");
					}
					quoteOpen = false;
				} else {
					if (sb.charAt(sb.length() - 1) == ';') {
						sb.delete(sb.length() - 1, sb.length());
						sb.append(". -\n");
					} else {
						sb.append("\n");
					}
				}
				canStop = false;
				sol = true;
			}
			if (canStop && offset > data.length && !quoteOpen) {
				break;
			}
		}
		return sb.toString();
	}

	public static byte[] decode(String plain) throws IOException {
		plain = plain.replaceAll("\",", ";\"").replaceAll("\\.\" -\n", ";\"\n").replaceAll("\\. -\n", ";\n");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean quoteOpen = false;
		boolean sol = true;
		int pos = 0;
		while (pos < plain.length()) {
			boolean quoteFlag = false, newParagraphFlag = false;

			if (quoteOpen) {
				if (plain.charAt(pos) == '"') {
					pos++;
					quoteFlag = true;
					quoteOpen = false;
				}
			}
			if (!sol) {
				if (plain.charAt(pos) != ' ')
					throw new RuntimeException(plain.substring(pos - 10, pos) + "|" + plain.substring(pos, pos + 10) + "/" + quoteOpen);
				pos++;
			}
			sol = false;
			if (!quoteOpen && plain.charAt(pos) == '"') {
				pos++;
				quoteFlag = true;
				quoteOpen = true;
			}
			int pos1 = plain.indexOf('.', pos);
			int pos2 = plain.indexOf(';', pos);
			int pos3 = plain.indexOf('!', pos);
			int pos4 = plain.indexOf('?', pos);
			if (pos1 == -1)
				pos1 = pos2;
			if (pos1 == -1)
				pos1 = pos3;
			if (pos1 == -1)
				pos1 = pos4;
			if (pos1 == -1)
				throw new RuntimeException();
			if (pos2 != -1 && pos2 < pos1)
				pos1 = pos2;
			if (pos3 != -1 && pos3 < pos1)
				pos1 = pos3;
			if (pos4 != -1 && pos4 < pos1)
				pos1 = pos4;
			if (pos1 == -1)
				throw new RuntimeException();
			String sentence = plain.substring(pos, pos1);
			int endmark = "?!.;".indexOf(plain.charAt(pos1));
			if (endmark == -1)
				throw new RuntimeException();
			pos = pos1 + 1;
			if (quoteOpen && plain.startsWith("\"\n", pos)) {
				pos += 2;
				quoteOpen = false;
				newParagraphFlag = true;
				sol = true;
			} else if (!quoteOpen && plain.startsWith("\n", pos)) {
				pos++;
				newParagraphFlag = true;
				sol = true;
			}

			boolean commaFlag = false;
			if (sentence.indexOf(',') != -1) {
				commaFlag = true;
				int oldlen = sentence.length();
				sentence = sentence.replaceAll(",", "");
				if (sentence.length() != oldlen - 1)
					throw new RuntimeException();
			}
			String[] words = sentence.split(" ");
			int sentenceInfo = (quoteFlag ? 0x80 : 0) |
					(newParagraphFlag ? 0x40 : 0) |
					(commaFlag ? 0x20 : 0) |
					(endmark << 3) |
					getPattern(words);
			baos.write(sentenceInfo);
			for (int i = 0; i < words.length; i += 2) {
				byte[] b1 = decodeWord(words[i]);
				byte[] b2 = decodeWord(words[i + 1]);
				baos.write((b1.length - 1) | ((b2.length - 1) << 4));
			}
			for (int i = 0; i < words.length; i++) {
				baos.write(decodeWord(words[i]));
			}
		}
		byte[] data = baos.toByteArray();
		int lastZero = -1;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0)
				lastZero = i;
		}
		if (lastZero == -1)
			throw new RuntimeException();
		byte[] result = new byte[lastZero];
		System.arraycopy(data, 0, result, 0, lastZero);
		return result;
	}

	private static int getPattern(String[] words) {
		if (words.length != 4 && words.length != 2)
			throw new RuntimeException();
		String t = "";
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (word.charAt(0) >= 'a' && word.charAt(0) <= 'z')
				t += "a";
			else if (word.charAt(0) >= 'A' && word.charAt(0) <= 'Z')
				t += "A";
			else
				throw new RuntimeException(word);
		}
		for (int j = 0; j < PATTERNS.length; j++) {
			if (PATTERNS[j].replaceAll(",", "").equals(t))
				return j;
		}
		throw new RuntimeException(t);
	}

	private static byte[] decodeWord(String string) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		string = string.toLowerCase() + "   ";
		while (string.length() > 3) {
			int consonantIndex = "bcdfgjklmprstwxz".indexOf(string.charAt(0));
			int vowelIndex;
			if (string.charAt(2) == 'y' || string.charAt(2) == 'e') {
				if (string.startsWith("ay", 1))
					vowelIndex = 5;
				else if (string.startsWith("oy", 1))
					vowelIndex = 6;
				else if (string.startsWith("ee", 1))
					vowelIndex = 7;
				else
					throw new RuntimeException();
				string = string.substring(3);
			} else {
				vowelIndex = "aeiou".indexOf(string.charAt(1));
				string = string.substring(2);
			}
			if (vowelIndex < 0 || consonantIndex < 0)
				throw new RuntimeException();
			boolean suffixFlag = false;
			if (string.startsWith("n")) {
				string = string.substring(1);
				suffixFlag = true;
			}
			baos.write((suffixFlag ? 0x10 : 0) | (vowelIndex << 5) | consonantIndex);
		}
		return baos.toByteArray();
	}

	public static void main(String[] args) throws IOException {
		byte[] s = new byte[20];
		Random rr = new Random();
		rr.nextBytes(s);
		System.out.println(new EnglishWordCoder(s).getPlainText());
		for (int i = 0; i < 20; i++) {
			int len = rr.nextInt(1000000);
			byte[] data = new byte[len];
			rr.nextBytes(data);
			String plain = new EnglishWordCoder(data).getPlainText().trim();
			System.out.println(plain.length() / (double) data.length);
			byte[] data2 = decode(plain);
			if (!Arrays.equals(data, data2))
				throw new RuntimeException();
		}
	}

}
