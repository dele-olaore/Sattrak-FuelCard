package com.dexter.fuelcard.model;

import java.io.Serializable;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="ROLE_TB")
public class Role implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
	private Long id;
	
	private String name;
	
	private String description;
	private boolean driverRole;
	
	@ManyToOne
	private Partner partner;
	
	@Transient
	private Vector<RoleFunction> functions;
	
	public Role()
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public boolean isDriverRole() {
		return driverRole;
	}

	public void setDriverRole(boolean driverRole) {
		this.driverRole = driverRole;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public Vector<RoleFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(Vector<RoleFunction> functions) {
		this.functions = functions;
	}
	
}
