package com.dexter.fmr.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="BANKRECORD_TB")
public class BankRecord implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
	private String cusName;
	private String cusPhone;
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date tranTime;
	private String tranTimeStr;
	private String tranType;
	private BigDecimal tranAmt;
	private BigDecimal tranFees;
	private String tranStatus;
	private String cardPan;
	private String cardStatus;
	private String schemeOwner;
	private String cardAcceptorId;
	private String cardAcceptorLoc;
	private String retrievalRefNum;
	private BigDecimal cardBal;
	
	@ManyToOne
	private Car vehicle;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public BankRecord()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCusName() {
		return cusName;
	}

	public void setCusName(String cusName) {
		this.cusName = cusName;
	}

	public String getCusPhone() {
		return cusPhone;
	}

	public void setCusPhone(String cusPhone) {
		this.cusPhone = cusPhone;
	}

	public Date getTranTime() {
		return tranTime;
	}

	public void setTranTime(Date tranTime) {
		this.tranTime = tranTime;
	}

	public String getTranTimeStr() {
		return tranTimeStr;
	}

	public void setTranTimeStr(String tranTimeStr) {
		this.tranTimeStr = tranTimeStr;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public BigDecimal getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(BigDecimal tranAmt) {
		this.tranAmt = tranAmt;
	}

	public BigDecimal getTranFees() {
		return tranFees;
	}

	public void setTranFees(BigDecimal tranFees) {
		this.tranFees = tranFees;
	}

	public String getTranStatus() {
		return tranStatus;
	}

	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}

	public String getCardPan() {
		return cardPan;
	}

	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getSchemeOwner() {
		return schemeOwner;
	}

	public void setSchemeOwner(String schemeOwner) {
		this.schemeOwner = schemeOwner;
	}

	public String getCardAcceptorId() {
		return cardAcceptorId;
	}

	public void setCardAcceptorId(String cardAcceptorId) {
		this.cardAcceptorId = cardAcceptorId;
	}

	public String getCardAcceptorLoc() {
		return cardAcceptorLoc;
	}

	public void setCardAcceptorLoc(String cardAcceptorLoc) {
		this.cardAcceptorLoc = cardAcceptorLoc;
	}

	public String getRetrievalRefNum() {
		return retrievalRefNum;
	}

	public void setRetrievalRefNum(String retrievalRefNum) {
		this.retrievalRefNum = retrievalRefNum;
	}

	public BigDecimal getCardBal() {
		return cardBal;
	}

	public void setCardBal(BigDecimal cardBal) {
		this.cardBal = cardBal;
	}

	public Car getVehicle() {
		return vehicle;
	}

	public void setVehicle(Car vehicle) {
		this.vehicle = vehicle;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
