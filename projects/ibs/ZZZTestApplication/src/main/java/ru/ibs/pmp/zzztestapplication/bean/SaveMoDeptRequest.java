package ru.ibs.pmp.zzztestapplication.bean;

import java.util.Objects;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author me
 */
public class SaveMoDeptRequest {

    protected Integer bedCount;
    protected String depServiceCondition;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deptEndDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deptStartDate;
    protected String deptVozCode;
    protected Long id;
    protected String name;
    protected String profileCode;

    public Integer getBedCount() {
        return bedCount;
    }

    public void setBedCount(Integer bedCount) {
        this.bedCount = bedCount;
    }

    public String getDepServiceCondition() {
        return depServiceCondition;
    }

    public void setDepServiceCondition(String depServiceCondition) {
        this.depServiceCondition = depServiceCondition;
    }

    public XMLGregorianCalendar getDeptEndDate() {
        return deptEndDate;
    }

    public void setDeptEndDate(XMLGregorianCalendar deptEndDate) {
        this.deptEndDate = deptEndDate;
    }

    public XMLGregorianCalendar getDeptStartDate() {
        return deptStartDate;
    }

    public void setDeptStartDate(XMLGregorianCalendar deptStartDate) {
        this.deptStartDate = deptStartDate;
    }

    public String getDeptVozCode() {
        return deptVozCode;
    }

    public void setDeptVozCode(String deptVozCode) {
        this.deptVozCode = deptVozCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.bedCount);
        hash = 37 * hash + Objects.hashCode(this.depServiceCondition);
        hash = 37 * hash + Objects.hashCode(this.deptEndDate);
        hash = 37 * hash + Objects.hashCode(this.deptStartDate);
        hash = 37 * hash + Objects.hashCode(this.deptVozCode);
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.profileCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SaveMoDeptRequest other = (SaveMoDeptRequest) obj;
        if (!Objects.equals(this.depServiceCondition, other.depServiceCondition))
            return false;
        if (!Objects.equals(this.deptVozCode, other.deptVozCode))
            return false;
        if (!Objects.equals(this.name, other.name))
            return false;
        if (!Objects.equals(this.profileCode, other.profileCode))
            return false;
        if (!Objects.equals(this.bedCount, other.bedCount))
            return false;
        if (!Objects.equals(this.deptEndDate, other.deptEndDate))
            return false;
        if (!Objects.equals(this.deptStartDate, other.deptStartDate))
            return false;
        if (!Objects.equals(this.id, other.id))
            return false;
        return true;
    }
}
