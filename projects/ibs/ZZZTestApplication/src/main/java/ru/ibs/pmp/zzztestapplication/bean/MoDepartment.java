package ru.ibs.pmp.zzztestapplication.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "MODEPT")
@XmlType(name = "MoDepartment")
@XmlAccessorType(XmlAccessType.FIELD)
@SequenceGenerator(name = "SEQ_PMP_MODEPT", sequenceName = "SEQ_PMP_MODEPT")
public class MoDepartment implements Serializable {

	private static final long serialVersionUID = 1776045568154454606L;

//	@PostLoad
//	protected void repair() {
//
//		if (mo != null) {
//			filId = mo.getId();
//		}
//
//	}

	@Id
	@GeneratedValue(generator = "SEQ_PMP_MODEPT", strategy = GenerationType.SEQUENCE)
	@Column(name = "MODEPT_ID", nullable = false, unique = true)
	private Long id;

//	@XmlTransient
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "FIL_ID")
//	private Mo mo;

	@Column(name = "FIL_ID", insertable = false, updatable = false)
	private Long moId;

	// "Фасетный код МГФОМС*
	@Column(name = "DEPTCODE", nullable = false)
	private String facetCode;

	// Наименование отделения МО (отделения филиала МО)
	@Column(name = "DEPTNAME")
	@XmlElement(nillable = false, required = true)
	private String name;

	// Дата начала работы отделения в МО
	@Column(name = "DEPTDATESTART", nullable = false)
	@Temporal(TemporalType.DATE)
	@XmlSchemaType(name = "dateTime")
	@XmlElement(nillable = false, required = true)
	private Calendar deptStartDate;

	// Дата окончания работы отделения в МО
	@Column(name = "DEPTDATEEND")
	@Temporal(TemporalType.DATE)
	@XmlSchemaType(name = "dateTime")
	private Calendar deptEndDate;

	// Код профиля отделения (X трехзначный код Профиля отделения по
	// кодификатору ФФОМС PrV002)
	@Column(name = "DEPTPRCODE", nullable = false)
	@XmlElement(nillable = false, required = true)
	private String profileCode;
	@Transient
	private String profileName;

	// Условия оказания мед помощи (Н двузначный код по справочнику Условия
	// оказания медпомощи)
	@Column(name = "DEPTPOFOTCODE", nullable = false)
	@XmlElement(nillable = false, required = true)
	private String depServiceCondition;
	@Transient
	private String depServiceConditionName;

	// Возрастной признак отделения (B - символ, первая часть фасетного кода,
	// справочник возрастов Vozobs)
	@Column(name = "DEPTVOZCODE", nullable = false)
	@XmlElement(nillable = false, required = true)
	private String deptVozCode;
	@Transient
	private String deptVozName;

	// Расчетное кол-во приемов в смену (для АПУ), количество коек
	@Column(name = "DEPTCOUNT")
	@XmlElement
	private Integer bedCount;

	// Дата создания/изменения/удаления записи в исходном реестре.
	// Если статус записи Активна, то это дата Создания или изменения.
	// Если статус Удалена, то удаления.
	@Column(name = "DEPTCHANGEDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deptChangeDate;

	@Column(name = "DEPTSYSTEMSOURCE")
	@XmlTransient
	private String deptSystemSource;

	@Transient
	private Long filId;

//	public LpuDepartment toLpuDepartment() {
//		LpuDepartment dept = new LpuDepartment();
//		dept.setId(getId() == null ? null : getId().intValue());
//		dept.setProfileCode(getProfileCode());
//		dept.setFacetCode(getFacetCode());
//		dept.setName(getName());
//		dept.setProfileName(getProfileName());
//		dept.setMgfomsCode(getProfileCode());
//		dept.setBedCount(getBedCount());
//		dept.setDepServiceCondition(getDepServiceCondition());
//
//		return dept;
//	}

//	@Override
	public Long getId() {
		return id;
	}

//	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getFacetCode() {
		return facetCode;
	}

	public void setFacetCode(String facetCode) {
		this.facetCode = facetCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBedCount() {
		return bedCount;
	}

	public String getDepServiceConditionName() {
		return depServiceConditionName;
	}

	public void setDepServiceConditionName(String depServiceConditionName) {
		this.depServiceConditionName = depServiceConditionName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getDeptVozName() {
		return deptVozName;
	}

	public void setDeptVozName(String deptVozName) {
		this.deptVozName = deptVozName;
	}

	public void setBedCount(Integer bedCount) {
		this.bedCount = bedCount;
	}

	public String getProfileCode() {
		return profileCode;
	}

	public void setProfileCode(String profileCode) {
		this.profileCode = profileCode;
	}

	public String getDepServiceCondition() {
		return depServiceCondition;
	}

	public void setDepServiceCondition(String depServiceCondition) {
		this.depServiceCondition = depServiceCondition;
	}

	public Calendar getDeptStartDate() {
		return deptStartDate;
	}

	public void setDeptStartDate(Calendar deptStartDate) {
		this.deptStartDate = deptStartDate;
	}

	public Calendar getDeptEndDate() {
		return deptEndDate;
	}

	public void setDeptEndDate(Calendar deptEndDate) {
		this.deptEndDate = deptEndDate;
	}

	public Date getDeptChangeDate() {
		return deptChangeDate;
	}

	public void setDeptChangeDate(Date deptChangeDate) {
		this.deptChangeDate = deptChangeDate;
	}

	public String getDeptSystemSource() {
		return deptSystemSource;
	}

	public void setDeptSystemSource(String deptSystemSource) {
		this.deptSystemSource = deptSystemSource;
	}

	public String getDeptVozCode() {
		return deptVozCode;
	}

	public void setDeptVozCode(String deptVozCode) {
		this.deptVozCode = deptVozCode;
	}

//	public Mo getMo() {
//		return mo;
//	}
//
//	public void setMo(Mo mo) {
//		this.mo = mo;
//	}

	public Long getMoId() {
		return moId;
	}

	public void setMoId(Long moId) {
		this.moId = moId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + ((mo == null) ? 0 : mo.hashCode());
		result = prime * result + ((facetCode == null) ? 0 : facetCode.hashCode());
		return result;
	}

	public Long getFilId() {
//		if (mo != null) {
//			return mo.getId();
//		}
		return null;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		}
//		if (obj == null) {
//			return false;
//		}
//		if (getClass() != obj.getClass()) {
//			return false;
//		}
//		final MoDepartment other = (MoDepartment) obj;
//		return Objects.equals(mo, other.mo) && Objects.equals(facetCode, other.facetCode);
//	}

	/**
	 * equals in the same MO
	 *
	 * @param obj
	 * @return
	 * @created 14 сент. 2016 г.
	 * @author ekhrykin
	 */
//	public boolean fullEquals(Object obj) {
//		if (this == obj) {
//			return true;
//		}
//		if (obj == null) {
//			return false;
//		}
//		if (getClass() != obj.getClass()) {
//			return false;
//		}
//
//		final MoDepartment other = (MoDepartment) obj;
//
//		return Objects.equals(mo, other.mo) && Objects.equals(id, other.id) && Objects.equals(facetCode, other.facetCode)
//				&& Objects.equals(bedCount, other.bedCount) && Objects.equals(depServiceCondition, other.depServiceCondition)
//				&& Objects.equals(deptVozCode, other.deptVozCode) && Objects.equals(name, other.name)
//				&& Objects.equals(profileCode, other.profileCode) && Objects.equals(deptStartDate, other.deptStartDate)
//				&& Objects.equals(deptEndDate, other.deptEndDate);
//	}

//	@Override
//	public String toString() {
//		return "Отделение [id=" + id + ", mo=" + mo + ", facetCode=" + facetCode + ", name=" + name + ", profileCode="
//				+ profileCode + ", depServiceCondition=" + depServiceCondition + ", deptVozCode=" + deptVozCode + "]";
//	}

}
