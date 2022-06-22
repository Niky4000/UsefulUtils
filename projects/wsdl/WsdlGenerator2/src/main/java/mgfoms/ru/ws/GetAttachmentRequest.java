package mgfoms.ru.ws;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class GetAttachmentRequest {

	@XmlElement(required = true)
	WsAuthInfo authInfo;
	@XmlElement(required = true)
	protected long mailGWlogid;

	public WsAuthInfo getAuthInfo() {
		return authInfo;
	}

	@XmlTransient
	public void setAuthInfo(WsAuthInfo authInfo) {
		this.authInfo = authInfo;
	}

	/**
	 * Gets the value of the mailGWlogid property.
	 *
	 */
	public long getMailGWlogid() {
		return mailGWlogid;
	}

	/**
	 * Sets the value of the mailGWlogid property.
	 *
	 */
	@XmlTransient
	public void setMailGWlogid(long value) {
		this.mailGWlogid = value;
	}

	@Override
	public String toString() {
		return "GetAttachmentRequest{" + "mailGWlogid=" + mailGWlogid + '}';
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
