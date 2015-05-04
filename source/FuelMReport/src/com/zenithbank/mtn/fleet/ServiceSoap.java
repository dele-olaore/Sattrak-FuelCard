
package com.zenithbank.mtn.fleet;

import java.math.BigDecimal;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ServiceSoap", targetNamespace = "http://zenithbank.com/mtn/fleet")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ServiceSoap {


    /**
     * Get all card transaction from the last updated ptid 
     * 
     * @param includeOpeningBalance
     * @return
     *     returns com.zenithbank.mtn.fleet.ArrayOfTransaction
     */
    @WebMethod(operationName = "GetCardTrans", action = "http://zenithbank.com/mtn/fleet/GetCardTrans")
    @WebResult(name = "GetCardTransResult", targetNamespace = "http://zenithbank.com/mtn/fleet")
    @RequestWrapper(localName = "GetCardTrans", targetNamespace = "http://zenithbank.com/mtn/fleet", className = "com.zenithbank.mtn.fleet.GetCardTrans")
    @ResponseWrapper(localName = "GetCardTransResponse", targetNamespace = "http://zenithbank.com/mtn/fleet", className = "com.zenithbank.mtn.fleet.GetCardTransResponse")
    public ArrayOfTransaction getCardTrans(
        @WebParam(name = "IncludeOpeningBalance", targetNamespace = "http://zenithbank.com/mtn/fleet")
        boolean includeOpeningBalance);

    /**
     * Update the max ptid of the transaction fetched
     * 
     * @param transactionID
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "UpdateMaxTransaction", action = "http://zenithbank.com/mtn/fleet/UpdateMaxTransaction")
    @WebResult(name = "UpdateMaxTransactionResult", targetNamespace = "http://zenithbank.com/mtn/fleet")
    @RequestWrapper(localName = "UpdateMaxTransaction", targetNamespace = "http://zenithbank.com/mtn/fleet", className = "com.zenithbank.mtn.fleet.UpdateMaxTransaction")
    @ResponseWrapper(localName = "UpdateMaxTransactionResponse", targetNamespace = "http://zenithbank.com/mtn/fleet", className = "com.zenithbank.mtn.fleet.UpdateMaxTransactionResponse")
    public boolean updateMaxTransaction(
        @WebParam(name = "transactionID", targetNamespace = "http://zenithbank.com/mtn/fleet")
        BigDecimal transactionID);

    /**
     * Get card detail
     * 
     * @return
     *     returns com.zenithbank.mtn.fleet.ArrayOfCardDetail
     */
    @WebMethod(operationName = "GetCardDetail", action = "http://zenithbank.com/mtn/fleet/GetCardDetail")
    @WebResult(name = "GetCardDetailResult", targetNamespace = "http://zenithbank.com/mtn/fleet")
    @RequestWrapper(localName = "GetCardDetail", targetNamespace = "http://zenithbank.com/mtn/fleet", className = "com.zenithbank.mtn.fleet.GetCardDetail")
    @ResponseWrapper(localName = "GetCardDetailResponse", targetNamespace = "http://zenithbank.com/mtn/fleet", className = "com.zenithbank.mtn.fleet.GetCardDetailResponse")
    public ArrayOfCardDetail getCardDetail();

    /**
     * 
     * @param includeOpeningBalance
     * @return
     *     returns com.zenithbank.mtn.fleet.ArrayOfTransaction
     */
    @WebMethod(operationName = "GetDailyTransaction", action = "http://zenithbank.com/mtn/fleet/GetDailyTransaction")
    @WebResult(name = "GetDailyTransactionResult", targetNamespace = "http://zenithbank.com/mtn/fleet")
    @RequestWrapper(localName = "GetDailyTransaction", targetNamespace = "http://zenithbank.com/mtn/fleet", className = "com.zenithbank.mtn.fleet.GetDailyTransaction")
    @ResponseWrapper(localName = "GetDailyTransactionResponse", targetNamespace = "http://zenithbank.com/mtn/fleet", className = "com.zenithbank.mtn.fleet.GetDailyTransactionResponse")
    public ArrayOfTransaction getDailyTransaction(
        @WebParam(name = "IncludeOpeningBalance", targetNamespace = "http://zenithbank.com/mtn/fleet")
        boolean includeOpeningBalance);

}
