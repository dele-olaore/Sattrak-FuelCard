
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
 *         &lt;element name="UpdateMaxTransactionResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "updateMaxTransactionResult"
})
@XmlRootElement(name = "UpdateMaxTransactionResponse")
public class UpdateMaxTransactionResponse {

    @XmlElement(name = "UpdateMaxTransactionResult")
    protected boolean updateMaxTransactionResult;

    /**
     * Gets the value of the updateMaxTransactionResult property.
     * 
     */
    public boolean isUpdateMaxTransactionResult() {
        return updateMaxTransactionResult;
    }

    /**
     * Sets the value of the updateMaxTransactionResult property.
     * 
     */
    public void setUpdateMaxTransactionResult(boolean value) {
        this.updateMaxTransactionResult = value;
    }

}
