package com.dexter.fmr.mbean;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.dexter.fmr.dao.AuditDAO;
import com.dexter.fmr.dao.CardBalanceDAO;
import com.dexter.fmr.model.AuditTrail;
import com.dexter.fmr.model.CardBalanceNotification;
import com.dexter.fmr.model.User;
import com.dexter.fmr.util.Utils;
import com.sun.faces.context.FacesContextImpl;

@Name("cardBalanceMBean")
@Scope(ScopeType.SESSION)
@AutoCreate
public class CardBalanceMBean implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	CardBalanceNotification cardBal;
	
	public void save()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getCardBal().getEmail() != null && getCardBal().getMinbalance() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Setting the card balance notification email and threshold");
			audit.setEntity("CardBalanceNotification");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			CardBalanceDAO cbDAO = new CardBalanceDAO();
			if(getCardBal().getId() != null)
			{
				if(cbDAO.updateBalance(getCardBal()))
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.success", null), null));
					audit.setSuccess(true);
				}
			}
			else
			{
				if(cbDAO.setBalance(getCardBal()))
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.success", null), null));
					audit.setSuccess(true);
				}
			}
			
			new AuditDAO().save(audit);
		}
	}

	public CardBalanceNotification getCardBal() {
		if(cardBal == null)
		{
			cardBal = new CardBalanceDAO().getBalanceNotification();
			if(cardBal == null)
				cardBal = new CardBalanceNotification();
		}
		return cardBal;
	}

	public void setCardBal(CardBalanceNotification cardBal) {
		this.cardBal = cardBal;
	}
	
	private User getActiveUser()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		return ((ReportMBean)curContext.getExternalContext().getSessionMap().get("reportMBean")).getActiveUser();
	}
}
