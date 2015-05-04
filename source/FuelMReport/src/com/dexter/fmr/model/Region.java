package com.dexter.fmr.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="REGION_TB")
public class Region implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String name;
	
	public Region()
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
	
}
