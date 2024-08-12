/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.api.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "PMP_BILL_STATISTICS", catalog = "")
@SequenceGenerator(name = "PMP_BILL_STATISTICS", sequenceName = "PMP_BILL_STATISTICS_SEQ")
@XmlRootElement
public class BillStatistics extends AbstractBaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "PMP_BILL_STATISTICS", strategy = GenerationType.SEQUENCE)
    private Long id;

//    @EmbeddedId
//    protected BillStatisticsPK billStatisticsPK;
    @Basic(optional = false)
    @Column(name = "REQUIREMENT_ID", nullable = false)
    private Long requirementId;
    // Тип операции: что происходило
    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private BillStatistics.BillOperation type;

    // Дата запуска процесса обработки счетов
    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    // Дата конца процесса обработки счетов
    @Column(name = "FINISHED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finished;

    // Количество обработанных услуг (счетов)
    @Column(name = "INVOICE_COUNT")
    private Integer invoiceCount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "billStatistics", fetch = FetchType.LAZY)
    private List<BillStatisticsTime> pmpBillStatisticsTimeList;

//    @OneToOne
    @Column(name = "REV")
    private Long revinfo;

    @Column(name = "PARAMETERS")
    private String parameters;

    @Column(name = "USER_UNIQUEID")
    private String userId;

    public BillStatistics() {
    }

    public BillStatistics(Long requirementId, Date created, Date finished, BillOperation type, Integer invoiceCount, Long revinfoId, String parameters, String userId) {
        this.requirementId = requirementId;
        this.type = type;
        this.created = created;
        this.finished = finished;
        this.invoiceCount = invoiceCount;
        this.revinfo = revinfoId;
        this.parameters = parameters;
        this.userId = userId;
    }

    public enum BillOperation {

        RECREATE("Пересоздание счетов стандартной процедурой сервера"),
        CREATE_AND_SEND("Посылка информационных посылок стандартной процедурой сервера"),
        RECREATE_BILL_MODULE("Пересоздание счетов и формирование информационных посылок модулем пересоздания счетов"),
        SEND_BILL_MODULE("Отправка счетов и формирование информационных посылок модулем пересоздания счетов");

        private String text;

        BillOperation(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public Integer getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(Integer invoiceCount) {
        this.invoiceCount = invoiceCount;
    }

    public Long getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Long requirementId) {
        this.requirementId = requirementId;
    }

    public BillOperation getType() {
        return type;
    }

    public void setType(BillOperation type) {
        this.type = type;
    }

    public List<BillStatisticsTime> getPmpBillStatisticsTimeList() {
        return pmpBillStatisticsTimeList;
    }

    public void setPmpBillStatisticsTimeList(List<BillStatisticsTime> pmpBillStatisticsTimeList) {
        this.pmpBillStatisticsTimeList = pmpBillStatisticsTimeList;
    }

    public Long getRevinfo() {
        return revinfo;
    }

    public void setRevinfo(Long revinfo) {
        this.revinfo = revinfo;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BillStatistics other = (BillStatistics) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BillStatistics{" + "id=" + id + ", requirementId=" + requirementId + ", type=" + type + ", requirementId=" + requirementId + ", created=" + created + ", finished=" + finished + ", invoiceCount=" + invoiceCount + '}';
    }

}
