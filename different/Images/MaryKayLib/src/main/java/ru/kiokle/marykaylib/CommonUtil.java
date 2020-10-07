/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Properties;

/**
 *
 * @author me
 */
public class CommonUtil {

    public static String s = FileSystems.getDefault().getSeparator();
    public static final String APPLICATION_CONFIGS = "applicationConfigs.txt";

    public static File getRelativeFilePath(File dir, String file) {
        return new File(dir.getAbsolutePath() + File.separator + file);
    }

    public static File getPathToSaveFolder(Class class_) {
        try {
            File file = new File(class_.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.getAbsolutePath().contains("classes")) { // Launched from debugger!
                file = Arrays.stream(file.getParentFile().listFiles()).filter(localFile -> localFile.getName().contains("MaryKay") && localFile.getName().contains(".jar")).findFirst().get();
            }
            File parentFile = file.getParentFile();
            if (parentFile.getAbsolutePath().contains("target")) {
                parentFile = parentFile.getParentFile();
            }
            return new File(parentFile.getAbsolutePath() + File.separator + "save");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String decodeValue(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Properties getConfigs(File saveFolder) {
        File configsFile = new File(saveFolder + s + APPLICATION_CONFIGS);
        if (!configsFile.exists()) {
            return new Properties();
        }
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(configsFile));
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            return new Properties();
        }
    }

    public static void putConfigs(File saveFolder, String userName, String sign) throws FileNotFoundException, IOException {
        Properties properties = getConfigs(saveFolder);
        properties.setProperty("userName", userName);
        properties.setProperty("sign", sign);
        properties.store(new FileOutputStream(new File(saveFolder + s + APPLICATION_CONFIGS)), "configs");
    }
}
