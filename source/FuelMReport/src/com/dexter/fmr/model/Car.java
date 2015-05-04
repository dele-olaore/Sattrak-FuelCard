package com.dexter.fmr.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="CAR_TB")
public class Car implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private int zonControlId;
	@Column(unique=true)
	private String regNumber;
	
	private String fuelType;
	private boolean assigned;
	
	@OneToOne
	private User assignedUser;
	
	private String cardPan;
	
	@ManyToOne
	private Region region;
	@ManyToOne
	private Department department;
	
	private double vehicleCapacity;
	private double calibratedCapacity;
	
	// =============================== //
	
	private BigDecimal minbalance;
	private String thresholdAlertEmail;
	
	private String transactionAlertEmail;
	private String expectionAlertEmail;
	
	private String alertMobileNumbers;
	
	@Transient
	private BigDecimal cardBalance;
	
	public Car()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getZonControlId() {
		return zonControlId;
	}

	public void setZonControlId(int zonControlId) {
		this.zonControlId = zonControlId;
	}

	public String getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public boolean isAssigned() {
		return assigned;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}

	public User getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(User assignedUser) {
		this.assignedUser = assignedUser;
	}

	public String getCardPan() {
		return cardPan;
	}

	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public double getVehicleCapacity() {
		return vehicleCapacity;
	}

	public void setVehicleCapacity(double vehicleCapacity) {
		this.vehicleCapacity = vehicleCapacity;
	}

	public double getCalibratedCapacity() {
		return calibratedCapacity;
	}

	public void setCalibratedCapacity(double calibratedCapacity) {
		this.calibratedCapacity = calibratedCapacity;
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

	public BigDecimal getCardBalance() {
		return cardBalance;
	}

	public void setCardBalance(BigDecimal cardBalance) {
		this.cardBalance = cardBalance;
	}
	
}
