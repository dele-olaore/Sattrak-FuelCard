package com.dexter.fuelcard.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="CARDREQUEST_TB")
public class CardRequest implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
	private String requestType; // ORDER-CARDS, CANCEL-CARDS, LOAD-CARDS, OFFLOAD-CARDS
	
	@Column(unique=true)
	private String requestRefNum; // should be unique
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date request_dt;
	
	private byte[] excelFile;
	private String additionalComment;
	
	@ManyToOne
	private User requestedBy;
	@ManyToOne
	private Partner partner;
	
	private String status; // PENDING, PROCESSING, CANCELED, DONE
	
	private String sattrak_response1, sattrak_response2;
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date response_dt;
	@ManyToOne
	private User respondedBy;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public CardRequest()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestRefNum() {
		return requestRefNum;
	}

	public void setRequestRefNum(String requestRefNum) {
		this.requestRefNum = requestRefNum;
	}

	public Date getRequest_dt() {
		return request_dt;
	}

	public void setRequest_dt(Date request_dt) {
		this.request_dt = request_dt;
	}

	public byte[] getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(byte[] excelFile) {
		this.excelFile = excelFile;
	}

	public String getAdditionalComment() {
		return additionalComment;
	}

	public void setAdditionalComment(String additionalComment) {
		this.additionalComment = additionalComment;
	}

	public User getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSattrak_response1() {
		return sattrak_response1;
	}

	public void setSattrak_response1(String sattrak_response1) {
		this.sattrak_response1 = sattrak_response1;
	}

	public String getSattrak_response2() {
		return sattrak_response2;
	}

	public void setSattrak_response2(String sattrak_response2) {
		this.sattrak_response2 = sattrak_response2;
	}

	public Date getResponse_dt() {
		return response_dt;
	}

	public void setResponse_dt(Date response_dt) {
		this.response_dt = response_dt;
	}

	public User getRespondedBy() {
		return respondedBy;
	}

	public void setRespondedBy(User respondedBy) {
		this.respondedBy = respondedBy;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}
	
}
