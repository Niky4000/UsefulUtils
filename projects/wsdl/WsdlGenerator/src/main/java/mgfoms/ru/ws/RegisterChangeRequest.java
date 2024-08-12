//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.11.23 at 10:56:32 AM MSK 
//


package mgfoms.ru.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="authInfo" type="{http://ws.ru.mgfoms}wsAuthInfo"/&gt;
 *         &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="docId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="nschet" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="C_OKATO1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="OKATO_OMS" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="year" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="month" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "authInfo",
    "action",
    "docId",
    "category",
    "nschet",
    "cokato1",
    "okatooms",
    "year",
    "month"
})
@XmlRootElement(name = "registerChangeRequest")
public class RegisterChangeRequest {

    @XmlElement(required = true)
    protected WsAuthInfo authInfo;
    @XmlElement(required = true)
    protected String action;
    @XmlElement(required = true)
    protected String docId;
    @XmlElement(required = true)
    protected String category;
    @XmlElement(required = true)
    protected String nschet;
    @XmlElement(name = "C_OKATO1", required = true)
    protected String cokato1;
    @XmlElement(name = "OKATO_OMS", required = true)
    protected String okatooms;
    protected int year;
    protected int month;

    /**
     * Gets the value of the authInfo property.
     * 
     * @return
     *     possible object is
     *     {@link WsAuthInfo }
     *     
     */
    public WsAuthInfo getAuthInfo() {
        return authInfo;
    }

    /**
     * Sets the value of the authInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link WsAuthInfo }
     *     
     */
    public void setAuthInfo(WsAuthInfo value) {
        this.authInfo = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the docId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocId() {
        return docId;
    }

    /**
     * Sets the value of the docId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocId(String value) {
        this.docId = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategory(String value) {
        this.category = value;
    }

    /**
     * Gets the value of the nschet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNschet() {
        return nschet;
    }

    /**
     * Sets the value of the nschet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNschet(String value) {
        this.nschet = value;
    }

    /**
     * Gets the value of the cokato1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOKATO1() {
        return cokato1;
    }

    /**
     * Sets the value of the cokato1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOKATO1(String value) {
        this.cokato1 = value;
    }

    /**
     * Gets the value of the okatooms property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOKATOOMS() {
        return okatooms;
    }

    /**
     * Sets the value of the okatooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOKATOOMS(String value) {
        this.okatooms = value;
    }

    /**
     * Gets the value of the year property.
     * 
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     */
    public void setYear(int value) {
        this.year = value;
    }

    /**
     * Gets the value of the month property.
     * 
     */
    public int getMonth() {
        return month;
    }

    /**
     * Sets the value of the month property.
     * 
     */
    public void setMonth(int value) {
        this.month = value;
    }

}
