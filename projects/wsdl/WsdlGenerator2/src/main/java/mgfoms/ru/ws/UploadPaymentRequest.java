package mgfoms.ru.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
//import org.apache.commons.lang3.builder.ToStringBuilder;
//import org.apache.cxf.xjc.runtime.JAXBToStringStyle;

/**
 * <p>
 * Java class for uploadMailRequest complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="uploadMailRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="attachment" type="{http://ws.smo.pmp.ibs.ru/}mailAttachment"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadMailRequest", propOrder = {
	"authInfo",
	"attachment"
})
public class UploadPaymentRequest {

	@XmlElement(required = true)
	protected WsAuthInfo authInfo;
	@XmlElement(required = true, nillable = true)
	protected MailAttachment attachment;

	public WsAuthInfo getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(WsAuthInfo authInfo) {
		this.authInfo = authInfo;
	}

	/**
	 * Gets the value of the attachment property.
	 *
	 * @return possible object is {@link MailAttachment }
	 *
	 */
	public MailAttachment getAttachment() {
		return attachment;
	}

	/**
	 * Sets the value of the attachment property.
	 *
	 * @param value allowed object is {@link MailAttachment }
	 *
	 */
	public void setAttachment(MailAttachment value) {
		this.attachment = value;
	}

	@Override
	public String toString() {
		return "UploadMailRequest{" + "attachment=" + attachment + '}';
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
