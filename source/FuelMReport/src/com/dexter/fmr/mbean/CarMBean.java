package com.dexter.fmr.mbean;

import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import jxl.Sheet;
import jxl.Workbook;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.dexter.fmr.dao.AuditDAO;
import com.dexter.fmr.dao.CarDAO;
import com.dexter.fmr.dao.UserDAO;
import com.dexter.fmr.model.AuditTrail;
import com.dexter.fmr.model.Car;
import com.dexter.fmr.model.User;
import com.dexter.fmr.util.Utils;
import com.sun.faces.context.FacesContextImpl;

@Name("carMBean")
@Scope(ScopeType.SESSION)
@AutoCreate
public class CarMBean implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Car car;
	private Vector<Car> cars;
	private long user_id;
	
	private UploadItem uploadItem;
	private Vector<Car> loadedCars = new Vector<Car>();
	
	/**
	 * Uploads the excel file for buck load of vehicles.
	 * */
	public void xlslisterner(UploadEvent event) throws Exception
	{
		UploadItem item = event.getUploadItem();
		
		try
		{
			loadedCars.clear();
			//ByteArrayInputStream bin = new ByteArrayInputStream();
			Workbook wbk = Workbook.getWorkbook(item.getFile());
			Sheet[] sheets = wbk.getSheets();
			for(Sheet sht : sheets)
			{
				// reading the contents 
				// column 1 - Registration number, 2 - Fuel Type: PETROL|DIESEL
				int rows = sht.getRows();
				for(int r=0; r<rows; r++)
				{
					try
					{
						String regNumber = sht.getCell(0, r).getContents();
						String fuelType = sht.getCell(1, r).getContents();
						
						Car e = new Car();
						e.setRegNumber(regNumber);
						e.setFuelType(fuelType);
						loadedCars.add(e);
					}
					catch(Exception ig){}
				}
			}
			
			setCars(null);
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Loaded: " + loadedCars.size(), null));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		//setUploadItem(item); // store the uploaded item, until SAVED
	}
	
	public void buckLoadCars()
	{
		if(loadedCars.size() > 0)
		{
			AuditTrail audit = new AuditTrail();
			
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Batch loading vehicles...");
			audit.setEntity("CAR");
			audit.setUsername(getActiveUser().getUsername());
			
			audit.setActionPerformed(audit.getActionPerformed() + " Loaded: " + loadedCars.size());
			
			int success = 0, failed = 0;
			CarDAO cDAO = new CarDAO();
			for(Car e : loadedCars)
			{
				boolean ret = cDAO.createCar(e);
				if(ret)
					success += 1;
				else
					failed += 1;
			}
			audit.setActionPerformed(audit.getActionPerformed() + " Success: " + success + ", Failed: " + failed);
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Loaded: " + loadedCars.size() + ", Success: " + success + ", Failed: " + failed, null));
			
			new AuditDAO().save(audit);
			
			loadedCars.clear();
		}
	}
	
	public void createCar()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getCar().getRegNumber() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Creating a new vehicle with registration number: " + getCar().getRegNumber());
			audit.setEntity("CAR");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			if(getUser_id() > 0)
			{
				User u = new UserDAO().getUserById(getUser_id());
				if(u != null)
				{
					getCar().setAssigned(true);
					getCar().setAssignedUser(u);
					
					audit.setActionPerformed(audit.getActionPerformed() + ". Assigned to: " + u.getFullname());
				}
			}
			
			boolean ret = new CarDAO().createCar(getCar());
			if(ret)
			{
				setCars(null);
				setCar(null);
				setUser_id(0);
				audit.setSuccess(true);
				
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.success", null), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.failed", null), null));
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void updateCar()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getCar().getId() != null)
		{
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Update vehicle with registration number: " + getCar().getRegNumber());
			audit.setEntity("CAR");
			audit.setUsername(getActiveUser().getUsername());
			
			boolean ret = new CarDAO().updateCar(getCar());
			if(ret)
			{
				setCars(null);
				setCar(null);
				setUser_id(0);
				audit.setSuccess(true);
				
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.success", null), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.failed", null), null));
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void unassignCar()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getCar().getId() != null)
		{
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Unassign vehicle with registration number: " + getCar().getRegNumber());
			audit.setEntity("CAR");
			audit.setUsername(getActiveUser().getUsername());
			
			getCar().setAssigned(false);
			getCar().setAssignedUser(null);
			
			boolean ret = new CarDAO().updateCar(getCar());
			if(ret)
			{
				setCars(null);
				setCar(null);
				setUser_id(0);
				audit.setSuccess(true);
				
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.success", null), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.failed", null), null));
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void assignCar()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getCar().getId() != null && getUser_id() > 0)
		{
			User u = new UserDAO().getUserById(getUser_id());
			if(u != null)
			{
				FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
				audit.setAuditTime(new java.util.Date());
				audit.setActionPerformed("Assign vehicle with registration number: " + getCar().getRegNumber() + " to " + u.getFullname());
				audit.setEntity("CAR");
				audit.setUsername(getActiveUser().getUsername());
				
				getCar().setAssigned(true);
				getCar().setAssignedUser(u);
				
				boolean ret = new CarDAO().updateCar(getCar());
				if(ret)
				{
					setCars(null);
					setCar(null);
					setUser_id(0);
					audit.setSuccess(true);
					
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.success", null), null));
				}
				else
				{
					audit.setSuccess(false);
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.failed", null), null));
				}
				
				new AuditDAO().save(audit);
			}
		}
	}

	public Car getCar() {
		if(car == null)
			car = new Car();
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public Vector<Car> getCars() {
		if(cars == null)
			cars = new CarDAO().getCars();
		return cars;
	}

	public void setCars(Vector<Car> cars) {
		this.cars = cars;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public UploadItem getUploadItem() {
		return uploadItem;
	}

	public void setUploadItem(UploadItem uploadItem) {
		this.uploadItem = uploadItem;
	}
	
	private User getActiveUser()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		return ((ReportMBean)curContext.getExternalContext().getSessionMap().get("reportMBean")).getActiveUser();
	}
	
}
