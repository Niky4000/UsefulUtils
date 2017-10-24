package ru.ibs.pmp.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by IntelliJ IDEA.
 * User: ezhulkov
 * Date: 11/22/12
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlType(name = "AbstractBaseEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractBaseEntity implements BaseEntity {

    @Override
    public int hashCode() {
        return getId() == null ? super.hashCode() : (getId().hashCode() * 37);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getId() == null ||  !(obj instanceof BaseEntity) || ((BaseEntity) obj).getId() == null) {
            return false;
        }
        return getId().equals(((BaseEntity) obj).getId());
    }

}
