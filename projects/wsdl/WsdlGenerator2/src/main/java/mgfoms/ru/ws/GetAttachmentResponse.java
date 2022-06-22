package mgfoms.ru.ws;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for attachment complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="attachment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="attachmentName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="checkSum" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="attachmentData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attachment", propOrder = {
	"authInfo",
	"attachmentName",
	"checkSum",
	"attachmentData"
})
public class GetAttachmentResponse {

	@XmlElement(required = true)
	WsAuthInfo authInfo;
	@XmlElement(required = true)
	protected String attachmentName;
	protected long checkSum;
	@XmlElement(required = true)
	@XmlMimeType("application/zip")
	protected DataHandler attachmentData;

	public WsAuthInfo getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(WsAuthInfo authInfo) {
		this.authInfo = authInfo;
	}

	/**
	 * Gets the value of the attachmentName property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getAttachmentName() {
		return attachmentName;
	}

	/**
	 * Sets the value of the attachmentName property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setAttachmentName(String value) {
		this.attachmentName = value;
	}

	/**
	 * Gets the value of the checkSum property.
	 *
	 */
	public long getCheckSum() {
		return checkSum;
	}

	/**
	 * Sets the value of the checkSum property.
	 *
	 */
	public void setCheckSum(long value) {
		this.checkSum = value;
	}

	/**
	 * Gets the value of the attachmentData property.
	 *
	 * @return possible object is {@link DataHandler }
	 *
	 */
	public DataHandler getAttachmentData() {
		return attachmentData;
	}

	/**
	 * Sets the value of the attachmentData property.
	 *
	 * @param value allowed object is {@link DataHandler }
	 *
	 */
	public void setAttachmentData(DataHandler value) {
		this.attachmentData = value;
	}

	@Override
	public String toString() {
		return "Attachment{" + "attachmentName=" + attachmentName + ", checkSum=" + checkSum + '}';
	}

	/**
	 * Generates a String representation of the contents of this type. This is
	 * an extension method, produced by the 'ts' xjc plugin
	 *
	 */
//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
//    }
}
