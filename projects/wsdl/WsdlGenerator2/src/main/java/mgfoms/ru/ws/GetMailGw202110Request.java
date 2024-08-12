package mgfoms.ru.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMailGw202110Request", propOrder = {
	"authInfo",
	"period",
	"moId",
	"smoId"
})
public class GetMailGw202110Request {

	@XmlElement(required = true)
	protected WsAuthInfo authInfo;
	@XmlElement(required = true)
	protected String period;
	@XmlElement(required = true)
	protected Long moId;
	@XmlElement(required = true)
	protected Long smoId;

	public WsAuthInfo getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(WsAuthInfo authInfo) {
		this.authInfo = authInfo;
	}

	/**
	 * Gets the value of the period property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 * Sets the value of the period property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setPeriod(String value) {
		this.period = value;
	}

	/**
	 * Gets the value of the moId property.
	 *
	 * @return possible object is {@link Long }
	 *
	 */
	public Long getMoId() {
		return moId;
	}

	/**
	 * Sets the value of the moId property.
	 *
	 * @param value allowed object is {@link Long }
	 *
	 */
	public void setMoId(Long value) {
		this.moId = value;
	}

	/**
	 * Gets the value of the smoId property.
	 *
	 * @return possible object is {@link Long }
	 *
	 */
	public Long getSmoId() {
		return smoId;
	}

	/**
	 * Sets the value of the smoId property.
	 *
	 * @param value allowed object is {@link Long }
	 *
	 */
	public void setSmoId(Long value) {
		this.smoId = value;
	}
}
