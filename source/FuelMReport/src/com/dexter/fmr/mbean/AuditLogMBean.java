package com.dexter.fmr.mbean;

import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.dexter.fmr.dao.AuditDAO;
import com.dexter.fmr.model.AuditTrail;
import com.dexter.fmr.util.Utils;
import com.sun.faces.context.FacesContextImpl;

@Name("auditMBean")
@Scope(ScopeType.SESSION)
@AutoCreate
public class AuditLogMBean implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Vector<AuditTrail> audits;
	
	private String actP;
	private String uname;
	private String succ;

	public void filterAudit()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getActP().trim().length() >= 3 || getUname().trim().length() >= 2)
		{
			if(getSucc() != null && getSucc().length() > 0)
			{
				try
				{
					setAudits(new AuditDAO().searchAudits((getActP().trim().length() >= 3) ? getActP().trim() : null, (getUname().trim().length() >= 2) ? getUname().trim() : null, Boolean.parseBoolean(getSucc())));
				}
				catch(Exception ex)
				{}
			}
			else
			{
				try
				{
					setAudits(new AuditDAO().searchAudits((getActP().trim().length() >= 3) ? getActP().trim() : null, (getUname().trim().length() >= 2) ? getUname().trim() : null, null));
				}
				catch(Exception ex)
				{}
			}
		}
		else
		{
			if(getSucc() != null && getSucc().length() > 0)
			{
				try
				{
					setAudits(new AuditDAO().searchAudits(null, null, Boolean.parseBoolean(getSucc())));
				}
				catch(Exception ex)
				{}
			}
			else
			{
				setAudits(new AuditDAO().getAudits());
			}
		}
		
		curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getAudits().size()}), null));
	}
	
	public void clearLogs()
	{
		new AuditDAO().clearAudits();
		setAudits(null);
	}
	
	public Vector<AuditTrail> getAudits() {
		if(audits == null)
			audits = new Vector<AuditTrail>();
		return audits;
	}

	public void setAudits(Vector<AuditTrail> audits) {
		this.audits = audits;
	}

	public String getActP() {
		return actP;
	}

	public void setActP(String actP) {
		this.actP = actP;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getSucc() {
		return succ;
	}

	public void setSucc(String succ) {
		this.succ = succ;
	}
	
}
