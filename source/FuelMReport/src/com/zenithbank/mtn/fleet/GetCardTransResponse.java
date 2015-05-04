
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
 *         &lt;element name="GetCardTransResult" type="{http://zenithbank.com/mtn/fleet}ArrayOfTransaction" minOccurs="0"/>
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
    "getCardTransResult"
})
@XmlRootElement(name = "GetCardTransResponse")
public class GetCardTransResponse {

    @XmlElement(name = "GetCardTransResult")
    protected ArrayOfTransaction getCardTransResult;

    /**
     * Gets the value of the getCardTransResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTransaction }
     *     
     */
    public ArrayOfTransaction getGetCardTransResult() {
        return getCardTransResult;
    }

    /**
     * Sets the value of the getCardTransResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTransaction }
     *     
     */
    public void setGetCardTransResult(ArrayOfTransaction value) {
        this.getCardTransResult = value;
    }

}
