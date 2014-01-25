package com.dexter.fmr.model;

import java.io.Serializable;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="ROLE_TB")
public class Role implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String name;
	
	private String description;
	private boolean driverRole;
	
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

	public Vector<RoleFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(Vector<RoleFunction> functions) {
		this.functions = functions;
	}
	
}
