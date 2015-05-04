package com.dexter.fuelcard.mbean;

import java.io.Serializable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.dexter.fuelcard.dao.CarDAO;
import com.dexter.fuelcard.dao.UserDAO;
import com.dexter.fuelcard.model.Car;
import com.dexter.fuelcard.model.User;

@ManagedBean(name = "dropdownMBean")
@SessionScoped
public class DropDownMBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	final Logger logger = Logger.getLogger("fuelcard-DropDownMBean");
	
	@ManagedProperty("#{userMBean}")
	private UserMBean userMBean;
	
	public Vector<Car> getCars()
	{
		return new CarDAO().getCarsNotAssigned(userMBean.getActiveUser().getPartner());
	}
	
	public Vector<Car> getAllCars()
	{
		return new CarDAO().getCars(userMBean.getActiveUser().getPartner());
	}
	
	public Vector<User> getUsers()
	{
		return new UserDAO().getUsers(userMBean.getActiveUser().getPartner());
	}
	
	public Vector<User> getDrivers()
	{
		return new UserDAO().getDrivers(userMBean.getActiveUser().getPartner());
	}
	
	public Vector<User> getUnassignedDrivers()
	{
		return new UserDAO().getUnAssignedDrivers(userMBean.getActiveUser().getPartner());
	}
	
	public Vector<String[]> getMonths()
	{
		Vector<String[]> list = new Vector<String[]>();
		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		for(int i=0; i<12; i++)
		{
			cal.set(java.util.Calendar.MONTH, i);
			String m = cal.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.US);
			String[] e = new String[]{""+(i+1), m};
			list.add(e);
		}
		
		return list;
	}

	public UserMBean getUserMBean() {
		return userMBean;
	}

	public void setUserMBean(UserMBean userMBean) {
		this.userMBean = userMBean;
	}
	
}
