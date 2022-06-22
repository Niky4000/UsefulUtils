//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.11.23 at 10:56:32 AM MSK 
//


package mgfoms.ru.ws;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{http://ws.ru.mgfoms}getDocListRequest"/&gt;
 *         &lt;element name="docList" type="{http://ws.ru.mgfoms}docMetadata" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "getDocListRequest",
    "docList"
})
@XmlRootElement(name = "getDocListResponse")
public class GetDocListResponse {

    @XmlElement(required = true)
    protected GetDocListRequestObject getDocListRequest;
    protected List<DocMetadata> docList;

    /**
     * Gets the value of the getDocListRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetDocListRequest2222 }
     *     
     */
    public GetDocListRequestObject getGetDocListRequest() {
        return getDocListRequest;
    }

    /**
     * Sets the value of the getDocListRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetDocListRequest2222 }
     *     
     */
    public void setGetDocListRequest(GetDocListRequestObject value) {
        this.getDocListRequest = value;
    }

    /**
     * Gets the value of the docList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the docList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocMetadata }
     * 
     * 
     */
    public List<DocMetadata> getDocList() {
        if (docList == null) {
            docList = new ArrayList<DocMetadata>();
        }
        return this.docList;
    }

}
