package com.dexter.fuelcard.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="INVOICE_TB")
public class Invoice implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue
	private Long id;
	
	private String invoice_no;
	@ManyToOne
	private Partner partner;
	private BigDecimal amount;
	private int month;
	private int year;
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date generated_dt;
	private boolean paid;
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date paid_dt;
	
	private byte[] document;
	
	@ManyToOne
	private User createdBy;
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	@Transient
	private String formatedDate;
	
	public Invoice()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInvoice_no() {
		return invoice_no;
	}

	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Date getGenerated_dt() {
		return generated_dt;
	}

	public void setGenerated_dt(Date generated_dt) {
		this.generated_dt = generated_dt;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Date getPaid_dt() {
		return paid_dt;
	}

	public void setPaid_dt(Date paid_dt) {
		this.paid_dt = paid_dt;
	}

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(byte[] document) {
		this.document = document;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}

	public String getFormatedDate() {
		if(formatedDate == null)
		{
			Calendar can = Calendar.getInstance();
			can.set(Calendar.MONTH, month-1);
			can.set(Calendar.YEAR, year);
			formatedDate = can.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + ", " + year;
		}
		return formatedDate;
	}

	public void setFormatedDate(String formatedDate) {
		this.formatedDate = formatedDate;
	}
	
}
