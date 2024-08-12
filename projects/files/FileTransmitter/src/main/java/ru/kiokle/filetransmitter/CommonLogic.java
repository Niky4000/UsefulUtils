/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Me
 */
public class CommonLogic {

    private static final MyLogger LOG = new MyLogger();

    public synchronized String getMd5Sum_(File file) {
        try {
            LOG.log("getMd5Sum started for " + file.getAbsolutePath() + "!", MyLogger.LogLevel.DEBUG);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(Files.readAllBytes(file.toPath()));
            byte[] digest = md.digest();
            String bytesToHex = bytesToHex(digest);
            LOG.log("getMd5Sum finished for " + file.getAbsolutePath() + "!", MyLogger.LogLevel.DEBUG);
            return bytesToHex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized String getMd5Sum(File file) {
        try {
            LOG.log("getMd5Sum started for " + file.getAbsolutePath() + "!", MyLogger.LogLevel.DEBUG);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            //Create byte array to read data in chunks
            try ( //Get file input stream for reading the file content
                    FileInputStream fis = new FileInputStream(file)) {
                //Create byte array to read data in chunks
                byte[] byteArray = new byte[DIGEST_BUFFER];
                int bytesCount = 0;
                //Read file data and update in message digest
                while ((bytesCount = fis.read(byteArray)) != -1) {
                    md.update(byteArray, 0, bytesCount);
                }
            }
            //Get the hash's bytes
            byte[] digest = md.digest();
            String bytesToHex = bytesToHex(digest);
            LOG.log("getMd5Sum finished for " + file.getAbsolutePath() + "!", MyLogger.LogLevel.DEBUG);
            return bytesToHex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static final int DIGEST_BUFFER = 1024 * 1024 * 10;

    public static String getMd5Sum2_(File crunchifyFile) {
        String crunchifyValue = null;
        FileInputStream crunchifyInputStream = null;
        try {
            crunchifyInputStream = new FileInputStream(crunchifyFile);
            // md5Hex converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
            // The returned array will be double the length of the passed array, as it takes two characters to represent any given byte.
            crunchifyValue = DigestUtils.md5Hex(IOUtils.toByteArray(crunchifyInputStream)).toUpperCase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(crunchifyInputStream);
        }
        return crunchifyValue;
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
