package com.dexter.fuelcard.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="CARD_TB")
public class Card implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String cardPan;
	
	private double balance;
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date lastTranTime;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public Card()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCardPan() {
		return cardPan;
	}

	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Date getLastTranTime() {
		return lastTranTime;
	}

	public void setLastTranTime(Date lastTranTime) {
		this.lastTranTime = lastTranTime;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
