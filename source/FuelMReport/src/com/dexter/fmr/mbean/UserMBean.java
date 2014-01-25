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
import com.dexter.fmr.dao.FunctionDAO;
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

@Name("userMBean")
@Scope(ScopeType.SESSION)
@AutoCreate
public class UserMBean implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private String cpassword;
	private long role_id;
	private Role selectedRole;
	private long car_id;
	private Vector<User> allusers;
	private User userObj;
	
	private Vector<Role> allroles;
	private Vector<Function> allFunctions;
	private Vector<RoleFunction> rfunctions;
	private Role role;
	
	private UploadItem uploadItem;
	private Vector<User> loadedUsers = new Vector<User>();
	
	public void createRole()
	{
		AuditTrail audit = new AuditTrail();
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getRole().getName() != null)
		{
			if(getRole().getId() == null)
				audit.setActionPerformed("Creating a new role with name: " + getRole().getName());
			else
				audit.setActionPerformed("Modifying role with id: " + getRole().getId());
			audit.setAuditTime(new java.util.Date());
			audit.setEntity("Role");
			
			Vector<RoleFunction> rFunctions = new Vector<RoleFunction>();
			User assignedBy = getActiveUser();
			for(Function e : getAllFunctions())
			{
				if(e.isSelected())
				{
					RoleFunction rf = new RoleFunction();
					rf.setFunction(e);
					rf.setAssignedBy(assignedBy);
					rFunctions.add(rf);
				}
			}
			
			if(rFunctions.size() > 0)
			{
				RoleDAO rDAO = new RoleDAO();
				boolean ret = false, updating = false;
				if(getRole().getId() == null)
					ret = rDAO.createRole(getRole());
				else
				{
					ret = rDAO.updateRole(getRole());
					updating = true;
				}
				if(ret)
				{
					if(updating)
					{
						for(RoleFunction e : rFunctions)
						{
							boolean exists = false;
							for(RoleFunction rf : getRfunctions())
							{
								if(e.getFunction().getId() == rf.getFunction().getId())
								{
									exists = true;
									break;
								}
							}
							if(!exists)
							{
								e.setRole(getRole());
								rDAO.addFunctionToRole(e);
							}
						}
						
						for(RoleFunction rf : getRfunctions())
						{
							boolean exists = false;
							for(RoleFunction e : rFunctions)
							{
								if(e.getFunction().getId() == rf.getFunction().getId())
								{
									exists = true;
									break;
								}
							}
							if(!exists)
							{
								rDAO.removeFunctionFromRole(rf);
							}
						}
					}
					else
					{
						for(RoleFunction e : rFunctions)
						{
							e.setRole(getRole());
							rDAO.addFunctionToRole(e);
						}
					}
					
					setRole(null);
					setAllFunctions(null);
					setAllroles(null);
					audit.setUsername(assignedBy.getUsername());
					audit.setSuccess(true);
					
					if(!updating)
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.success", null), null));
					else
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.success", null), null));
				}
				else
				{
					audit.setSuccess(false);
					if(!updating)
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.failed", null), null));
					else
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("update.failed", null), null));
				}
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.failed", null) + ". Please select atleast one function for this role.", null));
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void updateRole()
	{
		AuditTrail audit = new AuditTrail();
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getRole().getId() != null && getRole().getName() != null)
		{
			audit.setActionPerformed("Updating role with id: " + getRole().getId());
			audit.setAuditTime(new java.util.Date());
			audit.setEntity("Role");
			audit.setUsername(getActiveUser().getUsername());
			
			boolean ret = new RoleDAO().updateRole(getRole());
			if(ret)
			{
				setRole(null);
				setRfunctions(null);
				setAllFunctions(null);
				setAllroles(null);
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
	
	public void loadRoleFunctions()
	{
		if(getRole().getId() != null)
		{
			Vector<RoleFunction> rFunctions = new RoleDAO().getRoleFunctions(getRole());
			if(rFunctions.size() > 0)
			{
				setRfunctions(rFunctions);
				for(Function e : getAllFunctions())
				{
					for(RoleFunction rf : rFunctions)
					{
						if(e.getId() == rf.getFunction().getId())
						{
							e.setSelected(true);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Uploads the excel file for buck load of vehicles.
	 * */
	public void xlslisterner(UploadEvent event) throws Exception
	{
		UploadItem item = event.getUploadItem();
		setUploadItem(item); // store the uploaded item, until SAVED
	}
	
	public void buckLoadUsers()
	{
		AuditTrail audit = new AuditTrail();
		
		audit.setAuditTime(new java.util.Date());
		audit.setActionPerformed("Batch loading users...");
		audit.setEntity("USER");
		audit.setUsername(getActiveUser().getUsername());
		
		try
		{
			loadedUsers.clear();
			Workbook wbk = Workbook.getWorkbook(getUploadItem().getFile());
			Sheet[] sheets = wbk.getSheets();
			RoleDAO rDAO = new RoleDAO();
			for(Sheet sht : sheets)
			{
				// reading the contents 
				// column 1 - username, 2 - password, 3 - full name, 4 - role id
				int rows = sht.getRows();
				for(int r=0; r<rows; r++)
				{
					try
					{
						String username = sht.getCell(0, r).getContents();
						String password = sht.getCell(1, r).getContents();
						String fullname = sht.getCell(2, r).getContents();
						Long role_id = Long.parseLong(sht.getCell(3, r).getContents());
						
						User e = new User();
						e.setUsername(username);
						e.setPassword(password);
						e.setFullname(fullname);
						
						Role role = rDAO.getRoleById(role_id);
						if(role != null)
						{
							e.setRole(role);
							loadedUsers.add(e);
						}
					}
					catch(Exception ig){}
				}
			}
			
			setAllusers(null);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		audit.setActionPerformed(audit.getActionPerformed() + " Loaded: " + loadedUsers.size());
		
		int success = 0, failed = 0;
		UserDAO uDAO = new UserDAO();
		for(User e : loadedUsers)
		{
			String ret = uDAO.createUser(e);
			if(ret.indexOf("Success") >= 0)
				success += 1;
			else
				failed += 1;
		}
		audit.setActionPerformed(audit.getActionPerformed() + " Success: " + success + ", Failed: " + failed);
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Loaded: " + loadedUsers.size() + ", Success: " + success + ", Failed: " + failed, null));
		
		new AuditDAO().save(audit);
		
		loadedUsers.clear();
	}
	
	public void createUser()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getUserObj().getUsername() != null)
		{
			audit.setActionPerformed("Creating a new user with username: " + getUserObj().getUsername());
			audit.setAuditTime(new java.util.Date());
			audit.setEntity("User");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			if(getRole_id() > 0)
			{
				getUserObj().setRole(new RoleDAO().getRoleById(getRole_id()));
				try
				{
					audit.setActionPerformed(audit.getActionPerformed() + ". Role: " + getUserObj().getRole().getName());
				}
				catch(Exception ex)
				{}
			}
			
			if(getUserObj().getRole() != null)
			{
				String ret = new UserDAO().createUser(getUserObj());
				if(ret.indexOf("Success") >= 0)
				{
					audit.setSuccess(true);
					if(getCar_id() > 0)
					{
						CarDAO cDAO = new CarDAO();
						Car c = cDAO.getCarById(getCar_id());
						if(c != null)
						{
							c.setAssigned(true);
							c.setAssignedUser(getUserObj());
							
							cDAO.updateCar(c);
							audit.setActionPerformed(audit.getActionPerformed() + ". Assigned vehicle with reg no. " + c.getRegNumber());
						}
					}
					
					setRole_id(0);
					setCar_id(0);
					setAllusers(null);
					setUserObj(null);
					
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.success", null), null));
				}
				else
				{
					audit.setSuccess(false);
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.failed", null), null));
				}
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("create.failed", null), null));
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void updateUser()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getUserObj().getUsername() != null && getUserObj().getId() != null)
		{
			audit.setActionPerformed("Updating user: " + getUserObj().getUsername());
			audit.setAuditTime(new java.util.Date());
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			if(getRole_id() > 0)
			{
				getUserObj().setRole(new RoleDAO().getRoleById(getRole_id()));
				try
				{
					audit.setActionPerformed(audit.getActionPerformed() + ". Role: " + getUserObj().getRole().getName());
				}
				catch(Exception ex)
				{}
			}
			
			String ret = new UserDAO().updateUser(getUserObj());
			if(ret.indexOf("Success") >= 0)
			{
				audit.setSuccess(true);
				setAllusers(null);
				setUserObj(null);
				
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
	
	public void deletedUser()
	{
		AuditTrail audit = new AuditTrail();
		
		if(getUserObj().getUsername() != null && getUserObj().getId() != null)
		{
			audit.setActionPerformed("Deleting user: " + getUserObj().getUsername());
			audit.setAuditTime(new java.util.Date());
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			boolean ret = new UserDAO().deleteUser(getUserObj());
			if(ret)
			{
				audit.setSuccess(true);
				setAllusers(null);
				setUserObj(null);
				
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("remove.success", null), null));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, Utils.getBundleMessage("remove.failed", null), null));
			}
			
			new AuditDAO().save(audit);
		}
	}

	public String getCpassword() {
		return cpassword;
	}

	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}

	public long getRole_id() {
		return role_id;
	}

	public void setRole_id(long role_id) {
		this.role_id = role_id;
		if(role_id > 0)
			setSelectedRole(new RoleDAO().getRoleById(role_id));
	}

	public Role getSelectedRole() {
		return selectedRole;
	}

	public void setSelectedRole(Role selectedRole) {
		this.selectedRole = selectedRole;
	}

	public long getCar_id() {
		return car_id;
	}

	public void setCar_id(long car_id) {
		this.car_id = car_id;
	}

	public Vector<User> getAllusers()
	{
		if(allusers == null)
		{
			allusers = new UserDAO().getUsers();
			CarDAO cDAO = new CarDAO();
			for(User u : allusers)
			{
				Car c = cDAO.getCarByDriver(u);
				if(c != null)
					u.setRegNumber(c.getRegNumber());
				else
					u.setRegNumber("N/A");
			}
		}
		return allusers;
	}

	public void setAllusers(Vector<User> allusers) {
		this.allusers = allusers;
	}

	public User getUserObj() {
		if(userObj == null)
			userObj = new User();
		return userObj;
	}

	public void setUserObj(User userObj) {
		this.userObj = userObj;
	}

	public Vector<Role> getAllroles() {
		if(allroles == null)
		{
			RoleDAO rDAO = new RoleDAO();
			allroles = rDAO.getRoles();
			for(Role e : allroles)
			{
				e.setFunctions(rDAO.getRoleFunctions(e));
			}
		}
		return allroles;
	}

	public void setAllroles(Vector<Role> allroles) {
		this.allroles = allroles;
	}

	public Role getRole() {
		if(role == null)
			role = new Role();
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Vector<Function> getAllFunctions() {
		if(allFunctions == null)
			allFunctions = new FunctionDAO().getAllFunctions();
		return allFunctions;
	}

	public void setAllFunctions(Vector<Function> allFunctions) {
		this.allFunctions = allFunctions;
	}
	
	public Vector<RoleFunction> getRfunctions() {
		return rfunctions;
	}

	public void setRfunctions(Vector<RoleFunction> rfunctions) {
		this.rfunctions = rfunctions;
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
