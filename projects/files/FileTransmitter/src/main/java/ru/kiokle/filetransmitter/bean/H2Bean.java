/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter.bean;

import java.sql.Date;

/**
 *
 * @author Me
 */
public class H2Bean {

    private final Long id;
    private final String fileName;
    private final String md5sum;
    private final Date created;
    private final Long file_size;

    public H2Bean(Long id, String fileName, String md5sum, Date created, Long file_size) {
        this.id = id;
        this.fileName = fileName;
        this.md5sum = md5sum;
        this.created = created;
        this.file_size = file_size;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public Date getCreated() {
        return created;
    }

    public Long getFile_size() {
        return file_size;
    }
}
