package mgfoms.ru.ws;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getDocRequest",
    "checksum"//,
//    "content"
})
@XmlRootElement(name = "getDocResponse")
public class GetDocResponse {

    @XmlElement(required = true)
	protected GetDocRequestObject getDocRequest;
    @XmlElement(required = true)
	protected String checksum;
//    @XmlMimeType("application/zip")
//	protected DataHandler content;

	/**
	 * Gets the value of the getDocRequest property.
	 *
	 * @return possible object is {@link GetDocRequest2222 }
	 *
	 */
	public GetDocRequestObject getGetDocRequest() {
		return getDocRequest;
	}

	/**
	 * Sets the value of the getDocRequest property.
	 *
	 * @param value allowed object is {@link GetDocRequest2222 }
	 *
	 */
	@XmlTransient
	public void setGetDocRequest(GetDocRequestObject value) {
		this.getDocRequest = value;
	}

	/**
	 * Gets the value of the checksum property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * Sets the value of the checksum property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	@XmlTransient
	public void setChecksum(String value) {
		this.checksum = value;
	}

//	/**
//	 * Gets the value of the content property.
//	 *
//	 * @return possible object is {@link DataHandler }
//	 *
//	 */
//	public DataHandler getContent() {
//		return content;
//	}
//
//	/**
//	 * Sets the value of the content property.
//	 *
//	 * @param value allowed object is {@link DataHandler }
//	 *
//	 */
//	@XmlTransient
//	public void setContent(DataHandler value) {
//		this.content = value;
//	}

}
