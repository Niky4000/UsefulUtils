/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.api.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author NAnishhenko
 */
@Entity
@Table(name = "PMP_BILL_STATISTICS_TIME")
@SequenceGenerator(name = "PMP_BILL_TIME", sequenceName = "PMP_BILL_TIME_SEQ")
@XmlRootElement
public class BillStatisticsTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "PMP_BILL_TIME", strategy = GenerationType.SEQUENCE)
    private Long id;
    @Basic(optional = false)
    @Column(name = "OPERATION_DESCRIPTION", nullable = false, length = 1024)
    private String operationDescription;
    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "ALAPSED_TIME")
    private Long alapsedTime;
    @JoinColumn(name = "FK", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BillStatistics billStatistics;

    public BillStatisticsTime() {
    }

    public BillStatisticsTime(String operationDescription, Date created, Long alapsedTime) {
        this.operationDescription = operationDescription;
        this.created = created;
        this.alapsedTime = alapsedTime;
    }

    public BillStatisticsTime(Long id) {
        this.id = id;
    }

    public BillStatisticsTime(Long id, String operationDescription) {
        this.id = id;
        this.operationDescription = operationDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperationDescription() {
        return operationDescription;
    }

    public void setOperationDescription(String operationDescription) {
        this.operationDescription = operationDescription;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getAlapsedTime() {
        return alapsedTime;
    }

    public void setAlapsedTime(Long allapsedTime) {
        this.alapsedTime = allapsedTime;
    }

    public BillStatistics getBillStatistics() {
        return billStatistics;
    }

    public void setBillStatistics(BillStatistics billStatistics) {
        this.billStatistics = billStatistics;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BillStatisticsTime)) {
            return false;
        }
        BillStatisticsTime other = (BillStatisticsTime) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

//    @Override
//    public String toString() {
//        return "entity.PmpBillStatisticsTime[ id=" + id + " ]";
//    }
//    @Override
//    public String toString() {
//        return "BillStatisticsTime{" + "id=" + id + ", operationDescription=" + operationDescription + ", created=" + (created != null ? new SimpleDateFormat("yyyy-MM-dd").format(created) : "null") + ", alapsedTime=" + (alapsedTime != null ? new SimpleDateFormat("yyyy-MM-dd").format(alapsedTime) : "null") + ", billStatistics=" + billStatistics + '}';
//    }
    @Override
    public String toString() {
        return "BillStatisticsTime{" + "id=" + id + ", operationDescription=" + operationDescription + ", created=" + (created != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(created) : "null") + ", alapsedTime=" + (alapsedTime != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(alapsedTime) : "null") + '}';
    }
}
