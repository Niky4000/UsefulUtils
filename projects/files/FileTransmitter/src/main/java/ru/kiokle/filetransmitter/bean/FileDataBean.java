/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter.bean;

import java.io.Serializable;

/**
 *
 * @author Me
 */
public class FileDataBean implements Serializable {

    private final byte[] data;
    private final String fileRelativePath;
    private final long fileLength;
    private final int packageNumber;
    private final int packagesCount;

    public FileDataBean(byte[] data, String fileRelativePath, long fileLength, int packageNumber, int packagesCount) {
        this.data = data;
        this.fileRelativePath = fileRelativePath;
        this.fileLength = fileLength;
        this.packageNumber = packageNumber;
        this.packagesCount = packagesCount;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileRelativePath() {
        return fileRelativePath;
    }

    public long getFileLength() {
        return fileLength;
    }

    public int getPackageNumber() {
        return packageNumber;
    }

    public int getPackagesCount() {
        return packagesCount;
    }
}
