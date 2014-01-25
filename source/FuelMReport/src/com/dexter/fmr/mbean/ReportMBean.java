package com.dexter.fmr.mbean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.dexter.fmr.dao.AuditDAO;
import com.dexter.fmr.dao.CarDAO;
import com.dexter.fmr.dao.FunctionDAO;
import com.dexter.fmr.dao.ReportDAO;
import com.dexter.fmr.dao.RoleDAO;
import com.dexter.fmr.dao.UserDAO;
import com.dexter.fmr.model.AuditTrail;
import com.dexter.fmr.model.Car;
import com.dexter.fmr.model.Function;
import com.dexter.fmr.model.Role;
import com.dexter.fmr.model.RoleFunction;
import com.dexter.fmr.model.User;
import com.dexter.fmr.util.Utils;
import com.sun.faces.context.FacesContextImpl;

@Name("reportMBean")
@Scope(ScopeType.SESSION)
@AutoCreate
public class ReportMBean implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private Date tranDate, tranDate2;
	private int tranType;
	
	private long vehicle_id;
	private int month;
	private Car vehicle;
	
	private ArrayList<String[]> records, records2, recordsHC, recordsHP, recordsLD, recordsBE, recordsEX;
	private Boolean[] recordsFields, records2Fields, recordsEXFields;
	private boolean showTracker;
	
	private boolean authenticated;
	private String username;
	private String password;
	
	private User activeUser;
	private Vector<RoleFunction> userFunctions;
	
	public String authenticate()
	{
		AuditTrail audit = new AuditTrail();
		
		audit.setActionPerformed("User: " + getUsername() + " logging in");
		audit.setAuditTime(new java.util.Date());
		audit.setEntity("User");
		audit.setUsername(getUsername());
		
		setAuthenticated(false);
		setActiveUser(null);
		
		UserDAO uDAO = new UserDAO();
		User u = uDAO.getUserByLogin(getUsername(), getPassword());
		if(u != null)
		{
			audit.setSuccess(true);
			setActiveUser(u);
			setAuthenticated(true);
			
			setUserFunctions(new RoleDAO().getRoleFunctions(u.getRole()));
		}
		else
		{
			audit.setSuccess(false);
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("org.jboss.seam.loginFailed", null), null));
		}
		
		new AuditDAO().save(audit);
		
		return "index";
	}
	
	public String logout()
	{
		AuditTrail audit = new AuditTrail();
		
		audit.setActionPerformed("logged out!");
		audit.setAuditTime(new Date());
		audit.setUsername(getActiveUser().getUsername());
		
		setAuthenticated(false);
		setActiveUser(null);
		setUserFunctions(null);
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("org.jboss.seam.logout", null), null));
		
		org.jboss.seam.web.Session.getInstance().invalidate();
		
		audit.setSuccess(true);
		
		new AuditDAO().save(audit);
		
		return "index";
	}
	
	public void searchDailyTransactions()
	{
		AuditTrail audit = new AuditTrail();
		
		setRecords(null);
		if(getVehicle_id() > 0 && getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for daily log sheet transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			Car c = new CarDAO().getCarById(getVehicle_id());
			if(c != null)
			{
				FacesContext curContext = FacesContextImpl.getCurrentInstance();
				
				setVehicle(c);
				
				setRecords(new ReportDAO().searchDailyLogSheet(getTranDate(), getTranDate2(), getVehicle()));
				if(getRecords().size() > 0)
				{
					audit.setSuccess(true);
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecords().size()}), null));
				}
				else
				{
					audit.setSuccess(false);
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
				}
				
				audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecords().size() + " record(s) found.");
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchFuelPurchaseTransactions()
	{
		AuditTrail audit = new AuditTrail();
		setRecords2(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for daily log sheet transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecords2(new ReportDAO().searchFuelPurchaseReport(getTranDate(), getTranDate2()));
			if(getRecords2().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecords2().size()}), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecords().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchExceptionTransactions()
	{
		AuditTrail audit = new AuditTrail();
		setRecordsEX(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for exception transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsEX(new ReportDAO().exceptionTransactions(getTranDate(), getTranDate2()));
			if(getRecordsEX().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecordsEX().size()}), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsEX().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchHighestFuelConsumption()
	{
		AuditTrail audit = new AuditTrail();
		setRecordsHC(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for highest fuel consuming vehicles transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsHC(new ReportDAO().highestFuelConsumption(getTranDate(), getTranDate2()));
			if(getRecordsHC().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecordsHC().size()}), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsHC().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchHighestFuelPurchase()
	{
		AuditTrail audit = new AuditTrail();
		setRecordsHP(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for highest fuel purchase vehicles transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsHP(new ReportDAO().highestFuelPurchase(getTranDate(), getTranDate2()));
			if(getRecordsHP().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecordsHP().size()}), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsHP().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchLongestDistance()
	{
		AuditTrail audit = new AuditTrail();
		setRecordsLD(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for longest distance vehicles transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsLD(new ReportDAO().longestDistance(getTranDate(), getTranDate2()));
			if(getRecordsLD().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecordsLD().size()}), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsLD().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchBestEfficiency()
	{
		AuditTrail audit = new AuditTrail();
		setRecordsBE(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for best fuel efficient vehicles transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsBE(new ReportDAO().bestEfficiency(getTranDate(), getTranDate2()));
			if(getRecordsBE().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecordsBE().size()}), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsBE().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchTransactions()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		setRecords(new ArrayList<String[]>());
		setRecords2(new ArrayList<String[]>());
		
		if(getTranType() == 1)
		{
			setRecords(new ReportDAO().searchReport(getTranDate()));
			
			if(getRecords().size() > 0)
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecords().size()}), null));
			else
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
		}
		else if(getTranType() == 2)
		{
			setRecords2(new ReportDAO().searchReport2(getTranDate()));
			
			if(getRecords2().size() > 0)
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("search.success", new Object[] {getRecords2().size()}), null));
			else
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, Utils.getBundleMessage("search.failed", null), null));
		}
	}
	
	public boolean isSetup()
	{
		boolean ret = false;
		
		UserDAO uDAO = new UserDAO();
		if(uDAO.getUsers().size() > 0)
			ret = true;
		
		return ret;
	}
	
	public void setup()
	{
		AuditTrail audit = new AuditTrail();
		
		audit.setActionPerformed("Setting up default users...");
		audit.setAuditTime(new java.util.Date());
		audit.setUsername("SYSTEM");
		
		RoleDAO rDAO = new RoleDAO();
		UserDAO uDAO = new UserDAO();
		FunctionDAO fDAO = new FunctionDAO();
		
		Vector<Function> functions = new Vector<Function>();
		
		Function f = new Function();
		f.setModule("ADMIN");
		f.setName("Manage Roles");
		f.setDescription("Create and manage roles and assign functions to roles");
		f.setViewUrl("roles.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("ADMIN");
		f.setName("Manage Users");
		f.setDescription("Create and manage users along with their roles");
		f.setViewUrl("users.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("ADMIN");
		f.setName("Batch Load Users");
		f.setDescription("Batch load users based on the recognized template");
		f.setViewUrl("users.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("SETTINGS");
		f.setName("Manage Vehicles");
		f.setDescription("Create and manage vehicles");
		f.setViewUrl("cars.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("SETTINGS");
		f.setName("Batch Load Vehicles");
		f.setDescription("Batch load vehicles based on the recognized template");
		f.setViewUrl("cars.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("ADMIN");
		f.setName("View Audit");
		f.setDescription("View all audits");
		f.setViewUrl("logs.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("ADMIN");
		f.setName("Clear Audit");
		f.setDescription("Clear all audits");
		f.setViewUrl("logs.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("SETTINGS");
		f.setName("Manage Card Threshold");
		f.setDescription("Set notification settings for card balance threshold");
		f.setViewUrl("card_balance.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("REPORT");
		f.setName("Daily Fuel Log Sheet");
		f.setDescription("View daily fuel usage for a specific vehicle for a month duration");
		f.setViewUrl("daily_r.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("REPORT");
		f.setName("Fuel Purchase Report");
		f.setDescription("View purchase report by date range");
		f.setViewUrl("fuel_purchase_r.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("REPORT");
		f.setName("Exception Report");
		f.setDescription("View exception purchases report by date range");
		f.setViewUrl("exception_purchase_r.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("REPORT");
		f.setName("Highest Fuel Consumption");
		f.setDescription("View report of highest fuel comsuming vehicles by date range");
		f.setViewUrl("highest_fuel_consumption_r.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("REPORT");
		f.setName("Highest Fuel Purchase");
		f.setDescription("View report of highest fuel purchases by date range");
		f.setViewUrl("highest_fuel_purchase_r.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("REPORT");
		f.setName("Longest Distance");
		f.setDescription("View report of longest distance convered vehicles by date range");
		f.setViewUrl("longest_distance_r.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		f = new Function();
		f.setModule("REPORT");
		f.setName("Best Efficiency");
		f.setDescription("View report of best efficient fuel vehicles by date range");
		f.setViewUrl("best_efficency_r.xhtml");
		fDAO.createFunction(f);
		functions.add(f);
		
		// create roles and sample users
		Role r = new Role();
		r.setName("ADMIN");
		r.setDescription("Role for the administrator.");
		rDAO.createRole(r);
		
		User u = new User();
		u.setActive(true);
		u.setFullname("Super Admin");
		u.setPassword("12345678");
		u.setUsername("administrator");
		u.setRole(r);
		uDAO.createUser(u);
		
		for(Function e : functions)
		{
			RoleFunction rf = new RoleFunction();
			
			rf.setRole(r);
			rf.setFunction(e);
			rf.setAssignedBy(u);
			rDAO.addFunctionToRole(rf);
		}
		
		/*r = new Role();
		r.setName("DRIVER");
		r.setDescription("Role for the drivers.");
		rDAO.createRole(r);
		
		r = new Role();
		r.setName("SUPER-USER");
		r.setDescription("Role for the super user with extra reporting capability.");
		rDAO.createRole(r);
		u = new User();
		u.setActive(true);
		u.setFullname("Super User");
		u.setPassword("12345678");
		u.setUsername("superuser");
		u.setRole(r);
		uDAO.createUser(u);
		
		r = new Role();
		r.setName("USER");
		r.setDescription("Role for the users.");
		rDAO.createRole(r);
		u = new User();
		u.setActive(true);
		u.setFullname("Regular User");
		u.setPassword("12345678");
		u.setUsername("user1");
		u.setRole(r);
		uDAO.createUser(u);*/
		
		audit.setSuccess(true);
		
		new AuditDAO().save(audit);
	}

	public Date getTranDate() {
		return tranDate;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	public int getTranType() {
		return tranType;
	}

	public void setTranType(int tranType) {
		this.tranType = tranType;
	}

	public Date getTranDate2() {
		return tranDate2;
	}

	public void setTranDate2(Date tranDate2) {
		this.tranDate2 = tranDate2;
	}

	public long getVehicle_id() {
		return vehicle_id;
	}

	public void setVehicle_id(long vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Car getVehicle() {
		return vehicle;
	}

	public void setVehicle(Car vehicle) {
		this.vehicle = vehicle;
	}

	public ArrayList<String[]> getRecords() {
		if(records == null)
			records = new ArrayList<String[]>();
		return records;
	}

	public void setRecords(ArrayList<String[]> records) {
		this.records = records;
	}

	public ArrayList<String[]> getRecords2() {
		if(records2 == null)
			records2 = new ArrayList<String[]>();
		return records2;
	}

	public void setRecords2(ArrayList<String[]> records2) {
		this.records2 = records2;
	}

	public ArrayList<String[]> getRecordsHC() {
		if(recordsHC == null)
			recordsHC = new ArrayList<String[]>();
		return recordsHC;
	}

	public void setRecordsHC(ArrayList<String[]> recordsHC) {
		this.recordsHC = recordsHC;
	}

	public ArrayList<String[]> getRecordsHP() {
		if(recordsHP == null)
			recordsHP = new ArrayList<String[]>();
		return recordsHP;
	}

	public void setRecordsHP(ArrayList<String[]> recordsHP) {
		this.recordsHP = recordsHP;
	}

	public ArrayList<String[]> getRecordsLD() {
		if(recordsLD == null)
			recordsLD = new ArrayList<String[]>();
		return recordsLD;
	}

	public void setRecordsLD(ArrayList<String[]> recordsLD) {
		this.recordsLD = recordsLD;
	}

	public ArrayList<String[]> getRecordsBE() {
		if(recordsBE == null)
			recordsBE = new ArrayList<String[]>();
		return recordsBE;
	}

	public void setRecordsBE(ArrayList<String[]> recordsBE) {
		this.recordsBE = recordsBE;
	}

	public ArrayList<String[]> getRecordsEX() {
		if(recordsEX == null)
			recordsEX = new ArrayList<String[]>();
		return recordsEX;
	}

	public void setRecordsEX(ArrayList<String[]> recordsEX) {
		this.recordsEX = recordsEX;
	}

	public Boolean[] getRecordsFields() {
		if(recordsFields == null)
		{
			recordsFields = new Boolean[11];
			for(int i=0; i<recordsFields.length; i++)
				recordsFields[i] = true;
		}
		return recordsFields;
	}

	public void setRecordsFields(Boolean[] recordsFields) {
		this.recordsFields = recordsFields;
	}

	public Boolean[] getRecords2Fields() {
		if(records2Fields == null)
		{
			records2Fields = new Boolean[14];
			for(int i=0; i<records2Fields.length; i++)
				records2Fields[i] = true;
		}
		return records2Fields;
	}

	public void setRecords2Fields(Boolean[] records2Fields) {
		this.records2Fields = records2Fields;
	}

	public Boolean[] getRecordsEXFields() {
		if(recordsEXFields == null)
		{
			recordsEXFields = new Boolean[14];
			for(int i=0; i<recordsEXFields.length; i++)
				recordsEXFields[i] = true;
		}
		return recordsEXFields;
	}

	public void setRecordsEXFields(Boolean[] recordsEXFields) {
		this.recordsEXFields = recordsEXFields;
	}

	public boolean isShowTracker() {
		return showTracker;
	}

	public void setShowTracker(boolean showTracker) {
		this.showTracker = showTracker;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public boolean getAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
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

	public User getActiveUser() {
		return activeUser;
	}

	public void setActiveUser(User activeUser) {
		this.activeUser = activeUser;
	}

	public boolean isFunctionAllowed(String function_name)
	{
		boolean ret = false;
		
		for(RoleFunction e : getUserFunctions())
		{
			if(e.getFunction().getName().equalsIgnoreCase(function_name))
			{
				ret = true;
				break;
			}
		}
		
		return ret;
	}
	
	public boolean isModuleAllowed(String module_name)
	{
		boolean ret = false;
		
		for(RoleFunction e : getUserFunctions())
		{
			if(e.getFunction().getModule().equalsIgnoreCase(module_name))
			{
				ret = true;
				break;
			}
		}
		
		return ret;
	}
	
	public Vector<RoleFunction> getUserFunctions() {
		if(userFunctions == null)
			userFunctions = new Vector<RoleFunction>();
		return userFunctions;
	}

	public void setUserFunctions(Vector<RoleFunction> userFunctions) {
		this.userFunctions = userFunctions;
	}
	
}
