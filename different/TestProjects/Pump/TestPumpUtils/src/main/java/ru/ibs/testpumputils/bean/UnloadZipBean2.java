/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author me
 */
@Entity
@Table(name = "UnloadZipBean")
public class UnloadZipBean2 {

    @Id
    @Column(name = "mailgw_log_id")
    private Long mailgwLogId;

    @Column(name = "mo_id")
    private String moId;

    public Long getMailgwLogId() {
        return mailgwLogId;
    }

    public void setMailgwLogId(Long mailgwLogId) {
        this.mailgwLogId = mailgwLogId;
    }

    public String getMoId() {
        return moId;
    }

    public void setMoId(String moId) {
        this.moId = moId;
    }
}
