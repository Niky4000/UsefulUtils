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
public class FileStatusBean implements Serializable {

    private final Long length;
    private final String md5;
    private final String fileRelativePath;
    private final String absolutePath;
    private final boolean ready;

    public FileStatusBean(Long length, String md5, String fileRelativePath, String absolutePath, boolean ready) {
        this.length = length;
        this.md5 = md5;
        this.fileRelativePath = fileRelativePath;
        this.absolutePath = absolutePath;
        this.ready = ready;
    }

    public Long getLength() {
        return length;
    }

    public String getMd5() {
        return md5;
    }

    public String getFileRelativePath() {
        return fileRelativePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean isReady() {
        return ready;
    }
}
