package org.tempuri.fbn;

public class TransactionHistorySchemeOwnerSoapProxy implements org.tempuri.fbn.TransactionHistorySchemeOwnerSoap {
  private String _endpoint = null;
  private org.tempuri.fbn.TransactionHistorySchemeOwnerSoap transactionHistorySchemeOwnerSoap = null;
  
  public TransactionHistorySchemeOwnerSoapProxy() {
    _initTransactionHistorySchemeOwnerSoapProxy();
  }
  
  public TransactionHistorySchemeOwnerSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initTransactionHistorySchemeOwnerSoapProxy();
  }
  
  private void _initTransactionHistorySchemeOwnerSoapProxy() {
    try {
      transactionHistorySchemeOwnerSoap = (new org.tempuri.fbn.TransactionHistorySchemeOwnerLocator()).getTransactionHistorySchemeOwnerSoap();
      if (transactionHistorySchemeOwnerSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)transactionHistorySchemeOwnerSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)transactionHistorySchemeOwnerSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (transactionHistorySchemeOwnerSoap != null)
      ((javax.xml.rpc.Stub)transactionHistorySchemeOwnerSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.fbn.TransactionHistorySchemeOwnerSoap getTransactionHistorySchemeOwnerSoap() {
    if (transactionHistorySchemeOwnerSoap == null)
      _initTransactionHistorySchemeOwnerSoapProxy();
    return transactionHistorySchemeOwnerSoap;
  }
  
  public org.tempuri.fbn.TransactionHistoryData[] getTransactionHistory(java.lang.String institutionCode, java.lang.String cardPAN, java.util.Calendar dateFrom, java.util.Calendar dateTo, java.lang.String institutionData, java.lang.String key) throws java.rmi.RemoteException{
    if (transactionHistorySchemeOwnerSoap == null)
      _initTransactionHistorySchemeOwnerSoapProxy();
    return transactionHistorySchemeOwnerSoap.getTransactionHistory(institutionCode, cardPAN, dateFrom, dateTo, institutionData, key);
  }
  
  
}