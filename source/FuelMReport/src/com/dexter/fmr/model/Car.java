package com.dexter.fmr.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
	
}
