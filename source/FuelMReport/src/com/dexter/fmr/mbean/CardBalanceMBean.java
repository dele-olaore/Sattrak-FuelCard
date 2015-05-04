package com.dexter.fmr.mbean;

import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.dexter.fmr.dao.AuditDAO;
import com.dexter.fmr.dao.CardBalanceDAO;
import com.dexter.fmr.dao.RegionDAO;
import com.dexter.fmr.model.AuditTrail;
import com.dexter.fmr.model.CardBalanceNotification;
import com.dexter.fmr.model.Region;
import com.dexter.fmr.model.User;
import com.dexter.fmr.util.Utils;
import com.sun.faces.context.FacesContextImpl;

@Name("cardBalanceMBean")
@Scope(ScopeType.SESSION)
@AutoCreate
public class CardBalanceMBean implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Vector<CardBalanceNotification> settings;
	CardBalanceNotification cardBal, selCardBal;
	private long region_id;
	
	public void update()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getSelCardBal() != null && getSelCardBal().getThresholdAlertEmail() != null && getSelCardBal().getMinbalance() != null && 
				getSelCardBal().getNew_region_id() > 0L && getSelCardBal().getId() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Updating notification settings...");
			audit.setEntity("CardBalanceNotification");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			Vector<Region> regions = new RegionDAO().getRegions();
			for(Region e : regions)
			{
				if(e.getId().longValue() == getSelCardBal().getNew_region_id())
				{
					getSelCardBal().setRegion(e);
					break;
				}
			}
			
			CardBalanceDAO cbDAO = new CardBalanceDAO();
			if(cbDAO.updateBalance(getSelCardBal()))
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.success", null), null));
				audit.setSuccess(true);
			}
			
			setSelCardBal(null);
			setSettings(null);
			
			new AuditDAO().save(audit);
		}
	}
	
	public void save()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getCardBal().getThresholdAlertEmail() != null && getCardBal().getMinbalance() != null && getRegion_id() > 0L)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Setting the notification settings...");
			audit.setEntity("CardBalanceNotification");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			Vector<Region> regions = new RegionDAO().getRegions();
			
			CardBalanceDAO cbDAO = new CardBalanceDAO();
			for(Region e : regions)
			{
				if(e.getId().longValue() == getRegion_id())
				{
					getCardBal().setRegion(e);
					break;
				}
			}
			
			if(cbDAO.setBalance(getCardBal()))
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.success", null), null));
				audit.setSuccess(true);
			}
			
			setCardBal(null);
			setSettings(null);
			
			new AuditDAO().save(audit);
		}
	}

	public Vector<CardBalanceNotification> getSettings() {
		if(settings == null || settings.size() == 0)
		{
			settings = new CardBalanceDAO().getSettings();
		}
		return settings;
	}

	public void setSettings(Vector<CardBalanceNotification> settings) {
		this.settings = settings;
	}

	public CardBalanceNotification getCardBal() {
		if(cardBal == null)
			cardBal = new CardBalanceNotification();
		return cardBal;
	}

	public void setCardBal(CardBalanceNotification cardBal) {
		this.cardBal = cardBal;
	}
	
	public CardBalanceNotification getSelCardBal() {
		return selCardBal;
	}

	public void setSelCardBal(CardBalanceNotification selCardBal) {
		this.selCardBal = selCardBal;
	}

	public long getRegion_id() {
		return region_id;
	}

	public void setRegion_id(long region_id) {
		this.region_id = region_id;
	}

	private User getActiveUser()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		return ((ReportMBean)curContext.getExternalContext().getSessionMap().get("reportMBean")).getActiveUser();
	}
}
