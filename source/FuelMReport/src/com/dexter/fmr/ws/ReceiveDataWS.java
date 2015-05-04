package com.dexter.fmr.ws;

import java.math.BigDecimal;
import java.text.ParseException;

import com.dexter.fmr.dao.BankRecordDAO;
import com.dexter.fmr.dao.CarDAO;
import com.dexter.fmr.model.BankRecord;
import com.dexter.fmr.model.Car;

public class ReceiveDataWS
{
	public String receiveData(String customerName, String customerPhone,
			String transactionDate, String transactionType, double transactionAmount,
			double transactionFee, String transactionStatus, String cardPan,
			String cardStatus, String schemeOwnerName, String cardAcceptorId, 
			String cardAcceptorLocation, String retRefNumber, double cardBalance)
	{
		BankRecord e = new BankRecord();
		
		Car c = new CarDAO().getCarByRegNumber(customerName);
		
		e.setCusName(customerName);
		e.setCusPhone(customerPhone);
		try {
			e.setTranTime(java.text.DateFormat.getDateTimeInstance().parse(transactionDate));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		e.setTranType(transactionType);
		e.setTranAmt(transactionAmount);
		e.setTranFees(transactionFee);
		e.setTranStatus(transactionStatus);
		e.setCardPan(cardPan);
		e.setCardStatus(cardStatus);
		e.setSchemeOwner(schemeOwnerName);
		e.setCardAcceptorId(cardAcceptorId);
		e.setCardAcceptorLoc(cardAcceptorLocation);
		e.setRetrievalRefNum(retRefNumber);
		e.setCardBal(cardBalance);
		
		boolean ret = new BankRecordDAO().createBankRecord(e);
		
		return ""+ret;
	}
}
