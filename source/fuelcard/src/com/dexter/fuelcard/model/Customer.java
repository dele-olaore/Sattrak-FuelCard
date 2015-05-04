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
import javax.validation.constraints.NotNull;

@Entity
@Table(name="CUSTOMER_TB")
public class Customer implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
	private String customerName;
	@Column(unique=true)
	private String customerCode;
	
	private String licenseCount; // this should be the license count in numeric
	
	private String billingType; // this is the type of billing, 'Percent-Per-Tran', 'Flat-Per-License'
	private String billingUnitAmt; // this is the numeric cost of the billing
	
	private boolean activeStatus;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public Customer()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getLicenseCount() {
		return licenseCount;
	}

	public void setLicenseCount(String licenseCount) {
		this.licenseCount = licenseCount;
	}

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	public String getBillingUnitAmt() {
		return billingUnitAmt;
	}

	public void setBillingUnitAmt(String billingUnitAmt) {
		this.billingUnitAmt = billingUnitAmt;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
