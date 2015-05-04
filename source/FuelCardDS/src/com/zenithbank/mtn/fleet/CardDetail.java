/**
 * CardDetail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zenithbank.mtn.fleet;

public class CardDetail  implements java.io.Serializable {
    private java.lang.String lastNames;

    private java.lang.String otherName;

    private java.lang.String homeAddress;

    private java.lang.String phoneNumber;

    private java.lang.String email;

    private java.lang.String gender;

    private java.lang.String birthdate;

    private java.lang.String title;

    public CardDetail() {
    }

    public CardDetail(
           java.lang.String lastNames,
           java.lang.String otherName,
           java.lang.String homeAddress,
           java.lang.String phoneNumber,
           java.lang.String email,
           java.lang.String gender,
           java.lang.String birthdate,
           java.lang.String title) {
           this.lastNames = lastNames;
           this.otherName = otherName;
           this.homeAddress = homeAddress;
           this.phoneNumber = phoneNumber;
           this.email = email;
           this.gender = gender;
           this.birthdate = birthdate;
           this.title = title;
    }


    /**
     * Gets the lastNames value for this CardDetail.
     * 
     * @return lastNames
     */
    public java.lang.String getLastNames() {
        return lastNames;
    }


    /**
     * Sets the lastNames value for this CardDetail.
     * 
     * @param lastNames
     */
    public void setLastNames(java.lang.String lastNames) {
        this.lastNames = lastNames;
    }


    /**
     * Gets the otherName value for this CardDetail.
     * 
     * @return otherName
     */
    public java.lang.String getOtherName() {
        return otherName;
    }


    /**
     * Sets the otherName value for this CardDetail.
     * 
     * @param otherName
     */
    public void setOtherName(java.lang.String otherName) {
        this.otherName = otherName;
    }


    /**
     * Gets the homeAddress value for this CardDetail.
     * 
     * @return homeAddress
     */
    public java.lang.String getHomeAddress() {
        return homeAddress;
    }


    /**
     * Sets the homeAddress value for this CardDetail.
     * 
     * @param homeAddress
     */
    public void setHomeAddress(java.lang.String homeAddress) {
        this.homeAddress = homeAddress;
    }


    /**
     * Gets the phoneNumber value for this CardDetail.
     * 
     * @return phoneNumber
     */
    public java.lang.String getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Sets the phoneNumber value for this CardDetail.
     * 
     * @param phoneNumber
     */
    public void setPhoneNumber(java.lang.String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    /**
     * Gets the email value for this CardDetail.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this CardDetail.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the gender value for this CardDetail.
     * 
     * @return gender
     */
    public java.lang.String getGender() {
        return gender;
    }


    /**
     * Sets the gender value for this CardDetail.
     * 
     * @param gender
     */
    public void setGender(java.lang.String gender) {
        this.gender = gender;
    }


    /**
     * Gets the birthdate value for this CardDetail.
     * 
     * @return birthdate
     */
    public java.lang.String getBirthdate() {
        return birthdate;
    }


    /**
     * Sets the birthdate value for this CardDetail.
     * 
     * @param birthdate
     */
    public void setBirthdate(java.lang.String birthdate) {
        this.birthdate = birthdate;
    }


    /**
     * Gets the title value for this CardDetail.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this CardDetail.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CardDetail)) return false;
        CardDetail other = (CardDetail) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.lastNames==null && other.getLastNames()==null) || 
             (this.lastNames!=null &&
              this.lastNames.equals(other.getLastNames()))) &&
            ((this.otherName==null && other.getOtherName()==null) || 
             (this.otherName!=null &&
              this.otherName.equals(other.getOtherName()))) &&
            ((this.homeAddress==null && other.getHomeAddress()==null) || 
             (this.homeAddress!=null &&
              this.homeAddress.equals(other.getHomeAddress()))) &&
            ((this.phoneNumber==null && other.getPhoneNumber()==null) || 
             (this.phoneNumber!=null &&
              this.phoneNumber.equals(other.getPhoneNumber()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.gender==null && other.getGender()==null) || 
             (this.gender!=null &&
              this.gender.equals(other.getGender()))) &&
            ((this.birthdate==null && other.getBirthdate()==null) || 
             (this.birthdate!=null &&
              this.birthdate.equals(other.getBirthdate()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getLastNames() != null) {
            _hashCode += getLastNames().hashCode();
        }
        if (getOtherName() != null) {
            _hashCode += getOtherName().hashCode();
        }
        if (getHomeAddress() != null) {
            _hashCode += getHomeAddress().hashCode();
        }
        if (getPhoneNumber() != null) {
            _hashCode += getPhoneNumber().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getGender() != null) {
            _hashCode += getGender().hashCode();
        }
        if (getBirthdate() != null) {
            _hashCode += getBirthdate().hashCode();
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CardDetail.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "CardDetail"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastNames");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "LastNames"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("otherName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "OtherName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("homeAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "HomeAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phoneNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "PhoneNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gender");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Gender"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("birthdate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Birthdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("http://zenithbank.com/mtn/fleet", "Title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
