/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.generatefields;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author NAnishhenko
 */
public class GenerateFields {

    public static void main(String[] args) throws IOException, Exception {
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля.txt");
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.4.txt");
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.5.txt");
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.7.txt");
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.8.txt");
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.9.txt");
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.10.txt");
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.11.txt");
        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.12.txt");
//        handleFile("D:\\Documents\\Project\\PmpInnerWS\\Поля2.13.txt");
    }

    private static void handleFile(String fileName) throws IOException, Exception {
        StringBuilder strBuf = new StringBuilder();
        String fileData = new String(Files.readAllBytes(new File(fileName).toPath()), "utf-8");
        String[] rows = fileData.split("\\n");
        int i = 1;
        for (String row : rows) {
            if (!row.contains(";")) {
                continue;
            }
            String dataType = "";
            String[] columns = row.split(";");
            try {
                if (Integer.valueOf(columns[0].replaceAll("[^\\d]", "")).intValue() != i) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }

            if (columns[2].length() == 0) {
                i++;
                continue;
            }
            if(columns.length>4){
                throw new Exception("Too many columns in row: "+row+"!!!");
            }
            strBuf.append("// " + columns[1] + "\n"
                    + "    @XmlElement(name = \"" + columns[2] + "\", required = true)\n"
                    + "    private " + convertDataType(columns[3]) + " " + convertToNotation(columns[2]) + ";\n");
            i++;
        }
        System.out.println(strBuf.toString());
    }

    private static String convertDataType(String dataType) throws Exception {
        String type = normalizeType(dataType);
        if (type.matches("char\\(\\d+?\\)")) {
            return "String";
        } else if (type.matches("num\\([\\d\\.]+?\\)")) {
            int indexOf = type.indexOf("(");
            int indexOf2 = type.indexOf(")");
            String substring = type.substring(indexOf + 1, indexOf2);
            if (substring.contains(".")) {
                return "BigDecimal";
            }
            Integer numValue = Integer.valueOf(substring);
            if (numValue <= 8) {
                return "Integer";
            } else {
                return "BigDecimal";
            }
        } else if (type.equals("number(6)")) {
            return "Integer";
        } else if (type.equals("date")) {
            return "Date";
        } else if (type.equals("date(8)")) {
            return "Date";
        } else if (type.equals("string")) {
            return "String";
        } else {
            throw new Exception("Unknown data type=(" + type + ")!!!");
        }
//        return null;
    }

    private static String normalizeType(String dataType) {
        return dataType.toLowerCase().replaceAll("\\s", "");
    }

    private static String convertToNotation(String name) {
        name = name.toLowerCase();
        while (name.contains("_")) {
            int indexOf = name.indexOf("_");
            name = name.substring(0, indexOf) + name.substring(indexOf + 1, indexOf + 2).toUpperCase() + name.substring(indexOf + 2);
        }
        return name;
    }
}
