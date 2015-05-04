
package com.zenithbank.mtn.fleet;

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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetCardDetailResult" type="{http://zenithbank.com/mtn/fleet}ArrayOfCardDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCardDetailResult"
})
@XmlRootElement(name = "GetCardDetailResponse")
public class GetCardDetailResponse {

    @XmlElement(name = "GetCardDetailResult")
    protected ArrayOfCardDetail getCardDetailResult;

    /**
     * Gets the value of the getCardDetailResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCardDetail }
     *     
     */
    public ArrayOfCardDetail getGetCardDetailResult() {
        return getCardDetailResult;
    }

    /**
     * Sets the value of the getCardDetailResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCardDetail }
     *     
     */
    public void setGetCardDetailResult(ArrayOfCardDetail value) {
        this.getCardDetailResult = value;
    }

}
