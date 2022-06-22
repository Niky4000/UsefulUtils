
package ru.ibs.pmp.zzztestapplication.bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for answerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="answerType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PRELIMINARY"/&gt;
 *     &lt;enumeration value="FINAL"/&gt;
 *     &lt;enumeration value="EXAMINATION"/&gt;
 *     &lt;enumeration value="OLD_FINAL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "answerType")
@XmlEnum
public enum AnswerType {

    PRELIMINARY,
    FINAL,
    EXAMINATION,
    OLD_FINAL,
	OK;

    public String value() {
        return name();
    }

	public static AnswerType fromValue(String v) {
		return v != null ? valueOf(v) : null;
	}
}
