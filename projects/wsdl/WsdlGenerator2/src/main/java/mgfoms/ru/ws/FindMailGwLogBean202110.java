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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for findMailGwLogBean202110 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findMailGwLogBean202110"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="period" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="mailGwLogId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="mailDirection" type="{http://ws.ru.mgfoms}mailDirection"/&gt;
 *         &lt;element name="parcelType" type="{http://ws.ru.mgfoms}parcelType"/&gt;
 *         &lt;element name="moId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="smoId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sendDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="billId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="versionNumber" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="messageStatus" type="{http://ws.ru.mgfoms}mailStatus"/&gt;
 *         &lt;element name="parentMailGwLogId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="answerType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findMailGwLogBean202110", propOrder = {
    "period",
    "mailGwLogId",
    "mailDirection",
    "parcelType",
    "moId",
    "smoId",
    "messageId",
    "sendDate",
    "billId",
    "versionNumber",
    "messageStatus",
    "parentMailGwLogId",
    "answerType"
})
public class FindMailGwLogBean202110 {

    @XmlElement(required = true)
    protected String period;
    protected long mailGwLogId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected MailDirection mailDirection;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ParcelType parcelType;
    protected long moId;
    protected long smoId;
    @XmlElement(required = true)
    protected String messageId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sendDate;
    protected long billId;
    protected long versionNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected MailStatus messageStatus;
    protected Long parentMailGwLogId;
    protected String answerType;

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriod(String value) {
        this.period = value;
    }

    /**
     * Gets the value of the mailGwLogId property.
     * 
     */
    public long getMailGwLogId() {
        return mailGwLogId;
    }

    /**
     * Sets the value of the mailGwLogId property.
     * 
     */
    public void setMailGwLogId(long value) {
        this.mailGwLogId = value;
    }

    /**
     * Gets the value of the mailDirection property.
     * 
     * @return
     *     possible object is
     *     {@link MailDirection }
     *     
     */
    public MailDirection getMailDirection() {
        return mailDirection;
    }

    /**
     * Sets the value of the mailDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailDirection }
     *     
     */
    public void setMailDirection(MailDirection value) {
        this.mailDirection = value;
    }

    /**
     * Gets the value of the parcelType property.
     * 
     * @return
     *     possible object is
     *     {@link ParcelType }
     *     
     */
    public ParcelType getParcelType() {
        return parcelType;
    }

    /**
     * Sets the value of the parcelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParcelType }
     *     
     */
    public void setParcelType(ParcelType value) {
        this.parcelType = value;
    }

    /**
     * Gets the value of the moId property.
     * 
     */
    public long getMoId() {
        return moId;
    }

    /**
     * Sets the value of the moId property.
     * 
     */
    public void setMoId(long value) {
        this.moId = value;
    }

    /**
     * Gets the value of the smoId property.
     * 
     */
    public long getSmoId() {
        return smoId;
    }

    /**
     * Sets the value of the smoId property.
     * 
     */
    public void setSmoId(long value) {
        this.smoId = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the sendDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSendDate() {
        return sendDate;
    }

    /**
     * Sets the value of the sendDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSendDate(XMLGregorianCalendar value) {
        this.sendDate = value;
    }

    /**
     * Gets the value of the billId property.
     * 
     */
    public long getBillId() {
        return billId;
    }

    /**
     * Sets the value of the billId property.
     * 
     */
    public void setBillId(long value) {
        this.billId = value;
    }

    /**
     * Gets the value of the versionNumber property.
     * 
     */
    public long getVersionNumber() {
        return versionNumber;
    }

    /**
     * Sets the value of the versionNumber property.
     * 
     */
    public void setVersionNumber(long value) {
        this.versionNumber = value;
    }

    /**
     * Gets the value of the messageStatus property.
     * 
     * @return
     *     possible object is
     *     {@link MailStatus }
     *     
     */
    public MailStatus getMessageStatus() {
        return messageStatus;
    }

    /**
     * Sets the value of the messageStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailStatus }
     *     
     */
    public void setMessageStatus(MailStatus value) {
        this.messageStatus = value;
    }

    /**
     * Gets the value of the parentMailGwLogId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getParentMailGwLogId() {
        return parentMailGwLogId;
    }

    /**
     * Sets the value of the parentMailGwLogId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setParentMailGwLogId(Long value) {
        this.parentMailGwLogId = value;
    }

    /**
     * Gets the value of the answerType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnswerType() {
        return answerType;
    }

    /**
     * Sets the value of the answerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnswerType(String value) {
        this.answerType = value;
    }

}
