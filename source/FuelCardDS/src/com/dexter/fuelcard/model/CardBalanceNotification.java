package com.dexter.fuelcard.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="CARDBALNOTIFICATION_TB")
public class CardBalanceNotification implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue
	private Long id;
	
	@ManyToOne
    private Region region;
	
	private BigDecimal minbalance;
	private String thresholdAlertEmail;

    private String transactionAlertEmail;
    private String expectionAlertEmail;
    
    private String alertMobileNumbers;
	
	@ManyToOne
	private Partner partner;
	
	@Transient
    private long new_region_id;
	
	public CardBalanceNotification()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
		if(this.region != null && new_region_id <= 0L)
			new_region_id = this.region.getId();
	}

	public BigDecimal getMinbalance() {
		return minbalance;
	}

	public void setMinbalance(BigDecimal minbalance) {
		this.minbalance = minbalance;
	}

	public String getThresholdAlertEmail() {
		return thresholdAlertEmail;
	}

	public void setThresholdAlertEmail(String thresholdAlertEmail) {
		this.thresholdAlertEmail = thresholdAlertEmail;
	}

	public String getTransactionAlertEmail() {
		return transactionAlertEmail;
	}

	public void setTransactionAlertEmail(String transactionAlertEmail) {
		this.transactionAlertEmail = transactionAlertEmail;
	}

	public String getExpectionAlertEmail() {
		return expectionAlertEmail;
	}

	public void setExpectionAlertEmail(String expectionAlertEmail) {
		this.expectionAlertEmail = expectionAlertEmail;
	}

	public String getAlertMobileNumbers() {
		return alertMobileNumbers;
	}

	public void setAlertMobileNumbers(String alertMobileNumbers) {
		this.alertMobileNumbers = alertMobileNumbers;
	}

	public long getNew_region_id() {
		return new_region_id;
	}

	public void setNew_region_id(long new_region_id) {
		this.new_region_id = new_region_id;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}
	
}
