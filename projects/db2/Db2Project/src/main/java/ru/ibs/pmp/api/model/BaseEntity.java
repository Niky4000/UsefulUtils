package ru.ibs.pmp.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * User: ezhulkov
 * Date: 11/22/12
 * Time: 6:05 PM
 */

@XmlType(name = "BaseEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public interface BaseEntity extends Serializable {

    Long getId();

    void setId(Long id);

}
