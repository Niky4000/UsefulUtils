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

    private final String md5;
    private final String fileRelativePath;

    public FileStatusBean(String md5, String fileRelativePath) {
        this.md5 = md5;
        this.fileRelativePath = fileRelativePath;
    }

    public String getMd5() {
        return md5;
    }

    public String getFileRelativePath() {
        return fileRelativePath;
    }
}
