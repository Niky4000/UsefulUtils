package com.element.springbootandbirt;

import java.io.File;
import java.util.Arrays;

public class FileUtils {

    public static File getPathToJar() {
        try {
            File file = new File(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.getAbsolutePath().contains("classes")) { // Launched from debugger!
                file = Arrays.stream(file.getParentFile().listFiles()) .filter(localFile -> localFile.getName().endsWith(".jar")) .findFirst().get();
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
