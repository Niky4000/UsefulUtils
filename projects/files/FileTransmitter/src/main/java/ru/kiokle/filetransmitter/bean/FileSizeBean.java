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
	private final String md5Sum;

	public FileSizeBean(boolean check, Long fileSize, String md5Sum) {
		this.check = check;
		this.fileSize = fileSize;
		this.md5Sum = md5Sum;
	}

	public boolean isCheck() {
		return check;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public String getMd5Sum() {
		return md5Sum;
	}
}
