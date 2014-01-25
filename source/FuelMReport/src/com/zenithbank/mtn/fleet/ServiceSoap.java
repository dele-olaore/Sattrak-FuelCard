/**
 * ServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zenithbank.mtn.fleet;

public interface ServiceSoap extends java.rmi.Remote {

    /**
     * Get all card transaction from the last updated ptid
     */
    public com.zenithbank.mtn.fleet.Transaction[] getCardTrans(boolean includeOpeningBalance) throws java.rmi.RemoteException;

    /**
     * Update the max ptid of the transaction fetched
     */
    public boolean updateMaxTransaction(java.math.BigDecimal transactionID) throws java.rmi.RemoteException;

    /**
     * Get card detail
     */
    public com.zenithbank.mtn.fleet.CardDetail[] getCardDetail() throws java.rmi.RemoteException;
    public com.zenithbank.mtn.fleet.Transaction[] getDailyTransaction(boolean includeOpeningBalance) throws java.rmi.RemoteException;
}
