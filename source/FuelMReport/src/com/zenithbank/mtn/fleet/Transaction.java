/**
 * Transaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zenithbank.mtn.fleet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="ZENBK_TRAN_TB")
public class Transaction implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
    private java.lang.String transactionType;

    private java.lang.String transactionDate;

    private java.math.BigDecimal amount;

    private java.math.BigDecimal fees;

    private java.math.BigDecimal surcharge;

    private java.lang.String pan;

    private java.lang.String transactionStatus;

    private java.lang.String settlementStatus;

    private java.lang.String responseCode;

    private java.lang.String responseDescription;

    private java.lang.String retrievalReference;

    private java.lang.String stan;

    private java.lang.String cardAcceptorID;

    private java.lang.String cardAcceptorLocation;

    private java.lang.String terminalID;

    private java.lang.String schemeOwnerName;

    private java.math.BigDecimal transID;

    private java.lang.String accountNumber;

    private java.lang.String accountType;

    private java.math.BigDecimal openingBalance;

    private java.math.BigDecimal closingBalance;

    private java.math.BigDecimal availBalance;

    private java.math.BigDecimal curbalance;

    public Transaction() {
    }

    public Transaction(
           java.lang.String transactionType,
           java.lang.String transactionDate,
           java.math.BigDecimal amount,
           java.math.BigDecimal fees,
           java.math.BigDecimal surcharge,
           java.lang.String pan,
           java.lang.String transactionStatus,
           java.lang.String settlementStatus,
           java.lang.String responseCode,
           java.lang.String responseDescription,
           java.lang.String retrievalReference,
           java.lang.String stan,
           java.lang.String cardAcceptorID,
           java.lang.String cardAcceptorLocation,
           java.lang.String terminalID,
           java.lang.String schemeOwnerName,
           java.math.BigDecimal transID,
           java.lang.String accountNumber,
           java.lang.String accountType,
           java.math.BigDecimal openingBalance,
           java.math.BigDecimal closingBalance,
           java.math.BigDecimal availBalance,
           java.math.BigDecimal curbalance) {
           this.transactionType = transactionType;
           this.transactionDate = transactionDate;
           this.amount = amount;
           this.fees = fees;
           this.surcharge = surcharge;
           this.pan = pan;
           this.transactionStatus = transactionStatus;
           this.settlementStatus = settlementStatus;
           this.responseCode = responseCode;
           this.responseDescription = responseDescription;
           this.retrievalReference = retrievalReference;
           this.stan = stan;
           this.cardAcceptorID = cardAcceptorID;
           this.cardAcceptorLocation = cardAcceptorLocation;
           this.terminalID = terminalID;
           this.schemeOwnerName = schemeOwnerName;
           this.transID = transID;
           this.accountNumber = accountNumber;
           this.accountType = accountType;
           this.openingBalance = openingBalance;
           this.closingBalance = closingBalance;
           this.availBalance = availBalance;
           this.curbalance = curbalance;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
     * Gets the transactionType value for this Transaction.
     * 
     * @return transactionType
     */
    public java.lang.String getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType value for this Transaction.
     * 
     * @param transactionType
     */
    public void setTransactionType(java.lang.String transactionType) {
        this.transactionType = transactionType;
    }


    /**
     * Gets the transactionDate value for this Transaction.
     * 
     * @return transactionDate
     */
    public java.lang.String getTransactionDate() {
        return transactionDate;
    }


    /**
     * Sets the transactionDate value for this Transaction.
     * 
     * @param transactionDate
     */
    public void setTransactionDate(java.lang.String transactionDate) {
        this.transactionDate = transactionDate;
    }


    /**
     * Gets the amount value for this Transaction.
     * 
     * @return amount
     */
    public java.math.BigDecimal getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this Transaction.
     * 
     * @param amount
     */
    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }


    /**
     * Gets the fees value for this Transaction.
     * 
     * @return fees
     */
    public java.math.BigDecimal getFees() {
        return fees;
    }


    /**
     * Sets the fees value for this Transaction.
     * 
     * @param fees
     */
    public void setFees(java.math.BigDecimal fees) {
        this.fees = fees;
    }


    /**
     * Gets the surcharge value for this Transaction.
     * 
     * @return surcharge
     */
    public java.math.BigDecimal getSurcharge() {
        return surcharge;
    }


    /**
     * Sets the surcharge value for this Transaction.
     * 
     * @param surcharge
     */
    public void setSurcharge(java.math.BigDecimal surcharge) {
        this.surcharge = surcharge;
    }


    /**
     * Gets the pan value for this Transaction.
     * 
     * @return pan
     */
    public java.lang.String getPan() {
        return pan;
    }


    /**
     * Sets the pan value for this Transaction.
     * 
     * @param pan
     */
    public void setPan(java.lang.String pan) {
        this.pan = pan;
    }


    /**
     * Gets the transactionStatus value for this Transaction.
     * 
     * @return transactionStatus
     */
    public java.lang.String getTransactionStatus() {
        return transactionStatus;
    }


    /**
     * Sets the transactionStatus value for this Transaction.
     * 
     * @param transactionStatus
     */
    public void setTransactionStatus(java.lang.String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }


    /**
     * Gets the settlementStatus value for this Transaction.
     * 
     * @return settlementStatus
     */
    public java.lang.String getSettlementStatus() {
        return settlementStatus;
    }


    /**
     * Sets the settlementStatus value for this Transaction.
     * 
     * @param settlementStatus
     */
    public void setSettlementStatus(java.lang.String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }


    /**
     * Gets the responseCode value for this Transaction.
     * 
     * @return responseCode
     */
    public java.lang.String getResponseCode() {
        return responseCode;
    }


    /**
     * Sets the responseCode value for this Transaction.
     * 
     * @param responseCode
     */
    public void setResponseCode(java.lang.String responseCode) {
        this.responseCode = responseCode;
    }


    /**
     * Gets the responseDescription value for this Transaction.
     * 
     * @return responseDescription
     */
    public java.lang.String getResponseDescription() {
        return responseDescription;
    }


    /**
     * Sets the responseDescription value for this Transaction.
     * 
     * @param responseDescription
     */
    public void setResponseDescription(java.lang.String responseDescription) {
        this.responseDescription = responseDescription;
    }


    /**
     * Gets the retrievalReference value for this Transaction.
     * 
     * @return retrievalReference
     */
    public java.lang.String getRetrievalReference() {
        return retrievalReference;
    }


    /**
     * Sets the retrievalReference value for this Transaction.
     * 
     * @param retrievalReference
     */
    public void setRetrievalReference(java.lang.String retrievalReference) {
        this.retrievalReference = retrievalReference;
    }


    /**
     * Gets the stan value for this Transaction.
     * 
     * @return stan
     */
    public java.lang.String getStan() {
        return stan;
    }


    /**
     * Sets the stan value for this Transaction.
     * 
     * @param stan
     */
    public void setStan(java.lang.String stan) {
        this.stan = stan;
    }


    /**
     * Gets the cardAcceptorID value for this Transaction.
     * 
     * @return cardAcceptorID
     */
    public java.lang.String getCardAcceptorID() {
        return cardAcceptorID;
    }


    /**
     * Sets the cardAcceptorID value for this Transaction.
     * 
     * @param cardAcceptorID
     */
    public void setCardAcceptorID(java.lang.String cardAcceptorID) {
        this.cardAcceptorID = cardAcceptorID;
    }


    /**
     * Gets the cardAcceptorLocation value for this Transaction.
     * 
     * @return cardAcceptorLocation
     */
    public java.lang.String getCardAcceptorLocation() {
        return cardAcceptorLocation;
    }


    /**
     * Sets the cardAcceptorLocation value for this Transaction.
     * 
     * @param cardAcceptorLocation
     */
    public void setCardAcceptorLocation(java.lang.String cardAcceptorLocation) {
        this.cardAcceptorLocation = cardAcceptorLocation;
    }


    /**
     * Gets the terminalID value for this Transaction.
     * 
     * @return terminalID
     */
    public java.lang.String getTerminalID() {
        return terminalID;
    }


    /**
     * Sets the terminalID value for this Transaction.
     * 
     * @param terminalID
     */
    public void setTerminalID(java.lang.String terminalID) {
        this.terminalID = terminalID;
    }


    /**
     * Gets the schemeOwnerName value for this Transaction.
     * 
     * @return schemeOwnerName
     */
    public java.lang.String getSchemeOwnerName() {
        return schemeOwnerName;
    }


    /**
     * Sets the schemeOwnerName value for this Transaction.
     * 
     * @param schemeOwnerName
     */
    public void setSchemeOwnerName(java.lang.String schemeOwnerName) {
        this.schemeOwnerName = schemeOwnerName;
    }


    /**
     * Gets the transID value for this Transaction.
     * 
     * @return transID
     */
    public java.math.BigDecimal getTransID() {
        return transID;
    }


    /**
     * Sets the transID value for this Transaction.
     * 
     * @param transID
     */
    public void setTransID(java.math.BigDecimal transID) {
        this.transID = transID;
    }


    /**
     * Gets the accountNumber value for this Transaction.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this Transaction.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the accountType value for this Transaction.
     * 
     * @return accountType
     */
    public java.lang.String getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType value for this Transaction.
     * 
     * @param accountType
     */
    public void setAccountType(java.lang.String accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the openingBalance value for this Transaction.
     * 
     * @return openingBalance
     */
    public java.math.BigDecimal getOpeningBalance() {
        return openingBalance;
    }


    /**
     * Sets the openingBalance value for this Transaction.
     * 
     * @param openingBalance
     */
    public void setOpeningBalance(java.math.BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }


    /**
     * Gets the closingBalance value for this Transaction.
     * 
     * @return closingBalance
     */
    public java.math.BigDecimal getClosingBalance() {
        return closingBalance;
    }


    /**
     * Sets the closingBalance value for this Transaction.
     * 
     * @param closingBalance
     */
    public void setClosingBalance(java.math.BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }


    /**
     * Gets the availBalance value for this Transaction.
     * 
     * @return availBalance
     */
    public java.math.BigDecimal getAvailBalance() {
        return availBalance;
    }


    /**
     * Sets the availBalance value for this Transaction.
     * 
     * @param availBalance
     */
    public void setAvailBalance(java.math.BigDecimal availBalance) {
        this.availBalance = availBalance;
    }


    /**
     * Gets the curbalance value for this Transaction.
     * 
     * @return curbalance
     */
    public java.math.BigDecimal getCurbalance() {
        return curbalance;
    }


    /**
     * Sets the curbalance value for this Transaction.
     * 
     * @param curbalance
     */
    public void setCurbalance(java.math.BigDecimal curbalance) {
        this.curbalance = curbalance;
    }

    @Transient
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Transaction)) return false;
        Transaction other = (Transaction) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.transactionType==null && other.getTransactionType()==null) || 
             (this.transactionType!=null &&
              this.transactionType.equals(other.getTransactionType()))) &&
            ((this.transactionDate==null && other.getTransactionDate()==null) || 
             (this.transactionDate!=null &&
              this.transactionDate.equals(other.getTransactionDate()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.fees==null && other.getFees()==null) || 
             (this.fees!=null &&
              this.fees.equals(other.getFees()))) &&
            ((this.surcharge==null && other.getSurcharge()==null) || 
             (this.surcharge!=null &&
              this.surcharge.equals(other.getSurcharge()))) &&
            ((this.pan==null && other.getPan()==null) || 
             (this.pan!=null &&
              this.pan.equals(other.getPan()))) &&
            ((this.transactionStatus==null && other.getTransactionStatus()==null) || 
             (this.transactionStatus!=null &&
              this.transactionStatus.equals(other.getTransactionStatus()))) &&
            ((this.settlementStatus==null && other.getSettlementStatus()==null) || 
             (this.settlementStatus!=null &&
              this.settlementStatus.equals(other.getSettlementStatus()))) &&
            ((this.responseCode==null && other.getResponseCode()==null) || 
             (this.responseCode!=null &&
              this.responseCode.equals(other.getResponseCode()))) &&
            ((this.responseDescription==null && other.getResponseDescription()==null) || 
             (this.responseDescription!=null &&
              this.responseDescription.equals(other.getResponseDescription()))) &&
            ((this.retrievalReference==null && other.getRetrievalReference()==null) || 
             (this.retrievalReference!=null &&
              this.retrievalReference.equals(other.getRetrievalReference()))) &&
            ((this.stan==null && other.getStan()==null) || 
             (this.stan!=null &&
              this.stan.equals(other.getStan()))) &&
            ((this.cardAcceptorID==null && other.getCardAcceptorID()==null) || 
             (this.cardAcceptorID!=null &&
              this.cardAcceptorID.equals(other.getCardAcceptorID()))) &&
            ((this.cardAcceptorLocation==null && other.getCardAcceptorLocation()==null) || 
             (this.cardAcceptorLocation!=null &&
              this.cardAcceptorLocation.equals(other.getCardAcceptorLocation()))) &&
            ((this.terminalID==null && other.getTerminalID()==null) || 
             (this.terminalID!=null &&
              this.terminalID.equals(other.getTerminalID()))) &&
            ((this.schemeOwnerName==null && other.getSchemeOwnerName()==null) || 
             (this.schemeOwnerName!=null &&
              this.schemeOwnerName.equals(other.getSchemeOwnerName()))) &&
            ((this.transID==null && other.getTransID()==null) || 
             (this.transID!=null &&
              this.transID.equals(other.getTransID()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.accountType==null && other.getAccountType()==null) || 
             (this.accountType!=null &&
              this.accountType.equals(other.getAccountType()))) &&
            ((this.openingBalance==null && other.getOpeningBalance()==null) || 
             (this.openingBalance!=null &&
              this.openingBalance.equals(other.getOpeningBalance()))) &&
            ((this.closingBalance==null && other.getClosingBalance()==null) || 
             (this.closingBalance!=null &&
              this.closingBalance.equals(other.getClosingBalance()))) &&
            ((this.availBalance==null && other.getAvailBalance()==null) || 
             (this.availBalance!=null &&
              this.availBalance.equals(other.getAvailBalance()))) &&
            ((this.curbalance==null && other.getCurbalance()==null) || 
             (this.curbalance!=null &&
              this.curbalance.equals(other.getCurbalance())));
        __equalsCalc = null;
        return _equals;
    }

    @Transient
    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getTransactionType() != null) {
            _hashCode += getTransactionType().hashCode();
        }
        if (getTransactionDate() != null) {
            _hashCode += getTransactionDate().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getFees() != null) {
            _hashCode += getFees().hashCode();
        }
        if (getSurcharge() != null) {
            _hashCode += getSurcharge().hashCode();
        }
        if (getPan() != null) {
            _hashCode += getPan().hashCode();
        }
        if (getTransactionStatus() != null) {
            _hashCode += getTransactionStatus().hashCode();
        }
        if (getSettlementStatus() != null) {
            _hashCode += getSettlementStatus().hashCode();
        }
        if (getResponseCode() != null) {
            _hashCode += getResponseCode().hashCode();
        }
        if (getResponseDescription() != null) {
            _hashCode += getResponseDescription().hashCode();
        }
        if (getRetrievalReference() != null) {
            _hashCode += getRetrievalReference().hashCode();
        }
        if (getStan() != null) {
            _hashCode += getStan().hashCode();
        }
        if (getCardAcceptorID() != null) {
            _hashCode += getCardAcceptorID().hashCode();
        }
        if (getCardAcceptorLocation() != null) {
            _hashCode += getCardAcceptorLocation().hashCode();
        }
        if (getTerminalID() != null) {
            _hashCode += getTerminalID().hashCode();
        }
        if (getSchemeOwnerName() != null) {
            _hashCode += getSchemeOwnerName().hashCode();
        }
        if (getTransID() != null) {
            _hashCode += getTransID().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getAccountType() != null) {
            _hashCode += getAccountType().hashCode();
        }
        if (getOpeningBalance() != null) {
            _hashCode += getOpeningBalance().hashCode();
        }
        if (getClosingBalance() != null) {
            _hashCode += getClosingBalance().hashCode();
        }
        if (getAvailBalance() != null) {
            _hashCode += getAvailBalance().hashCode();
        }
        if (getCurbalance() != null) {
            _hashCode += getCurbalance().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    @Transient
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Transaction.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Transaction"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "TransactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "TransactionDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fees");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Fees"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("surcharge");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Surcharge"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pan");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Pan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "TransactionStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("settlementStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "SettlementStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "ResponseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "ResponseDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retrievalReference");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "RetrievalReference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stan");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Stan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardAcceptorID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "CardAcceptorID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardAcceptorLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "CardAcceptorLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "TerminalID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("schemeOwnerName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "SchemeOwnerName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "TransID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "AccountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "AccountType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("openingBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "OpeningBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("closingBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "ClosingBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("availBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "AvailBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("curbalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Curbalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    @Transient
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    @Transient
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
