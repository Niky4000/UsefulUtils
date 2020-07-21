/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter.bean;

/**
 *
 * @author Me
 */
public class FileSizeBean {

    private final boolean check;
    private final Long fileSize;

    public FileSizeBean(boolean check, Long fileSize) {
        this.check = check;
        this.fileSize = fileSize;
    }

    public boolean isCheck() {
        return check;
    }

    public Long getFileSize() {
        return fileSize;
    }

}
