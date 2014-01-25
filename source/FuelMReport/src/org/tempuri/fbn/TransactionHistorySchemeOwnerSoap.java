/**
 * TransactionHistorySchemeOwnerSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri.fbn;

public interface TransactionHistorySchemeOwnerSoap extends java.rmi.Remote {
    public org.tempuri.fbn.TransactionHistoryData[] getTransactionHistory(java.lang.String institutionCode, java.lang.String cardPAN, java.util.Calendar dateFrom, java.util.Calendar dateTo, java.lang.String institutionData, java.lang.String key) throws java.rmi.RemoteException;
}
