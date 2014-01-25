package com.dexter.fmr.mbean;

import java.util.Vector;
import java.util.logging.Logger;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.dexter.fmr.dao.CarDAO;
import com.dexter.fmr.dao.RoleDAO;
import com.dexter.fmr.dao.UserDAO;
import com.dexter.fmr.model.Car;
import com.dexter.fmr.model.Role;
import com.dexter.fmr.model.User;

@Name("dropdownMBean")
@Scope(ScopeType.SESSION)
public class DropDownMBean
{
	final Logger logger = Logger.getLogger("FuelMReport-DropDownMBean");
	
	public Vector<Role> getRoles()
	{
		return new RoleDAO().getRoles();
	}
	
	public Vector<Car> getCars()
	{
		return new CarDAO().getCarsNotAssigned();
	}
	
	public Vector<Car> getAllCars()
	{
		return new CarDAO().getCars();
	}
	
	public Vector<User> getUsers()
	{
		return new UserDAO().getUsers();
	}
	
	public Vector<User> getDrivers()
	{
		return new UserDAO().getDrivers();
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
}
