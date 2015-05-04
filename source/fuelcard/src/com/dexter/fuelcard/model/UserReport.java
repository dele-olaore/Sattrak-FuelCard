package com.dexter.fuelcard.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="USER_REPORT_TB")
public class UserReport implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
    @NotNull
    @GeneratedValue
	private Long id;
	
	@ManyToOne
	private User user;
	private String reportTitle;
	private String fields;
	
	private int durationType; // 1-DAY, 2-WEEK, 3-MONTH
	//private int durationCount; // Regular int which determines the amount of days, weeks, or months
	
	@ManyToOne
	private Car car;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date lastDeliveredDate; // the last time the report was generated and sent
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date nextRunDate; // next time report will run
	
	private boolean active;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date crt_dt;
	
	public UserReport()
	{}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public int getDurationType() {
		return durationType;
	}

	public void setDurationType(int durationType) {
		this.durationType = durationType;
	}

	/*public int getDurationCount() {
		return durationCount;
	}

	public void setDurationCount(int durationCount) {
		this.durationCount = durationCount;
	}*/

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public Date getLastDeliveredDate() {
		return lastDeliveredDate;
	}

	public void setLastDeliveredDate(Date lastDeliveredDate) {
		this.lastDeliveredDate = lastDeliveredDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCrt_dt() {
		return crt_dt;
	}

	public void setCrt_dt(Date crt_dt) {
		this.crt_dt = crt_dt;
	}

	public Date getNextRunDate() {
		return nextRunDate;
	}

	public void setNextRunDate(Date nextRunDate) {
		this.nextRunDate = nextRunDate;
	}
	
}
