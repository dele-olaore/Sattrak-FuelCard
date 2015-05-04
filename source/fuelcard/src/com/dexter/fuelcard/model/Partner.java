package com.dexter.fuelcard.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="PARTNER_TB")
public class Partner implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String name;
	@Column(unique=true)
	private String code;
	@Column(unique=true)
	private String zenithAccountNumber;
	
	private String contactEmails; // emails separated by commas
	private String contactMobiles; // mobile phone numbers separated by commas
	
	private boolean sattrak;
	
	private int licenseCount; // this should be the license count in numeric
	
	private String billingType; // this is the type of billing, 'Percent-Per-Tran', 'Flat-Per-License'
	private double billingUnitAmt; // this is the numeric cost of the billing
	
	private boolean noTrackerUsed; // this is to know if the partner has tracker installed or not
	private double petrolUnitPrice, disealUnitPrice; // if partner doesn't have tracker installed, then these fields are required for the system to know the unit price of petrol / diseal
	
	private boolean active;
	
	public Partner()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getZenithAccountNumber() {
		return zenithAccountNumber;
	}

	public void setZenithAccountNumber(String zenithAccountNumber) {
		this.zenithAccountNumber = zenithAccountNumber;
	}

	public String getContactEmails() {
		return contactEmails;
	}

	public void setContactEmails(String contactEmails) {
		this.contactEmails = contactEmails;
	}

	public String getContactMobiles() {
		return contactMobiles;
	}

	public void setContactMobiles(String contactMobiles) {
		this.contactMobiles = contactMobiles;
	}

	public boolean isSattrak() {
		return sattrak;
	}

	public void setSattrak(boolean sattrak) {
		this.sattrak = sattrak;
	}

	public int getLicenseCount() {
		return licenseCount;
	}

	public void setLicenseCount(int licenseCount) {
		this.licenseCount = licenseCount;
	}

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	public double getBillingUnitAmt() {
		return billingUnitAmt;
	}

	public void setBillingUnitAmt(double billingUnitAmt) {
		this.billingUnitAmt = billingUnitAmt;
	}

	public boolean isNoTrackerUsed() {
		return noTrackerUsed;
	}

	public void setNoTrackerUsed(boolean noTrackerUsed) {
		this.noTrackerUsed = noTrackerUsed;
	}

	public double getPetrolUnitPrice() {
		return petrolUnitPrice;
	}

	public void setPetrolUnitPrice(double petrolUnitPrice) {
		this.petrolUnitPrice = petrolUnitPrice;
	}

	public double getDisealUnitPrice() {
		return disealUnitPrice;
	}

	public void setDisealUnitPrice(double disealUnitPrice) {
		this.disealUnitPrice = disealUnitPrice;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
