package ru.ibs.tomcatrestart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {

    List<String> fileList;
//    private static final String INPUT_ZIP_FILE = "C:\\MyFile.zip";
//    private static final String OUTPUT_FOLDER = "C:\\outputzip";

    public static void main(String[] args) {
//    	UnZip unZip = new UnZip();
//    	unZip.unZipIt(INPUT_ZIP_FILE,OUTPUT_FOLDER);
    }

    /**
     * Unzip it
     *
     * @param zipFile input zip file
     * @param outputFolder
     */
    public static void unZipIt(String zipFile, String outputFolder, Consumer<File> falsify) {
        byte[] buffer = new byte[1024];
        try {
            //create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                System.out.println("file unzip : " + newFile.getAbsoluteFile());
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                if (!ze.isDirectory()) {
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    falsify.accept(newFile);
                } else {
                    newFile.mkdirs();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
