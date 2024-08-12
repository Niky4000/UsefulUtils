package com.element.springbootandbirt;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class FileUtils {

	public static File getPathToJar() {
		try {
			URL location = FileUtils.class.getProtectionDomain().getCodeSource().getLocation();
			String fileStr = location.getFile();
			File file;
			if (!fileStr.contains("!")) {
				file = new File(location.toURI());
			} else {
				file = new File(fileStr.substring(0, fileStr.indexOf("!")));
			}
			if (file.getAbsolutePath().contains("classes")) { // Launched from debugger!
				file = Arrays.stream(file.getParentFile().listFiles()).filter(localFile -> localFile.getName().endsWith(".jar")).findFirst().get();
			}
			return file;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
