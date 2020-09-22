/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author me
 */
public class LogAnalizer {

    public static void analizeLogs(String logDirPath, String[] substring) throws IOException {
        File logDir = new File(logDirPath);
        File[] listFiles = logDir.listFiles();
        for (File logFile : listFiles) {
            String string = new String(Files.readAllBytes(logFile.toPath()));
            List<Integer> countList = Arrays.stream(substring).map(substr -> countSubstring(string, substr)).collect(Collectors.toList());
            System.out.println(logFile.getName() + " " + countList.stream().map(obj -> obj.toString()).reduce("", (str1, str2) -> str1 + " " + str2));
        }
    }

    private static int countSubstring(String str, String findStr) {
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = str.indexOf(findStr, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return count;
    }
}
