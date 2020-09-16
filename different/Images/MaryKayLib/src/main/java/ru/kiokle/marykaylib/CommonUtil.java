/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Arrays;

/**
 *
 * @author me
 */
public class CommonUtil {
        public static String s = FileSystems.getDefault().getSeparator();


    public static File getRelativeFilePath(File dir, String file) {
        return new File(dir.getAbsolutePath() + File.separator + file);
    }

    public static File getPathToSaveFolder(Class class_) {
        try {
            File file = new File(class_.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.getAbsolutePath().contains("classes")) { // Launched from debugger!
                file = Arrays.stream(file.getParentFile().listFiles()).filter(localFile -> localFile.getName().contains("MaryKayImage.jar")).findFirst().get();
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
}
