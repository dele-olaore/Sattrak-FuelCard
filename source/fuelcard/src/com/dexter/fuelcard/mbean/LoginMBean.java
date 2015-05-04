package com.dexter.fuelcard.mbean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.dexter.fuelcard.dao.AuditDAO;
import com.dexter.fuelcard.dao.RoleDAO;
import com.dexter.fuelcard.dao.UserDAO;
import com.dexter.fuelcard.model.AuditTrail;
import com.dexter.fuelcard.model.User;
import com.dexter.fuelcard.util.Hasher;
import com.sun.faces.context.FacesContextImpl;

@ManagedBean(name = "loginBean")
@RequestScoped
public class LoginMBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	private FacesMessage msg = null;
	
	private String username;
	private String password;
	private String partnerCode;
	
	@ManagedProperty(value="#{userMBean}")
	private UserMBean userMBean;
	
	@ManagedProperty(value="#{appMBean}")
	private ApplicationMBean appMBean;
	
	public LoginMBean()
	{}
	
	public String doLogin()
	{
		String ret = "index";
		
		AuditTrail audit = new AuditTrail();
		
		audit.setActionPerformed("User: " + getUsername() + " logging in");
		audit.setAuditTime(new java.util.Date());
		audit.setEntity("User");
		audit.setUsername(getUsername());
		
		UserDAO uDAO = new UserDAO();
		User u = uDAO.getUserByLogin(getUsername(), Hasher.getHashValue(getPassword()), getPartnerCode());
		if(u != null)
		{
			audit.setSuccess(true);
			userMBean.setActiveUser(u);
			userMBean.setAuthenticated(true);
			
			userMBean.setUserFunctions(new RoleDAO().getRoleFunctions(u.getRole()));
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Welcome " + u.getFullname() + "!"));
			
			ret = "home";
		}
		else
		{
			audit.setSuccess(false);
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Authentication failed!"));
		}
		
		new AuditDAO().save(audit);
		
		return ret;
	}
	
	public String doLogout()
	{
		String ret = "signout";
		
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
		msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "Log out successful!");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		
		return ret;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public UserMBean getUserMBean() {
		return userMBean;
	}

	public void setUserMBean(UserMBean userMBean) {
		this.userMBean = userMBean;
	}

	public ApplicationMBean getAppMBean() {
		return appMBean;
	}

	public void setAppMBean(ApplicationMBean appMBean) {
		this.appMBean = appMBean;
	}
	
}
