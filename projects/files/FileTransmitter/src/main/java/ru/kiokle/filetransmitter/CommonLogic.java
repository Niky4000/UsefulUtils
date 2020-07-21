/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 *
 * @author Me
 */
public class CommonLogic {

    public static String getMd5Sum(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(Files.readAllBytes(file.toPath()));
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String printData(byte[] buffer) {
        if (buffer == null || buffer.length == 0) {
            return "buffer is empty!";
        } else {
            int digit = Math.min(20, buffer.length);
            byte[] b = new byte[digit];
            System.arraycopy(buffer, 0, b, 0, digit);
            String s = bytesToHex(b);
            return s;
        }
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] serializeObject(Object obj, MyLogger LOG) {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos);) {
            out.writeObject(obj);
            out.flush();
            byte[] byteArray = bos.toByteArray();
            return byteArray;
        } catch (IOException e) {
            LOG.log("Serialization exception!", e);
            return null;
        }
    }

    public static <T> T deSerializeObject(byte[] data, MyLogger LOG) {
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInput input = new ObjectInputStream(bis);) {
            T obj = (T) input.readObject();
            return obj;
        } catch (ClassNotFoundException | IOException e) {
            LOG.log("Deserialization exception!", e);
        }
        return null;
    }
}
