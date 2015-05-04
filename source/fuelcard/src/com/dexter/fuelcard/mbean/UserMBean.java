package com.dexter.fuelcard.mbean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.dexter.common.util.Emailer;
import com.dexter.fuelcard.dao.CardBalanceDAO;
import com.dexter.fuelcard.model.CardBalanceNotification;
import com.dexter.fuelcard.dao.DepartmentDAO;
import com.dexter.fuelcard.dao.GeneralDAO;
import com.dexter.fuelcard.dao.AuditDAO;
import com.dexter.fuelcard.dao.CarDAO;
import com.dexter.fuelcard.dao.FunctionDAO;
import com.dexter.fuelcard.dao.PlainDAO;
import com.dexter.fuelcard.dao.RegionDAO;
import com.dexter.fuelcard.dao.ReportDAO;
import com.dexter.fuelcard.dao.RoleDAO;
import com.dexter.fuelcard.dao.UserDAO;
import com.dexter.fuelcard.model.AuditTrail;
import com.dexter.fuelcard.model.Card;
import com.dexter.fuelcard.model.CardRequest;
import com.dexter.fuelcard.model.Department;
import com.dexter.fuelcard.model.Function;
import com.dexter.fuelcard.model.Partner;
import com.dexter.fuelcard.model.Region;
import com.dexter.fuelcard.model.Role;
import com.dexter.fuelcard.model.Car;
import com.dexter.fuelcard.model.RoleFunction;
import com.dexter.fuelcard.model.User;
import com.dexter.fuelcard.model.UserReport;
import com.dexter.fuelcard.model.VehicleMake;
import com.dexter.fuelcard.model.VehicleModel;
import com.dexter.fuelcard.model.VehicleType;
import com.dexter.fuelcard.util.Hasher;
import com.sun.faces.context.FacesContextImpl;

@ManagedBean(name = "userMBean")
@SessionScoped
public class UserMBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Date tranDate, tranDate2;
	private int tranType;
	
	private long vehicle_id, user_id;
	private int month;
	private long region_id, department_id;
	private String region_nm, department_nm;
	private Car vehicle;
	
	private ArrayList<String[]> records, records2, recordsHC, recordsHP, recordsLD, recordsBE, recordsEX;
	private ArrayList<String[]> precords;
	private Boolean[] recordsFields, records2Fields, recordsEXFields;
	private boolean showTracker;
	
	private boolean authenticated;
	private String username;
	private String password;
	
	private User activeUser;
	private Vector<RoleFunction> userFunctions;
	
	// ----------------------------------- //
	private String cpassword, newpassword;
	private long role_id;
	private long car_id;
	private Vector<User> allusers;
	private User userObj;
	
	private Vector<Role> allroles;
	private Vector<Function> allFunctions, allFunctions2;
	private Vector<RoleFunction> rfunctions;
	private Role role;
	private Role selectedRole;
	
	private UploadedFile uploadItem;
	
	private Region region;
	private Department department;
	private Vector<Region> regions;
	private Vector<Department> departments;
	
	private Long vtype_id, vmake_id, vmodel_id;
	private VehicleType vtype;
	private Vector<VehicleType> vtypes;
	private VehicleMake vmake;
	private Vector<VehicleMake> vmakes;
	private VehicleModel vmodel;
	private Vector<VehicleModel> vmodels;
	
	private Vector<Car> vehicles, vehiclesList;
	
	private CardBalanceNotification cardBal, selCardBal;
	private Vector<CardBalanceNotification> cardBals;
	
	private Vector<AuditTrail> audits;
	
	private Partner partner, sattrakPartner;
	private User partnerUser;
	private Vector<Partner> partners;
	
	private UserReport userReport;
	private Vector<UserReport> userReports;
	
	private UploadedFile vehiclesFile, regionsFile, deptsFile, vmodelsFile;
	private StreamedContent usersExcelTemplate, vehiclesExcelTemplate, regionsExcelTemplate, deptsExcelTemplate, vmodelsExcelTemplate;
	private StreamedContent ordercardsExcelTemplate, cancelcardsExcelTemplate, loadcardsExcelTemplate, offloadcardsExcelTemplate;
	
	private String requestComment, responseStatus, responseComment;
	private CardRequest cardRequest;
	private Vector<CardRequest> myPendingCardOrderRequests, myPendingCardCancelRequests, myPendingCardLoadRequests, myPendingCardOffLoadRequests;
	private Vector<CardRequest> pendingCardOrderRequests, pendingCardCancelRequests, pendingCardLoadRequests, pendingCardOffLoadRequests;
	
	public UserMBean()
	{
		InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/fc_batchload_users.xls");  
		usersExcelTemplate = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "fc_batchload_users.xls");
		
		InputStream stream2 = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/fc_batchload_vehicles.xls");  
        vehiclesExcelTemplate = new DefaultStreamedContent(stream2, "application/vnd.ms-excel", "fc_batchload_vehicles.xls");
        
        InputStream stream3 = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/fc_batchload_regions.xls");  
        regionsExcelTemplate = new DefaultStreamedContent(stream3, "application/vnd.ms-excel", "fc_batchload_regions.xls");
        
        InputStream stream4 = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/fc_batchload_depts.xls");  
        deptsExcelTemplate = new DefaultStreamedContent(stream4, "application/vnd.ms-excel", "fc_batchload_depts.xls");
        
        InputStream stream5 = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/fc_batchload_models.xls");  
        vmodelsExcelTemplate = new DefaultStreamedContent(stream5, "application/vnd.ms-excel", "fc_batchload_models.xls");
        
        InputStream stream6 = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/card_order_template.xls");  
        ordercardsExcelTemplate = new DefaultStreamedContent(stream6, "application/vnd.ms-excel", "card_order_template.xls");
        
        InputStream stream7 = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/card_decommission_template.xls");  
        cancelcardsExcelTemplate = new DefaultStreamedContent(stream7, "application/vnd.ms-excel", "card_decommission_template.xls");
        
        InputStream stream8 = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/card_load_template.xls");  
        loadcardsExcelTemplate = new DefaultStreamedContent(stream8, "application/vnd.ms-excel", "card_load_template.xls");
     
        InputStream stream9 = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/templates/card_offload_template.xls");  
        offloadcardsExcelTemplate = new DefaultStreamedContent(stream9, "application/vnd.ms-excel", "card_offload_template.xls");
	}
	
	public void keepSessionAlive()
	{
		System.out.println("Keeping session alive for user: " + getActiveUser().getUsername());
	}
	
	public void downloadRequestExcel()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getCardRequest() != null)
		{
			String fileName = getCardRequest().getRequestRefNum() + ".xls";
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				bout.write(getCardRequest().getExcelFile());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			writeFileToResponse(curContext.getExternalContext(), bout, fileName, "application/vnd.ms-excel");
			
			curContext.responseComplete();
		}
		else
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No request selected!"));
	}
	
	public void attendToRequest()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getCardRequest() != null)
		{
			String body = "";
			
			getCardRequest().setStatus(getResponseStatus());
			getCardRequest().setResponse_dt(new Date());
			getCardRequest().setRespondedBy(getActiveUser());
			if(getResponseStatus() != null && getResponseStatus().equals("PROCESSING"))
			{
				body = "<html><body><p>Dear " + getCardRequest().getRequestedBy().getFullname() + ",</p><p>Your request with reference number " + getCardRequest().getRequestRefNum() + " is now been processed.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
				getCardRequest().setSattrak_response1(getResponseComment());
			}
			else if(getResponseStatus() != null && getResponseStatus().equals("DONE"))
			{
				body = "<html><body><p>Dear " + getCardRequest().getRequestedBy().getFullname() + ",</p><p>Your request with reference number " + getCardRequest().getRequestRefNum() + " is now completed.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
				getCardRequest().setSattrak_response2(getResponseComment());
			}
			else if(getResponseStatus() != null && getResponseStatus().equals("CANCELED"))
			{
				body = "<html><body><p>Dear " + getCardRequest().getRequestedBy().getFullname() + ",</p><p>Your request with reference number " + getCardRequest().getRequestRefNum() + " has been canceled.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
				getCardRequest().setSattrak_response2(getResponseComment());
			}
			
			GeneralDAO gDAO = new GeneralDAO();
			gDAO.startTransaction();
			boolean ret = gDAO.update(getCardRequest());
			if(ret)
			{
				gDAO.commit();
				setCardRequest(null);
				setPendingCardOrderRequests(null);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request attended to successfully!"));
				
				// Send email to user that submitted that their request is been treated
				Emailer.sendEmail("fuelcard@sattrakservices.com", new String[]{getCardRequest().getRequestedBy().getEmail()}, "Request Status Update - " + getCardRequest().getRequestRefNum() + " - " + getResponseStatus(), body);
				// Send email to sattrak that request is submitted
				String body2 = "<html><body><p>Hello,</p><p>Request with reference number " + getCardRequest().getRequestRefNum() + " has been updated to status - " + getResponseStatus() + ".</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
				if(getSattrakPartner() != null && getSattrakPartner().getContactEmails() != null && getSattrakPartner().getContactEmails().trim().length() > 0)
				{
					String[] to = getSattrakPartner().getContactEmails().split(",");
					Emailer.sendEmail("fuelcard@sattrakservices.com", to, "Request Status Update - " + getCardRequest().getRequestRefNum() + " - " + getResponseStatus(), body2);
				}
			}
			else
			{
				gDAO.rollback();
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
			}
			gDAO.destroy();
		}
		else
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No request selected!"));
	}
	
	public void cancelMyRequest()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getCardRequest() != null)
		{
			if(getCardRequest().getStatus().equals("PENDING"))
			{
				getCardRequest().setStatus("CANCELED");
				getCardRequest().setResponse_dt(new Date());
				getCardRequest().setRespondedBy(getActiveUser());
				
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				boolean ret = gDAO.update(getCardRequest());
				if(ret)
				{
					gDAO.commit();
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request cancelled successfully!"));
					
					// Send email to user that submitted that their request is been treated
					String body = "<html><body><p>Dear " + getActiveUser().getFullname() + ",</p><p>Your request with reference number " + getCardRequest().getRequestRefNum() + " has been canceled.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
					Emailer.sendEmail("fuelcard@sattrakservices.com", new String[]{getActiveUser().getEmail()}, "Canceled Request - " + getCardRequest().getRequestRefNum(), body);
					// Send email to sattrak that request is submitted
					String body2 = "<html><body><p>Hello,</p><p>Request with reference number " + getCardRequest().getRequestRefNum() + " has been canceled.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
					if(getSattrakPartner() != null && getSattrakPartner().getContactEmails() != null && getSattrakPartner().getContactEmails().trim().length() > 0)
					{
						String[] to = getSattrakPartner().getContactEmails().split(",");
						Emailer.sendEmail("fuelcard@sattrakservices.com", to, "Canceled Request - " + getCardRequest().getRequestRefNum(), body2);
					}
					
					setCardRequest(null);
					setMyPendingCardOrderRequests(null);
				}
				else
				{
					gDAO.rollback();
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
				}
				gDAO.destroy();
			}
			else
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Only pending requests can be cancelled!"));
		}
		else
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No request selected!"));
	}
	
	public void offloadCards()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getUploadItem() != null)
		{
			try
			{
				HSSFWorkbook workbook = new HSSFWorkbook(getUploadItem().getInputstream());
				int sheetCount = workbook.getNumberOfSheets();
				if(sheetCount >= 1)
				{
					String password = getRandomDigitPassword();
					
					HSSFSheet sheet = workbook.getSheetAt(0); // first sheet should be the main sheet
					sheet.protectSheet(password);
					
					ByteArrayOutputStream byout = new ByteArrayOutputStream();
					workbook.write(byout);
					byout.close();
					
					CardRequest cr = new CardRequest();
					cr.setRequestRefNum(getActiveUser().getPartner().getCode() + "-" + password);
					cr.setAdditionalComment(getRequestComment());
					cr.setCrt_dt(new Date());
					cr.setExcelFile(byout.toByteArray());
					cr.setPartner(getActiveUser().getPartner());
					cr.setRequest_dt(new Date());
					cr.setRequestedBy(getActiveUser());
					cr.setRequestType("OFFLOAD-CARDS");
					cr.setStatus("PENDING");
					
					GeneralDAO gDAO = new GeneralDAO();
					gDAO.startTransaction();
					boolean ret = gDAO.save(cr);
					if(ret)
					{
						gDAO.commit();
						setRequestComment(null);
						setMyPendingCardCancelRequests(null);
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request submitted successfully!"));
						
						// Send email to user that submitted that their request is been treated
						String body = "<html><body><p>Dear " + getActiveUser().getFullname() + ",</p><p>Your request to offload cards has been recieved and will be treated as soon as possible. You will be notified of every progress.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
						Emailer.sendEmail("fuelcard@sattrakservices.com", new String[]{getActiveUser().getEmail()}, "Card OffLoad Request - " + cr.getRequestRefNum() + " Received", body);
						// Send email to sattrak that request is submitted
						byte[] doc = cr.getExcelFile();
						String body2 = "<html><body><p>Hello,</p><p>A request to offload cards has been submitted. The document is attached.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
						if(getSattrakPartner() != null && getSattrakPartner().getContactEmails() != null && getSattrakPartner().getContactEmails().trim().length() > 0)
						{
							String[] to = getSattrakPartner().getContactEmails().split(",");
							Emailer.sendEmail("fuelcard@sattrakservices.com", to, "Card OffLoad Request - " + cr.getRequestRefNum() + " Submitted", body2, doc, cr.getRequestRefNum() + ".xls", "application/vnd.ms-excel");
						}
					}
					else
					{
						gDAO.rollback();
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
					}
					gDAO.destroy();
				}
				else
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Excel document is not valid!"));
				}
			}
			catch(Exception ex)
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Severe", "Please upload an excel document! Error: " + ex.getMessage()));
			}
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please upload an excel document!"));
		}
	}
	
	public void loadCards()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getUploadItem() != null)
		{
			try
			{
				HSSFWorkbook workbook = new HSSFWorkbook(getUploadItem().getInputstream());
				int sheetCount = workbook.getNumberOfSheets();
				if(sheetCount >= 1)
				{
					String password = getRandomDigitPassword();
					
					HSSFSheet sheet = workbook.getSheetAt(0); // first sheet should be the main sheet
					sheet.protectSheet(password);
					
					ByteArrayOutputStream byout = new ByteArrayOutputStream();
					workbook.write(byout);
					byout.close();
					
					CardRequest cr = new CardRequest();
					cr.setRequestRefNum(getActiveUser().getPartner().getCode() + "-" + password);
					cr.setAdditionalComment(getRequestComment());
					cr.setCrt_dt(new Date());
					cr.setExcelFile(byout.toByteArray());
					cr.setPartner(getActiveUser().getPartner());
					cr.setRequest_dt(new Date());
					cr.setRequestedBy(getActiveUser());
					cr.setRequestType("LOAD-CARDS");
					cr.setStatus("PENDING");
					
					GeneralDAO gDAO = new GeneralDAO();
					gDAO.startTransaction();
					boolean ret = gDAO.save(cr);
					if(ret)
					{
						gDAO.commit();
						setRequestComment(null);
						setMyPendingCardCancelRequests(null);
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request submitted successfully!"));
						
						// Send email to user that submitted that their request is been treated
						String body = "<html><body><p>Dear " + getActiveUser().getFullname() + ",</p><p>Your request to load cards has been recieved and will be treated as soon as possible. You will be notified of every progress.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
						Emailer.sendEmail("fuelcard@sattrakservices.com", new String[]{getActiveUser().getEmail()}, "Card Load Request - " + cr.getRequestRefNum() + " Received", body);
						// Send email to sattrak that request is submitted
						byte[] doc = cr.getExcelFile();
						String body2 = "<html><body><p>Hello,</p><p>A request to load cards has been submitted. The document is attached.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
						if(getSattrakPartner() != null && getSattrakPartner().getContactEmails() != null && getSattrakPartner().getContactEmails().trim().length() > 0)
						{
							String[] to = getSattrakPartner().getContactEmails().split(",");
							Emailer.sendEmail("fuelcard@sattrakservices.com", to, "Card Load Request - " + cr.getRequestRefNum() + " Submitted", body2, doc, cr.getRequestRefNum() + ".xls", "application/vnd.ms-excel");
						}
					}
					else
					{
						gDAO.rollback();
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
					}
					gDAO.destroy();
				}
				else
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Excel document is not valid!"));
				}
			}
			catch(Exception ex)
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Severe", "Please upload an excel document! Error: " + ex.getMessage()));
			}
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please upload an excel document!"));
		}
	}
	
	public void cancelCards()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getUploadItem() != null)
		{
			try
			{
				HSSFWorkbook workbook = new HSSFWorkbook(getUploadItem().getInputstream());
				int sheetCount = workbook.getNumberOfSheets();
				if(sheetCount >= 1)
				{
					String password = getRandomDigitPassword();
					
					HSSFSheet sheet = workbook.getSheetAt(0); // first sheet should be the main sheet
					sheet.protectSheet(password);
					
					ByteArrayOutputStream byout = new ByteArrayOutputStream();
					workbook.write(byout);
					byout.close();
					
					CardRequest cr = new CardRequest();
					cr.setRequestRefNum(getActiveUser().getPartner().getCode() + "-" + password);
					cr.setAdditionalComment(getRequestComment());
					cr.setCrt_dt(new Date());
					cr.setExcelFile(byout.toByteArray());
					cr.setPartner(getActiveUser().getPartner());
					cr.setRequest_dt(new Date());
					cr.setRequestedBy(getActiveUser());
					cr.setRequestType("CANCEL-CARDS");
					cr.setStatus("PENDING");
					
					GeneralDAO gDAO = new GeneralDAO();
					gDAO.startTransaction();
					boolean ret = gDAO.save(cr);
					if(ret)
					{
						gDAO.commit();
						setRequestComment(null);
						setMyPendingCardCancelRequests(null);
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request submitted successfully!"));
						
						// Send email to user that submitted that their request is been treated
						String body = "<html><body><p>Dear " + getActiveUser().getFullname() + ",</p><p>Your request to decommission cards has been recieved and will be treated as soon as possible. You will be notified of every progress.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
						Emailer.sendEmail("fuelcard@sattrakservices.com", new String[]{getActiveUser().getEmail()}, "Card Decommission Request - " + cr.getRequestRefNum() + " Received", body);
						// Send email to sattrak that request is submitted
						byte[] doc = cr.getExcelFile();
						String body2 = "<html><body><p>Hello,</p><p>A request to decommission cards has been submitted. The document is attached.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
						if(getSattrakPartner() != null && getSattrakPartner().getContactEmails() != null && getSattrakPartner().getContactEmails().trim().length() > 0)
						{
							String[] to = getSattrakPartner().getContactEmails().split(",");
							Emailer.sendEmail("fuelcard@sattrakservices.com", to, "Card Decommission Request - " + cr.getRequestRefNum() + " Submitted", body2, doc, cr.getRequestRefNum() + ".xls", "application/vnd.ms-excel");
						}
					}
					else
					{
						gDAO.rollback();
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
					}
					gDAO.destroy();
				}
				else
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Excel document is not valid!"));
				}
			}
			catch(Exception ex)
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Severe", "Please upload an excel document! Error: " + ex.getMessage()));
			}
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please upload an excel document!"));
		}
	}
	
	public void orderCards()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getUploadItem() != null)
		{
			try
			{
				HSSFWorkbook workbook = new HSSFWorkbook(getUploadItem().getInputstream());
				int sheetCount = workbook.getNumberOfSheets();
				if(sheetCount >= 2)
				{
					String password = getRandomDigitPassword();
					
					HSSFSheet sheet = workbook.getSheetAt(0); // first sheet should be the main sheet
					sheet.protectSheet(password);
					
					ByteArrayOutputStream byout = new ByteArrayOutputStream();
					workbook.write(byout);
					byout.close();
					
					CardRequest cr = new CardRequest();
					cr.setRequestRefNum(getActiveUser().getPartner().getCode() + "-" + password);
					cr.setAdditionalComment(getRequestComment());
					cr.setCrt_dt(new Date());
					cr.setExcelFile(byout.toByteArray());
					cr.setPartner(getActiveUser().getPartner());
					cr.setRequest_dt(new Date());
					cr.setRequestedBy(getActiveUser());
					cr.setRequestType("ORDER-CARDS");
					cr.setStatus("PENDING");
					
					GeneralDAO gDAO = new GeneralDAO();
					gDAO.startTransaction();
					boolean ret = gDAO.save(cr);
					if(ret)
					{
						gDAO.commit();
						setRequestComment(null);
						setMyPendingCardOrderRequests(null);
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request submitted successfully!"));
						
						// Send email to user that submitted that their request is been treated
						String body = "<html><body><p>Dear " + getActiveUser().getFullname() + ",</p><p>Your request to order cards has been recieved and will be treated as soon as possible. You will be notified of every progress.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
						Emailer.sendEmail("fuelcard@sattrakservices.com", new String[]{getActiveUser().getEmail()}, "Card Order Request - " + cr.getRequestRefNum() + " Received", body);
						// Send email to sattrak that request is submitted
						byte[] doc = cr.getExcelFile();
						String body2 = "<html><body><p>Hello,</p><p>A request to order cards has been submitted. The document is attached.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
						if(getSattrakPartner() != null && getSattrakPartner().getContactEmails() != null && getSattrakPartner().getContactEmails().trim().length() > 0)
						{
							String[] to = getSattrakPartner().getContactEmails().split(",");
							Emailer.sendEmail("fuelcard@sattrakservices.com", to, "Card Order Request - " + cr.getRequestRefNum() + " Submitted", body2, doc, cr.getRequestRefNum() + ".xls", "application/vnd.ms-excel");
						}
					}
					else
					{
						gDAO.rollback();
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
					}
					gDAO.destroy();
				}
				else
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Excel document is not valid!"));
				}
			}
			catch(Exception ex)
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Severe", "Please upload an excel document! Error: " + ex.getMessage()));
			}
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please upload an excel document!"));
		}
	}
	
	public void setupMyReport()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getUserReport().getReportTitle() != null && getUserReport().getDurationType() > 0)
		{
			GeneralDAO gDAO = new GeneralDAO();
			boolean error = false;
			if(getUserReport().getReportTitle().equalsIgnoreCase("Daily Fuel Log Sheet"))
			{
				if(getVehicle_id() > 0 || getVehicle_id() < 0)
				{
					Object vObj = gDAO.find(Car.class, getVehicle_id());
					if(vObj != null)
						getUserReport().setCar((Car)vObj);
					else
						error = true;
					
					String fields = null;
					try
					{
						for(int i=0; i<getRecordsFields().length; i++)
						{
							if(getRecordsFields()[i])
							{
								switch(i)
								{
								case 0:
									fields += "Transaction Date";
									break;
								case 1:
									fields += "Location";
									break;
								case 2:
									fields += "Amount on Card";
									break;
								case 3:
									fields += "Previous Km Reading";
									break;
								case 4:
									fields += "Current Km Reading";
									break;
								case 5:
									fields += "Kilometres Covered";
									break;
								case 6:
									fields += "Fuel Amount";
									break;
								case 7:
									fields += "Litres Bought";
									break;
								case 8:
									fields += "Balance on Card";
									break;
								case 9:
									fields += "Initial Fuel Level";
									break;
								case 10:
									fields += "Final Fuel Level";
									break;
								}
								if(i < (getRecordsFields().length-1))
									fields += "|";
							}
						}
					} catch(Exception ex)
					{}
					if(fields != null)
					{
						getUserReport().setFields(fields);
					}
					else
					{
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please select at least one column!"));
						error = true;
					}
				}
				else
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please select a vehicle!"));
					error = true;
				}
			}
			else if(getUserReport().getReportTitle().equalsIgnoreCase("Fuel Purchase Report"))
			{
				if(getVehicle_id() > 0 || getVehicle_id() < 0)
				{
					Object vObj = gDAO.find(Car.class, getVehicle_id());
					if(vObj != null)
						getUserReport().setCar((Car)vObj);
					else
						error = true;
					
					String fields = null;
					try
					{
						for(int i=0; i<getRecords2Fields().length; i++)
						{
							if(getRecords2Fields()[i])
							{
								switch(i)
								{
								case 0:
									fields += "Purchase Date";
									break;
								case 1:
									fields += "VIN";
									break;
								case 2:
									fields += "Created By";
									break;
								case 3:
									fields += "Driver Km1";
									break;
								case 4:
									fields += "Driver Km2";
									break;
								case 5:
									fields += "Distance";
									break;
								case 6:
									fields += "Fuel Qty";
									break;
								case 7:
									fields += "Amount";
									break;
								case 8:
									fields += "Fuel Efficiency";
									break;
								case 9:
									fields += "Fuel Brand";
									break;
								case 10:
									fields += "Fuel Dealer";
									break;
								case 11:
									fields += "Purchase by";
									break;
								case 12:
									fields += "Initial Fuel Level";
									break;
								case 13:
									fields += "Final Fuel Level";
									break;
								}
								if(i < (getRecords2Fields().length-1))
									fields += "|";
							}
						}
					} catch(Exception ex)
					{}
					if(fields != null)
					{
						getUserReport().setFields(fields);
					}
					else
					{
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please select at least one column!"));
						error = true;
					}
				}
				else
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please select a vehicle!"));
					error = true;
				}
			}
			
			if(!error)
			{
				getUserReport().setActive(true);
				getUserReport().setCrt_dt(new Date());
				getUserReport().setUser(getActiveUser());
				
				gDAO.startTransaction();
				if(gDAO.save(getUserReport()))
				{
					gDAO.commit();
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Report setup successfully!"));
					setUserReport(null);
					setUserReports(null);
					setVehicle_id(0L);
					setRecordsFields(null);
					setRecords2Fields(null);
				}
				else
				{
					gDAO.rollback();
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
				}
				gDAO.destroy();
			}
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All fields with the '*' sign are required!"));
		}
	}
	
	public void createPartner()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		if(getPartner().getName() != null && getPartner().getName().trim().length() > 0 &&
			getPartner().getCode() != null && getPartner().getCode().trim().length() > 0 &&
			getPartner().getBillingType() != null && getPartner().getBillingType().trim().length() > 0 &&
			getPartnerUser().getFullname() != null && getPartnerUser().getFullname().trim().length() > 0 &&
			getPartnerUser().getUsername() != null && getPartnerUser().getUsername().trim().length() > 0 &&
			getPartnerUser().getPassword() != null && getPartnerUser().getPassword().trim().length() > 0)
		{
			if(getPartnerUser().getPassword().equals(getCpassword()))
			{
				getPartnerUser().setPassword(Hasher.getHashValue(getPartnerUser().getPassword()));
				GeneralDAO gDAO = new GeneralDAO();
				gDAO.startTransaction();
				boolean ret = gDAO.save(getPartner());
				if(ret)
				{
					getPartnerUser().setActive(true);
					getPartnerUser().setPartner(getPartner());
					
					Role r = new Role();
					r.setName("ADMIN");
					r.setDescription("Role for " + getPartner().getName() + " administrator.");
					r.setPartner(getPartner());
					r.setDriverRole(false);
					gDAO.save(r);
					
					getPartnerUser().setRole(r);
					gDAO.save(getPartnerUser());
					
					for(Function e : getAllFunctions())
					{
						RoleFunction rf = new RoleFunction();
						
						rf.setRole(r);
						rf.setFunction(e);
						rf.setAssignedBy(getPartnerUser());
						gDAO.save(rf);
					}
					
					if(gDAO.commit())
					{
						setPartner(null);
						setPartnerUser(null);
						setPartners(null);
						
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Partner created successfully!"));
					}
					else
					{
						gDAO.rollback();
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
					}
				}
				else
				{
					gDAO.rollback();
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", gDAO.getMessage()));
				}
				gDAO.destroy();
			}
			else
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Password fields are not the same!"));
		}
		else
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All fields with the '*' sign are required!"));
	}
	
	@SuppressWarnings("unchecked")
	public void searchAudits()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		setAudits(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from AuditTrail e where e.auditTime between :date1 and :date2 and e.partner=:partner order by e.auditTime desc");
			q.setParameter("date1", getTranDate());
			q.setParameter("date2", getTranDate2());
			q.setParameter("partner", getActiveUser().getPartner());
			Object retObj = gDAO.search(q, 0);
			if(retObj != null)
			{
				setAudits((Vector<AuditTrail>)retObj);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getAudits().size() + " record(s) found!"));
			}
			else
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
			}
			gDAO.destroy();
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Please supply search dates!"));
		}
	}

	public void searchDailyTransactions()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
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
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecords().size() + " record(s) found!"));
				}
				else
				{
					audit.setSuccess(false);
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
				}
				
				audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecords().size() + " record(s) found.");
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchFuelPurchase2Transactions()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		setPrecords(null);
		if(getVehicle_id() > 0 && getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for fuel purchase transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setPrecords(new ReportDAO().searchFuelPurchaseReport2(getTranDate(), getTranDate2(), getVehicle_id()));
			if(getPrecords().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getPrecords().size() + " record(s) found!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getPrecords().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchFuelPurchaseTransactions()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		setRecords2(null);
		if(getVehicle_id() > 0 && getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for fuel purchase transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecords2(new ReportDAO().searchFuelPurchaseReport(getTranDate(), getTranDate2(), getVehicle_id()));
			if(getRecords2().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecords2().size() + " record(s) found!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecords2().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchExceptionTransactions()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		setRecordsEX(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for exception transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsEX(new ReportDAO().exceptionTransactions(getTranDate(), getTranDate2(), getActiveUser().getPartner().getId()));
			if(getRecordsEX().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecordsEX().size() + " record(s) found!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsEX().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchHighestFuelConsumption()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		setRecordsHC(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for highest fuel consuming vehicles transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsHC(new ReportDAO().highestFuelConsumption(getTranDate(), getTranDate2(), getActiveUser().getPartner().getId()));
			if(getRecordsHC().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecordsHC().size() + " record(s) found!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsHC().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchHighestFuelPurchase()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		setRecordsHP(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for highest fuel purchase vehicles transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsHP(new ReportDAO().highestFuelPurchase(getTranDate(), getTranDate2(), getActiveUser().getPartner().getId()));
			if(getRecordsHP().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecordsHP().size() + " record(s) found!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsHP().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchLongestDistance()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		setRecordsLD(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for longest distance vehicles transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsLD(new ReportDAO().longestDistance(getTranDate(), getTranDate2(), getActiveUser().getPartner().getId()));
			if(getRecordsLD().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecordsLD().size() + " record(s) found!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
			}
			
			audit.setActionPerformed(audit.getActionPerformed() + ". " + getRecordsLD().size() + " record(s) found.");
			
			new AuditDAO().save(audit);
		}
	}
	
	public void searchBestEfficiency()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		setRecordsBE(null);
		if(getTranDate() != null && getTranDate2() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Searching for best fuel efficient vehicles transactions...");
			audit.setEntity("TRANSACTIONS");
			audit.setUsername(getActiveUser().getUsername());
			
			FacesContext curContext = FacesContextImpl.getCurrentInstance();
			
			setRecordsBE(new ReportDAO().bestEfficiency(getTranDate(), getTranDate2(), getActiveUser().getPartner().getId()));
			if(getRecordsBE().size() > 0)
			{
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecordsBE().size() + " record(s) found!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
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
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecords().size() + " record(s) found!"));
			else
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
		}
		else if(getTranType() == 2)
		{
			setRecords2(new ReportDAO().searchReport2(getTranDate()));
			
			if(getRecords2().size() > 0)
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", getRecords2().size() + " record(s) found!"));
			else
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "No record found!"));
		}
	}
	
	public void onRowEdit(RowEditEvent event)
	{
		if(event.getObject() != null)
		{
			AuditTrail audit = new AuditTrail();
			audit.setPartner(getActiveUser().getPartner());
			
			audit.setActionPerformed("Update entity");
			audit.setAuditTime(new java.util.Date());
			audit.setUsername(getActiveUser().getUsername());
			audit.setEntity(event.getObject().getClass().getSimpleName());
			
			GeneralDAO gDAO = new GeneralDAO();
			boolean ret = false;
			Object eventSource = event.getObject();

			// updating a card balance notification, region might have changed
			if(eventSource instanceof CardBalanceNotification)
			{
				CardBalanceNotification cbn = (CardBalanceNotification) eventSource;
				long rg_id = cbn.getNew_region_id();
				if(rg_id > 0)
				{
					for(Region e : getRegions())
					{
						if(e.getId().longValue() == rg_id)
						{
							cbn.setRegion(e);
							break;
						}
					}
				}
				eventSource = cbn;
			}
			
			gDAO.startTransaction();
			ret = gDAO.update(eventSource);
			
			if(ret)
			{
				gDAO.commit();
				audit.setSuccess(true);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success: ", "Entity updated successfully.");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				gDAO.rollback();
				audit.setSuccess(false);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: ", "Failed to update entity. " + gDAO.getMessage());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			gDAO.destroy();
			
			new AuditDAO().save(audit);
		}
	}
	
	public void onRowCancel(RowEditEvent event)
	{
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cancelled", "Edit cancelled by user!");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public boolean isSetup()
	{
		boolean ret = false;
		
		UserDAO uDAO = new UserDAO();
		if(uDAO.getUsers().size() > 0)
			ret = true;
		
		return ret;
	}
	
	public void setupPlatform()
	{
		AuditTrail audit = new AuditTrail();
		
		audit.setActionPerformed("Setting up default users...");
		audit.setAuditTime(new java.util.Date());
		audit.setUsername("SYSTEM");
		
		RoleDAO rDAO = new RoleDAO();
		UserDAO uDAO = new UserDAO();
		FunctionDAO fDAO = new FunctionDAO();
		PlainDAO pDAO = new PlainDAO();
		
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
		
		Partner sattrak = new Partner();
		sattrak.setActive(true);
		sattrak.setCode("SATTRAK");
		sattrak.setLicenseCount(0);
		sattrak.setBillingUnitAmt(0);
		sattrak.setName("SATTRAK");
		sattrak.setSattrak(true);
		pDAO.save(sattrak);
		
		// create roles and sample users
		Role r = new Role();
		r.setName("ADMIN");
		r.setDescription("Role for the administrator.");
		r.setPartner(sattrak);
		r.setDriverRole(false);
		rDAO.createRole(r);
		
		User u = new User();
		u.setActive(true);
		u.setFullname("Super Admin");
		u.setPassword(Hasher.getHashValue("12345678"));
		u.setUsername("administrator");
		u.setEmail("dele.olaore@gmail.com");
		u.setMobileNumber("2348026966835");
		u.setPartner(sattrak);
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
		
		Partner mtn = new Partner();
		mtn.setActive(true);
		mtn.setCode("MTN");
		mtn.setBillingType("Flat-Per-License");
		mtn.setLicenseCount(100);
		mtn.setBillingUnitAmt(2500);
		mtn.setName("MTN");
		mtn.setSattrak(false);
		pDAO.save(mtn);
		
		// create roles and sample users
		Role r2 = new Role();
		r2.setName("ADMIN");
		r2.setDescription("Role for the administrator.");
		r2.setPartner(mtn);
		r2.setDriverRole(false);
		rDAO.createRole(r2);
		
		User u2 = new User();
		u2.setActive(true);
		u2.setFullname("MTN Admin");
		u2.setPassword(Hasher.getHashValue("12345678"));
		u2.setUsername("mtn");
		u2.setEmail("info@mtnonline.com");
		u2.setMobileNumber("234803");
		u2.setPartner(mtn);
		u2.setRole(r2);
		uDAO.createUser(u2);
		
		for(Function e : functions)
		{
			RoleFunction rf = new RoleFunction();
			
			rf.setRole(r2);
			rf.setFunction(e);
			rf.setAssignedBy(u2);
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
	
	public String gotoPage(String page_name)
	{
		return page_name;
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

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public long getRegion_id() {
		return region_id;
	}

	public void setRegion_id(long region_id) {
		this.region_id = region_id;
	}

	public long getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(long department_id) {
		this.department_id = department_id;
	}

	public String getRegion_nm() {
		return region_nm;
	}

	public void setRegion_nm(String region_nm) {
		this.region_nm = region_nm;
	}

	public String getDepartment_nm() {
		return department_nm;
	}

	public void setDepartment_nm(String department_nm) {
		this.department_nm = department_nm;
	}

	public Car getVehicle() {
		if(vehicle == null)
			vehicle = new Car();
		return vehicle;
	}

	public void setVehicle(Car vehicle) {
		this.vehicle = vehicle;
	}

	public ArrayList<String[]> getPrecords() {
		if(precords == null)
			precords = new ArrayList<String[]>();
		return precords;
	}

	public void setPrecords(ArrayList<String[]> precords) {
		this.precords = precords;
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

	@SuppressWarnings("unchecked")
	private Partner getSattrakPartner()
	{
		if(sattrakPartner == null)
		{
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("sattrak", true);
			GeneralDAO gDAO = new GeneralDAO();
			Object listObj = gDAO.search("Partner", params);
			if(listObj != null)
			{
				Vector<Partner> list = (Vector<Partner>)listObj;
				for(Partner p : list)
					sattrakPartner = p;
			}
		}
		return sattrakPartner;
	}
	
	public boolean isSattrak()
	{
		if(getActiveUser() != null && getActiveUser().getPartner() != null && getActiveUser().getPartner().isSattrak())
			return true;
		else
			return false;
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
	
	//-------------------------------//
	public void createRole()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getRole().getName() != null)
		{
			audit.setActionPerformed("Creating a new role with name: " + getRole().getName());
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
				boolean ret = false;
				
				getRole().setPartner(getActiveUser().getPartner());
				ret = rDAO.createRole(getRole());
				
				if(ret)
				{
					for(RoleFunction e : rFunctions)
					{
						e.setRole(getRole());
						rDAO.addFunctionToRole(e);
					}
					
					setRole(null);
					setAllFunctions(null);
					setAllroles(null);
					audit.setUsername(assignedBy.getUsername());
					audit.setSuccess(true);
					
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record created successfully!"));
				}
				else
				{
					audit.setSuccess(false);
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Record create failed!"));
				}
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Record created failed. Please select atleast one function for this role."));
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void updateRole()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getSelectedRole() != null && getSelectedRole().getId() != null && getSelectedRole().getName() != null)
		{
			audit.setActionPerformed("Updating role with id: " + getSelectedRole().getId());
			audit.setAuditTime(new java.util.Date());
			audit.setEntity("Role");
			audit.setUsername(getActiveUser().getUsername());
			
			Vector<RoleFunction> rFunctions = new Vector<RoleFunction>();
			User assignedBy = getActiveUser();
			for(Function e : getAllFunctions2())
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
						e.setRole(getSelectedRole());
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
				
				boolean ret = new RoleDAO().updateRole(getSelectedRole());
				if(ret)
				{
					setSelectedRole(null);
					setRfunctions(null);
					setAllFunctions2(null);
					setAllroles(null);
					audit.setSuccess(true);
					
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record updated successfully!"));
				}
				else
				{
					audit.setSuccess(false);
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Record update failed!"));
				}
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Record update failed. Please select atleast one function for this role."));
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			audit.setSuccess(false);
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required! Also ensure you selected an existing role!"));
		}
	}
	
	public void loadRoleFunctions()
	{
		setRfunctions(null);
		if(getSelectedRole() != null && getSelectedRole().getId() != null)
		{
			Vector<RoleFunction> rFunctions = new RoleDAO().getRoleFunctions(getSelectedRole());
			if(rFunctions.size() > 0)
			{
				setRfunctions(rFunctions);
				for(Function e : getAllFunctions2())
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
	
	public void changePassword()
	{
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getPassword() != null && getNewpassword() != null && getCpassword() != null)
		{
			AuditTrail audit = new AuditTrail();
			audit.setPartner(getActiveUser().getPartner());
			
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Password change");
			audit.setEntity("USER");
			audit.setUsername(getActiveUser().getUsername());
			
			if(getActiveUser().getPassword().equals(getPassword()))
			{
				if(getNewpassword().trim().length() > 3)
				{
					if(getNewpassword().equals(getCpassword()))
					{
						String activeUPassword = getActiveUser().getPassword();
						getActiveUser().setPassword(getNewpassword());
						
						String r = new UserDAO().updateUser(getActiveUser());
						if(r != null && r.indexOf("Success")>=0)
						{
							curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Password changed successfully!"));
							
							audit.setActionPerformed(audit.getActionPerformed() + ": " + r);
							audit.setSuccess(true);
						}
						else
						{
							curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", r));
							
							getActiveUser().setPassword(activeUPassword);
							audit.setActionPerformed(audit.getActionPerformed() + ": " + r);
							audit.setSuccess(false);
						}
					}
					else
					{
						curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "New password fields do not match!"));
						
						audit.setActionPerformed(audit.getActionPerformed() + ": New password fields do not match!");
						audit.setSuccess(false);
					}
				}
				else
				{
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Invalid lenght for new password!"));
					
					audit.setActionPerformed(audit.getActionPerformed() + ": Invalid lenght for new password!");
					audit.setSuccess(false);
				}
			}
			else
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Authentication Failed!"));
				
				audit.setActionPerformed(audit.getActionPerformed() + ": Authentication Failed");
				audit.setSuccess(false);
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required!"));
		}
	}
	
	public void saveCardBal()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getRegion_id() > 0L && ((getCardBal().getThresholdAlertEmail() != null && getCardBal().getMinbalance() != null) || (getCardBal().getExpectionAlertEmail() != null || getCardBal().getTransactionAlertEmail() != null)))
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Setting the card balance notification email and threshold");
			audit.setEntity("CardBalanceNotification");
			audit.setUsername(getActiveUser().getUsername());
			
			if(getRegion_id() > 0)
			{
				for(Region e : getRegions())
				{
					if(e.getId().longValue() == getRegion_id())
					{
						getCardBal().setRegion(e);
						break;
					}
				}
			}
			getCardBal().setPartner(getActiveUser().getPartner());
			
			CardBalanceDAO cbDAO = new CardBalanceDAO();
			if(cbDAO.setBalance(getCardBal()))
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Card balance notification setting created successfully!"));
				audit.setSuccess(true);
				
				setCardBal(null);
				setSelCardBal(null);
				setCardBals(null);
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required!"));
		}
	}
	
	public void updateCardBal()
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
			
			if(getSelCardBal().getNew_region_id() > 0)
			{
				for(Region e : getRegions())
				{
					if(e.getId().longValue() == getSelCardBal().getNew_region_id())
					{
						getSelCardBal().setRegion(e);
						break;
					}
				}
			}
			
			CardBalanceDAO cbDAO = new CardBalanceDAO();
			if(cbDAO.updateBalance(getSelCardBal()))
			{
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Card balance notification setting updated successfully!"));
				audit.setSuccess(true);
			}
			
			setSelCardBal(null);
			setCardBals(null);
			
			new AuditDAO().save(audit);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buckLoadVehicles()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		audit.setAuditTime(new java.util.Date());
		audit.setActionPerformed("Batch loading vehicles...");
		audit.setEntity("CAR");
		audit.setUsername(getActiveUser().getUsername());
		
		Vector<Car> loadedCars = new Vector<Car>();
		
		try
		{
			//Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(getVehiclesFile().getInputstream());
			for(int i=0; i<workbook.getNumberOfSheets(); i++)
			{
				//Get current sheet from the workbook
				HSSFSheet sheet = workbook.getSheetAt(i);
				//Get iterator to all the rows in current sheet starting from row 2
				Iterator<Row> rowIterator = sheet.iterator();
				int pos = 1;
				
				// reading the contents 
				// column 1 - zonControlId, 2 - regNumber, 3 - fuelType, 4 - username, 5 - cardPan, 6 - region, 7 - dept, 8 - make, 9 - type, 10 - model, 11 - year, 12 - kmpl, 13 - vehicleCapacity, 14 - calibratedCapacity
				while(rowIterator.hasNext())
				{
					Row row = rowIterator.next();
					String zonControlId = "", regNumber = "", fuelType = "", username ="";
					String cardPan = "", region = "", dept = "", make = "", type = "";
					String model = "", year = "", kmpl = "", vehicleCapacity = "", calibratedCapacity = "";
					if(pos > 1) // skip the first row, should be headers
					{
						//Get iterator to all cells of current row
						Iterator<Cell> cellIterator = row.cellIterator();
						while(cellIterator.hasNext())
						{
							Cell cell = cellIterator.next();
							String val = "";
							switch(cell.getCellType())
							{
								case Cell.CELL_TYPE_BLANK:
									val = "";
									break;
								case Cell.CELL_TYPE_BOOLEAN:
									val = ""+cell.getBooleanCellValue();
									break;
								case Cell.CELL_TYPE_ERROR:
									val = "";
									break;
								case Cell.CELL_TYPE_NUMERIC:
									val = ""+cell.getNumericCellValue();
									break;
								case Cell.CELL_TYPE_STRING:
									val = cell.getStringCellValue();
									break;
								default:
								{
									try
									{
									val = cell.getStringCellValue();
									} catch(Exception ex){}
									break;
								}
							}
							
							switch(cell.getColumnIndex())
							{
								case 0:
									zonControlId = val;
									break;
								case 1:
									regNumber = val;
									break;
								case 2:
									fuelType = val;
									break;
								case 3:
									username = val;
									break;
								case 4:
									cardPan = val;
									break;
								case 5:
									region = val;
									break;
								case 6:
									dept = val;
									break;
								case 7:
									make = val;
									break;
								case 8:
									type = val;
									break;
								case 9:
									model = val;
									break;
								case 10:
									year = val;
									break;
								case 11:
									kmpl = val;
									break;
								case 12:
									vehicleCapacity = val;
									break;
								case 13:
									calibratedCapacity = val;
									break;
							}
						}
						try
						{
							Car e = new Car();
							e.setCalibratedCapacity((calibratedCapacity!=null)?Double.parseDouble(calibratedCapacity):0);
							e.setCardPan(cardPan);
							e.setFuelType(fuelType);
							e.setPartner(getActiveUser().getPartner());
							e.setRegNumber(regNumber);
							try{
							e.setVehicleCapacity((vehicleCapacity!=null)?Double.parseDouble(vehicleCapacity):0);
							} catch(Exception ex){}
							try {
							e.setZonControlId((zonControlId != null)? Integer.parseInt(zonControlId):0);
							} catch(Exception ex){}
							
							if(username != null)
							{
								User u = new UserDAO().getUserByUsername(username, getActiveUser().getPartner().getCode());
								if(u != null)
								{
									e.setAssigned(true);
									e.setAssignedUser(u);
								}
							}
							
							if(region != null)
							{
								RegionDAO regDAO = new RegionDAO();
								Region reg = regDAO.getRegionByName(region);
								if(reg == null)
								{
									reg = new Region();
									reg.setName(region);
									reg.setPartner(getActiveUser().getPartner());
									regDAO.save(reg);
								}
								e.setRegion(reg);
							}
							
							if(dept != null)
							{
								DepartmentDAO dDAO = new DepartmentDAO();
								Department d = dDAO.getDepartmentByName(dept);
								if(d == null)
								{
									d = new Department();
									d.setName(dept);
									d.setPartner(getActiveUser().getPartner());
									dDAO.save(d);
								}
								e.setDepartment(d);
							}
							
							if(make != null && type != null && model != null)
							{
								VehicleMake m = null;
								VehicleType t = null;
								VehicleModel md = null;
								GeneralDAO gDAO = new GeneralDAO();
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("name", make);
								params.put("partner", getActiveUser().getPartner());
								Object retObj = gDAO.search("VehicleMake", params);
								if(retObj != null)
								{
									Vector<VehicleMake> retList = (Vector<VehicleMake>)retObj;
									for(VehicleMake ret : retList)
										m = ret;
								}
								if(m == null)
								{
									m = new VehicleMake();
									m.setName(make);
									m.setPartner(getActiveUser().getPartner());
									new PlainDAO().save(m);
								}
								
								params = new Hashtable<String, Object>();
								params.put("name", type);
								params.put("partner", getActiveUser().getPartner());
								retObj = gDAO.search("VehicleType", params);
								if(retObj != null)
								{
									Vector<VehicleType> retList = (Vector<VehicleType>)retObj;
									for(VehicleType ret : retList)
										t = ret;
								}
								if(t == null)
								{
									t = new VehicleType();
									t.setName(type);
									t.setPartner(getActiveUser().getPartner());
									new PlainDAO().save(t);
								}
								
								params = new Hashtable<String, Object>();
								params.put("name", model);
								params.put("make", m);
								params.put("type", t);
								params.put("year", year);
								params.put("partner", getActiveUser().getPartner());
								retObj = gDAO.search("VehicleModel", params);
								if(retObj != null)
								{
									Vector<VehicleModel> retList = (Vector<VehicleModel>)retObj;
									for(VehicleModel ret : retList)
										md = ret;
								}
								if(md == null)
								{
									md = new VehicleModel();
									md.setName(model);
									try{
									md.setKmpl((kmpl !=null && kmpl.trim().length() > 0)?Double.parseDouble(kmpl):0);
									} catch(Exception ex){}
									md.setMake(m);
									md.setType(t);
									md.setYear(year);
									md.setPartner(getActiveUser().getPartner());
									new PlainDAO().save(md);
								}
								
								e.setModel(md);
							}
							
							loadedCars.add(e);
						}
						catch(Exception ig){}
					}
					else
						pos += 1;
				}
			}
			
			setAllusers(null);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		audit.setActionPerformed(audit.getActionPerformed() + " Loaded: " + loadedCars.size());
		
		int success = 0, failed = 0;
		CarDAO cDAO = new CarDAO();
		for(Car e : loadedCars)
		{
			if(cDAO.createCar(e))
			{
				success += 1;
				if(e.getCardPan() != null)
				{
					Card card = new Card();
					card.setCardPan(getVehicle().getCardPan());
					card.setCrt_dt(new Date());
					new PlainDAO().save(card);
				}
			}
			else
				failed += 1;
		}
		audit.setActionPerformed(audit.getActionPerformed() + " Success: " + success + ", Failed: " + failed);
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Done", "Loaded: " + loadedCars.size() + ", Success: " + success + ", Failed: " + failed));
		
		new AuditDAO().save(audit);
		setVehicles(null);
	}
	
	public void createVehicle()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getVehicle().getRegNumber() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Creating a new vehicle with registration number: " + getVehicle().getRegNumber());
			audit.setEntity("CAR");
			audit.setUsername(getActiveUser().getUsername());
			
			if(getUser_id() > 0)
			{
				User u = new UserDAO().getUserById(getUser_id());
				if(u != null)
				{
					getVehicle().setAssigned(true);
					getVehicle().setAssignedUser(u);
					
					audit.setActionPerformed(audit.getActionPerformed() + ". Assigned to: " + u.getFullname());
				}
			}
			
			if(getRegion_id() > 0)
			{
				for(Region e : getRegions())
				{
					if(e.getId().longValue() == getRegion_id())
					{
						getVehicle().setRegion(e);
						break;
					}
				}
			}
			
			if(getDepartment_id() > 0)
			{
				for(Department e : getDepartments())
				{
					if(e.getId().longValue() == getDepartment_id())
					{
						getVehicle().setDepartment(e);
						break;
					}
				}
			}
			
			if(getVmodel_id() > 0)
			{
				for(VehicleModel e : getVmodels())
				{
					if(e.getId().longValue() == getVmodel_id())
					{
						getVehicle().setModel(e);
						break;
					}
				}
			}
			
			getVehicle().setPartner(getActiveUser().getPartner());
			boolean ret = new CarDAO().createCar(getVehicle());
			if(ret)
			{
				if(getVehicle().getCardPan() != null)
				{
					Card card = new Card();
					card.setCardPan(getVehicle().getCardPan());
					card.setCrt_dt(new Date());
					new PlainDAO().save(card);
				}
				
				setVehicles(null);
				setVehicle(null);
				setUser_id(0);
				audit.setSuccess(true);
				setRegion_id(0);
				setDepartment_id(0);
				
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Vehicle created successfully!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Could not create vehicle!"));
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required!"));
		}
	}
	
	public void createVType()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getVtype().getName() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Creating a new vehicle type with name: " + getVtype().getName() + ", Partner: " + getActiveUser().getPartner().getName());
			audit.setEntity("VehicleType");
			audit.setUsername(getActiveUser().getUsername());
			
			getVtype().setPartner(getActiveUser().getPartner());
			boolean ret = new PlainDAO().save(getVtype());
			if(ret)
			{
				setVtype(null);
				setVtypes(null);
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Vehicle type created successfully!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Could not create vehicle type!"));
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required!"));
		}
	}
	
	public void createVMake()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getVmake().getName() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Creating a new vehicle make with name: " + getVmake().getName() + ", Partner: " + getActiveUser().getPartner().getName());
			audit.setEntity("VehicleMake");
			audit.setUsername(getActiveUser().getUsername());
			
			getVmake().setPartner(getActiveUser().getPartner());
			boolean ret = new PlainDAO().save(getVmake());
			if(ret)
			{
				setVmake(null);
				setVmakes(null);
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Vehicle make created successfully!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Could not create vehicle make!"));
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required!"));
		}
	}
	
	public void createVModel()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getVmodel().getName() != null && getVtype_id() != null && getVmake_id() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Creating a new vehicle model with name: " + getVmodel().getName() + ", Partner: " + getActiveUser().getPartner().getName());
			audit.setEntity("VehicleModel");
			audit.setUsername(getActiveUser().getUsername());
			
			for(VehicleType vt : getVtypes())
			{
				if(vt.getId().longValue() == getVtype_id().longValue())
				{
					getVmodel().setType(vt);
					break;
				}
			}
			for(VehicleMake vm : getVmakes())
			{
				if(vm.getId().longValue() == getVmake_id().longValue())
				{
					getVmodel().setMake(vm);
					break;
				}
			}
			
			getVmodel().setPartner(getActiveUser().getPartner());
			boolean ret = new PlainDAO().save(getVmodel());
			if(ret)
			{
				setVmodel(null);
				setVmodels(null);
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Vehicle model created successfully!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Could not create vehicle model!"));
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required!"));
		}
	}
	
	public void createRegion()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getRegion().getName() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Creating a new region with name: " + getRegion().getName() + ", Partner: " + getActiveUser().getPartner().getName());
			audit.setEntity("Region");
			audit.setUsername(getActiveUser().getUsername());
			
			getRegion().setPartner(getActiveUser().getPartner());
			boolean ret = new RegionDAO().save(getRegion());
			if(ret)
			{
				setRegion(null);
				setRegions(null);
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Region created successfully!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Could not create region!"));
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required!"));
		}
	}
	
	public void createDepartment()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		
		if(getDepartment().getName() != null)
		{
			audit.setAuditTime(new java.util.Date());
			audit.setActionPerformed("Creating a new department with name: " + getDepartment().getName());
			audit.setEntity("Department");
			audit.setUsername(getActiveUser().getUsername());
			
			getDepartment().setPartner(getActiveUser().getPartner());
			boolean ret = new DepartmentDAO().save(getDepartment());
			if(ret)
			{
				setDepartment(null);
				setDepartments(null);
				audit.setSuccess(true);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department created successfully!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Could not create department!"));
			}
			
			new AuditDAO().save(audit);
		}
		else
		{
			curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "All field(s) with the '*' sign are required!"));
		}
	}
	
	public void buckLoadUsers()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
		audit.setAuditTime(new java.util.Date());
		audit.setActionPerformed("Batch loading users...");
		audit.setEntity("USER");
		audit.setUsername(getActiveUser().getUsername());
		
		Vector<User> loadedUsers = new Vector<User>();
		
		try
		{
			/*InputStream inputstream = getUploadItem().getInputstream();
			ByteArrayOutputStream fos = new ByteArrayOutputStream();      
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = inputstream.read(bytes)) != -1)
			{
				fos.write(bytes, 0, read);
			}
			fos.close();
			
			ByteArrayInputStream byteIn = new ByteArrayInputStream(fos.toByteArray());*/
			//Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(getUploadItem().getInputstream());
			RoleDAO rDAO = new RoleDAO();
			for(int i=0; i<workbook.getNumberOfSheets(); i++)
			{
				//Get current sheet from the workbook
				HSSFSheet sheet = workbook.getSheetAt(i);
				//Get iterator to all the rows in current sheet starting from row 2
				Iterator<Row> rowIterator = sheet.iterator();
				int pos = 1;
				
				// reading the contents 
				// column 1 - username, 2 - password, 3 - full name, 4 - role id, 5 - email, 6 - mobile number, 7 - Vehicle Reg Number
				while(rowIterator.hasNext())
				{
					Row row = rowIterator.next();
					String username = "", password = "", fullname = "", email ="", mobile = "", regNumber = "";
					long role_id = 0L;
					if(pos > 1) // skip the first row, should be headers
					{
						//Get iterator to all cells of current row
						Iterator<Cell> cellIterator = row.cellIterator();
						while(cellIterator.hasNext())
						{
							Cell cell = cellIterator.next();
							String val = "";
							switch(cell.getCellType())
							{
								case Cell.CELL_TYPE_BLANK:
									val = "";
									break;
								case Cell.CELL_TYPE_BOOLEAN:
									val = ""+cell.getBooleanCellValue();
									break;
								case Cell.CELL_TYPE_ERROR:
									val = "";
									break;
								case Cell.CELL_TYPE_NUMERIC:
									val = ""+cell.getNumericCellValue();
									break;
								case Cell.CELL_TYPE_STRING:
									val = cell.getStringCellValue();
									break;
								default:
								{
									try
									{
									val = cell.getStringCellValue();
									} catch(Exception ex){}
									break;
								}
							}
							
							switch(cell.getColumnIndex())
							{
								case 0:
									username = val;
									break;
								case 1:
									password = val;
									break;
								case 2:
									fullname = val;
									break;
								case 3:
									try{ role_id = Long.parseLong(val); }catch(Exception ex){ex.printStackTrace();}
									break;
								case 4:
									email = val;
									break;
								case 5:
									mobile = val;
									break;
								case 6:
									regNumber = val;
									break;
							}
						}
						try
						{
							User e = new User();
							e.setUsername(username);
							e.setPassword(password);
							e.setFullname(fullname);
							e.setEmail(email);
							e.setMobileNumber(mobile);
							e.setActive(true);
							e.setRegNumber(regNumber);
							
							Role role = rDAO.getRoleById(role_id);
							if(role != null)
							{
								e.setRole(role);
								loadedUsers.add(e);
							}
						}
						catch(Exception ig){}
					}
					else
						pos += 1;
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
			{
				success += 1;
				if(e.getRegNumber() != null)
				{
					CarDAO cDAO = new CarDAO();
					Car c = cDAO.getCarByRegNumber(e.getRegNumber());
					if(c != null)
					{
						c.setAssigned(true);
						c.setAssignedUser(e);
						
						cDAO.updateCar(c);
					}
				}
			}
			else
				failed += 1;
		}
		audit.setActionPerformed(audit.getActionPerformed() + " Success: " + success + ", Failed: " + failed);
		
		FacesContext curContext = FacesContextImpl.getCurrentInstance();
		curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Done", "Loaded: " + loadedUsers.size() + ", Success: " + success + ", Failed: " + failed));
		
		new AuditDAO().save(audit);
		setAllusers(null);
	}
	
	public void createUser()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
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
				// hash the supplied password
				getUserObj().setPassword(Hasher.getHashValue(getUserObj().getPassword()));
				
				getUserObj().setPartner(getActiveUser().getPartner());
				String ret = new UserDAO().createUser(getUserObj());
				if(ret.indexOf("Success") >= 0)
				{
					audit.setSuccess(true);
					if(getVehicle_id() > 0)
					{
						CarDAO cDAO = new CarDAO();
						Car c = cDAO.getCarById(getVehicle_id());
						if(c != null)
						{
							c.setAssigned(true);
							c.setAssignedUser(getUserObj());
							
							cDAO.updateCar(c);
							audit.setActionPerformed(audit.getActionPerformed() + ". Assigned vehicle with reg no. " + c.getRegNumber());
						}
					}
					
					setRole_id(0);
					setVehicle_id(0);
					setAllusers(null);
					setUserObj(null);
					
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record created successfully!"));
				}
				else
				{
					audit.setSuccess(false);
					curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", ret));
				}
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Record create failed: Role not found!"));
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void updateUser()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
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
				
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record updated successfully!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Record update failed!"));
			}
			
			new AuditDAO().save(audit);
		}
	}
	
	public void deletedUser()
	{
		AuditTrail audit = new AuditTrail();
		audit.setPartner(getActiveUser().getPartner());
		
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
				
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record deleted successfully!"));
			}
			else
			{
				audit.setSuccess(false);
				curContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Record delete failed!"));
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

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
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
		if(selectedRole == null)
			selectedRole = new Role();
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
			allusers = new UserDAO().getUsers(getActiveUser().getPartner());
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
			allroles = rDAO.getRoles(getActiveUser().getPartner());
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

	public UploadedFile getUploadItem() {
		return uploadItem;
	}

	public void setUploadItem(UploadedFile uploadItem) {
		this.uploadItem = uploadItem;
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
		if(getActiveUser().getPartner().isNoTrackerUsed())
		{
			Vector<Function> filteredFunctions = new Vector<Function>();
			for(Function e : allFunctions)
			{
				if(e.getName().equalsIgnoreCase("Fuel Purchase Report") || e.getName().equalsIgnoreCase("Highest Fuel Purchase"))
				{
					filteredFunctions.add(e);
				}
			}
			allFunctions.clear();
			allFunctions.addAll(filteredFunctions);
		}
		return allFunctions;
	}

	public void setAllFunctions(Vector<Function> allFunctions) {
		this.allFunctions = allFunctions;
	}
	
	public Vector<Function> getAllFunctions2() {
		if(allFunctions2 == null)
			allFunctions2 = new FunctionDAO().getAllFunctions();
		return allFunctions2;
	}

	public void setAllFunctions2(Vector<Function> allFunctions2) {
		this.allFunctions2 = allFunctions2;
	}

	public Vector<RoleFunction> getRfunctions() {
		return rfunctions;
	}

	public void setRfunctions(Vector<RoleFunction> rfunctions) {
		this.rfunctions = rfunctions;
	}

	public Region getRegion() {
		if(region == null)
			region = new Region();
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public Department getDepartment() {
		if(department == null)
			department = new Department();
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Vector<Region> getRegions() {
		if(regions == null || (regions != null && regions.size() == 0))
		{
			regions = new RegionDAO().getRegions(getActiveUser().getPartner());
		}
		return regions;
	}

	public void setRegions(Vector<Region> regions) {
		this.regions = regions;
	}

	public Vector<Department> getDepartments() {
		if(departments == null || (departments != null && departments.size() == 0))
		{
			departments = new DepartmentDAO().getDepartments(getActiveUser().getPartner());
		}
		return departments;
	}

	public void setDepartments(Vector<Department> departments) {
		this.departments = departments;
	}

	public Long getVtype_id() {
		return vtype_id;
	}

	public void setVtype_id(Long vtype_id) {
		this.vtype_id = vtype_id;
	}

	public Long getVmake_id() {
		return vmake_id;
	}

	public void setVmake_id(Long vmake_id) {
		this.vmake_id = vmake_id;
	}

	public Long getVmodel_id() {
		return vmodel_id;
	}

	public void setVmodel_id(Long vmodel_id) {
		this.vmodel_id = vmodel_id;
	}

	public VehicleType getVtype() {
		if(vtype == null)
			vtype = new VehicleType();
		return vtype;
	}

	public void setVtype(VehicleType vtype) {
		this.vtype = vtype;
	}

	@SuppressWarnings("unchecked")
	public Vector<VehicleType> getVtypes() {
		if(vtypes == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getActiveUser().getPartner());
			Object vtypesObj = gDAO.search("VehicleType", params);
			if(vtypesObj != null)
				vtypes = (Vector<VehicleType>)vtypesObj;
			
			gDAO.destroy();
		}
		return vtypes;
	}

	public void setVtypes(Vector<VehicleType> vtypes) {
		this.vtypes = vtypes;
	}

	public VehicleMake getVmake() {
		if(vmake == null)
			vmake = new VehicleMake();
		return vmake;
	}

	public void setVmake(VehicleMake vmake) {
		this.vmake = vmake;
	}

	@SuppressWarnings("unchecked")
	public Vector<VehicleMake> getVmakes() {
		if(vmakes == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getActiveUser().getPartner());
			Object vmakesObj = gDAO.search("VehicleMake", params);
			if(vmakesObj != null)
				vmakes = (Vector<VehicleMake>)vmakesObj;
			
			gDAO.destroy();
		}
		return vmakes;
	}

	public void setVmakes(Vector<VehicleMake> vmakes) {
		this.vmakes = vmakes;
	}

	public VehicleModel getVmodel() {
		if(vmodel == null)
			vmodel = new VehicleModel();
		return vmodel;
	}

	public void setVmodel(VehicleModel vmodel) {
		this.vmodel = vmodel;
	}

	@SuppressWarnings("unchecked")
	public Vector<VehicleModel> getVmodels() {
		if(vmodels == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getActiveUser().getPartner());
			Object vmodelsObj = gDAO.search("VehicleModel", params);
			if(vmodelsObj != null)
				vmodels = (Vector<VehicleModel>)vmodelsObj;
			
			gDAO.destroy();
		}
		return vmodels;
	}

	public void setVmodels(Vector<VehicleModel> vmodels) {
		this.vmodels = vmodels;
	}

	@SuppressWarnings("unchecked")
	public Vector<Car> getVehicles() {
		if(vehicles == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getActiveUser().getPartner());
			Object carsObj = gDAO.search("Car", params);
			if(carsObj != null)
				vehicles = (Vector<Car>)carsObj;
			
			gDAO.destroy();
		}
		return vehicles;
	}

	public void setVehicles(Vector<Car> vehicles) {
		this.vehicles = vehicles;
	}

	@SuppressWarnings("unchecked")
	public Vector<Car> getVehiclesList() {
		if(vehiclesList == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("partner", getActiveUser().getPartner());
			Object carsObj = gDAO.search("Car", params);
			if(carsObj != null)
				vehiclesList = (Vector<Car>)carsObj;
			
			if(vehiclesList != null && vehiclesList.size() > 0)
			{
				for(Car e : vehiclesList)
				{
					params = new Hashtable<String, Object>();
					params.put("cardPan", e.getCardPan());
					Object cardsObj = gDAO.search("Card", params);
					if(cardsObj != null)
					{
						Vector<Card> cards = (Vector<Card>)cardsObj;
						for(Card crd : cards)
							e.setCard(crd);
					}
				}
			}
			
			gDAO.destroy();
		}
		return vehiclesList;
	}

	public void setVehiclesList(Vector<Car> vehiclesList) {
		this.vehiclesList = vehiclesList;
	}

	public CardBalanceNotification getCardBal() {
		if(cardBal == null)
		{
			cardBal = new CardBalanceNotification();
		}
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

	public Vector<CardBalanceNotification> getCardBals() {
		if(cardBals == null || cardBals.size() == 0)
		{
			cardBals = new CardBalanceDAO().getSettings(getActiveUser().getPartner().getId().longValue());
		}
		return cardBals;
	}

	public void setCardBals(Vector<CardBalanceNotification> cardBals) {
		this.cardBals = cardBals;
	}

	public Vector<AuditTrail> getAudits() {
		return audits;
	}

	public void setAudits(Vector<AuditTrail> audits) {
		this.audits = audits;
	}

	public Partner getPartner() {
		if(partner == null)
			partner = new Partner();
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public User getPartnerUser() {
		if(partnerUser == null)
			partnerUser = new User();
		return partnerUser;
	}

	public void setPartnerUser(User partnerUser) {
		this.partnerUser = partnerUser;
	}

	@SuppressWarnings("unchecked")
	public Vector<Partner> getPartners() {
		if(partners == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Object partnersObj = gDAO.findAll("Partner");
			if(partnersObj != null)
				partners = (Vector<Partner>)partnersObj;
			gDAO.destroy();
		}
		return partners;
	}

	public void setPartners(Vector<Partner> partners) {
		this.partners = partners;
	}

	public UserReport getUserReport() {
		if(userReport == null)
			userReport = new UserReport();
		return userReport;
	}

	public void setUserReport(UserReport userReport) {
		this.userReport = userReport;
	}

	@SuppressWarnings("unchecked")
	public Vector<UserReport> getUserReports() {
		if(userReports == null)
		{
			GeneralDAO gDAO = new GeneralDAO();
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			params.put("active", true);
			Object retObj = gDAO.search("UserReport", params);
			if(retObj != null)
				userReports = (Vector<UserReport>)retObj;
			gDAO.destroy();
		}
		return userReports;
	}

	public void setUserReports(Vector<UserReport> userReports) {
		this.userReports = userReports;
	}

	public String getRequestComment() {
		return requestComment;
	}

	public void setRequestComment(String requestComment) {
		this.requestComment = requestComment;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseComment() {
		return responseComment;
	}

	public void setResponseComment(String responseComment) {
		this.responseComment = responseComment;
	}

	public CardRequest getCardRequest() {
		return cardRequest;
	}

	public void setCardRequest(CardRequest cardRequest) {
		this.cardRequest = cardRequest;
	}

	@SuppressWarnings("unchecked")
	public Vector<CardRequest> getMyPendingCardOrderRequests() {
		if(myPendingCardOrderRequests == null || (myPendingCardOrderRequests != null && myPendingCardOrderRequests.size() == 0))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CardRequest e where e.requestType=:requestType and e.requestedBy=:requestedBy and (e.status=:status or e.status=:status2)");
			q.setParameter("requestType", "ORDER-CARDS");
			q.setParameter("requestedBy", getActiveUser());
			q.setParameter("status", "PENDING");
			q.setParameter("status2", "PROCESSING");
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				myPendingCardOrderRequests = (Vector<CardRequest>)listObj;
			gDAO.destroy();
		}
		return myPendingCardOrderRequests;
	}

	public void setMyPendingCardOrderRequests(
			Vector<CardRequest> myPendingCardOrderRequests) {
		this.myPendingCardOrderRequests = myPendingCardOrderRequests;
	}

	@SuppressWarnings("unchecked")
	public Vector<CardRequest> getMyPendingCardCancelRequests() {
		if(myPendingCardCancelRequests == null || (myPendingCardCancelRequests != null && myPendingCardCancelRequests.size() == 0))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CardRequest e where e.requestType=:requestType and e.requestedBy=:requestedBy and (e.status=:status or e.status=:status2)");
			q.setParameter("requestType", "CANCEL-CARDS");
			q.setParameter("requestedBy", getActiveUser());
			q.setParameter("status", "PENDING");
			q.setParameter("status2", "PROCESSING");
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				myPendingCardCancelRequests = (Vector<CardRequest>)listObj;
			gDAO.destroy();
		}
		return myPendingCardCancelRequests;
	}

	public void setMyPendingCardCancelRequests(
			Vector<CardRequest> myPendingCardCancelRequests) {
		this.myPendingCardCancelRequests = myPendingCardCancelRequests;
	}

	@SuppressWarnings("unchecked")
	public Vector<CardRequest> getMyPendingCardLoadRequests() {
		if(myPendingCardLoadRequests == null || (myPendingCardLoadRequests != null && myPendingCardLoadRequests.size() == 0))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CardRequest e where e.requestType=:requestType and e.requestedBy=:requestedBy and (e.status=:status or e.status=:status2)");
			q.setParameter("requestType", "LOAD-CARDS");
			q.setParameter("requestedBy", getActiveUser());
			q.setParameter("status", "PENDING");
			q.setParameter("status2", "PROCESSING");
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				myPendingCardLoadRequests = (Vector<CardRequest>)listObj;
			gDAO.destroy();
		}
		return myPendingCardLoadRequests;
	}

	public void setMyPendingCardLoadRequests(
			Vector<CardRequest> myPendingCardLoadRequests) {
		this.myPendingCardLoadRequests = myPendingCardLoadRequests;
	}

	@SuppressWarnings("unchecked")
	public Vector<CardRequest> getMyPendingCardOffLoadRequests() {
		if(myPendingCardOffLoadRequests == null || (myPendingCardOffLoadRequests != null && myPendingCardOffLoadRequests.size() == 0))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CardRequest e where e.requestType=:requestType and e.requestedBy=:requestedBy and (e.status=:status or e.status=:status2)");
			q.setParameter("requestType", "OFFLOAD-CARDS");
			q.setParameter("requestedBy", getActiveUser());
			q.setParameter("status", "PENDING");
			q.setParameter("status2", "PROCESSING");
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				myPendingCardOffLoadRequests = (Vector<CardRequest>)listObj;
			gDAO.destroy();
		}
		return myPendingCardOffLoadRequests;
	}

	public void setMyPendingCardOffLoadRequests(
			Vector<CardRequest> myPendingCardOffLoadRequests) {
		this.myPendingCardOffLoadRequests = myPendingCardOffLoadRequests;
	}

	@SuppressWarnings("unchecked")
	public Vector<CardRequest> getPendingCardOffLoadRequests() {
		if(pendingCardOffLoadRequests == null || (pendingCardOffLoadRequests != null && pendingCardOffLoadRequests.size() == 0))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CardRequest e where e.requestType=:requestType and (e.status=:status or e.status=:status2)");
			q.setParameter("requestType", "OFFLOAD-CARDS");
			q.setParameter("status", "PENDING");
			q.setParameter("status2", "PROCESSING");
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				pendingCardOffLoadRequests = (Vector<CardRequest>)listObj;
			gDAO.destroy();
		}
		return pendingCardOffLoadRequests;
	}

	public void setPendingCardOffLoadRequests(
			Vector<CardRequest> pendingCardOffLoadRequests) {
		this.pendingCardOffLoadRequests = pendingCardOffLoadRequests;
	}

	@SuppressWarnings("unchecked")
	public Vector<CardRequest> getPendingCardLoadRequests() {
		if(pendingCardLoadRequests == null || (pendingCardLoadRequests != null && pendingCardLoadRequests.size() == 0))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CardRequest e where e.requestType=:requestType and (e.status=:status or e.status=:status2)");
			q.setParameter("requestType", "LOAD-CARDS");
			q.setParameter("status", "PENDING");
			q.setParameter("status2", "PROCESSING");
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				pendingCardLoadRequests = (Vector<CardRequest>)listObj;
			gDAO.destroy();
		}
		return pendingCardLoadRequests;
	}

	public void setPendingCardLoadRequests(
			Vector<CardRequest> pendingCardLoadRequests) {
		this.pendingCardLoadRequests = pendingCardLoadRequests;
	}

	@SuppressWarnings("unchecked")
	public Vector<CardRequest> getPendingCardOrderRequests() {
		if(pendingCardOrderRequests == null || (pendingCardOrderRequests != null && pendingCardOrderRequests.size() == 0))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CardRequest e where e.requestType=:requestType and (e.status=:status or e.status=:status2)");
			q.setParameter("requestType", "ORDER-CARDS");
			q.setParameter("status", "PENDING");
			q.setParameter("status2", "PROCESSING");
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				pendingCardOrderRequests = (Vector<CardRequest>)listObj;
			gDAO.destroy();
		}
		return pendingCardOrderRequests;
	}

	public void setPendingCardOrderRequests(
			Vector<CardRequest> pendingCardOrderRequests) {
		this.pendingCardOrderRequests = pendingCardOrderRequests;
	}

	@SuppressWarnings("unchecked")
	public Vector<CardRequest> getPendingCardCancelRequests() {
		if(pendingCardCancelRequests == null || (pendingCardCancelRequests != null && pendingCardCancelRequests.size() == 0))
		{
			GeneralDAO gDAO = new GeneralDAO();
			Query q = gDAO.createQuery("Select e from CardRequest e where e.requestType=:requestType and (e.status=:status or e.status=:status2)");
			q.setParameter("requestType", "CANCEL-CARDS");
			q.setParameter("status", "PENDING");
			q.setParameter("status2", "PROCESSING");
			Object listObj = gDAO.search(q, 0);
			if(listObj != null)
				pendingCardCancelRequests = (Vector<CardRequest>)listObj;
			gDAO.destroy();
		}
		return pendingCardCancelRequests;
	}

	public void setPendingCardCancelRequests(
			Vector<CardRequest> pendingCardCancelRequests) {
		this.pendingCardCancelRequests = pendingCardCancelRequests;
	}

	public UploadedFile getVehiclesFile() {
		return vehiclesFile;
	}

	public void setVehiclesFile(UploadedFile vehiclesFile) {
		this.vehiclesFile = vehiclesFile;
	}

	public UploadedFile getRegionsFile() {
		return regionsFile;
	}

	public void setRegionsFile(UploadedFile regionsFile) {
		this.regionsFile = regionsFile;
	}

	public UploadedFile getDeptsFile() {
		return deptsFile;
	}

	public void setDeptsFile(UploadedFile deptsFile) {
		this.deptsFile = deptsFile;
	}

	public UploadedFile getVmodelsFile() {
		return vmodelsFile;
	}

	public void setVmodelsFile(UploadedFile vmodelsFile) {
		this.vmodelsFile = vmodelsFile;
	}

	public StreamedContent getUsersExcelTemplate() {
		return usersExcelTemplate;
	}

	public StreamedContent getVehiclesExcelTemplate() {
		return vehiclesExcelTemplate;
	}

	public StreamedContent getRegionsExcelTemplate() {
		return regionsExcelTemplate;
	}

	public StreamedContent getDeptsExcelTemplate() {
		return deptsExcelTemplate;
	}

	public StreamedContent getVmodelsExcelTemplate() {
		return vmodelsExcelTemplate;
	}

	public StreamedContent getOrdercardsExcelTemplate() {
		return ordercardsExcelTemplate;
	}
	
	public StreamedContent getCancelcardsExcelTemplate() {
		return cancelcardsExcelTemplate;
	}

	public StreamedContent getLoadcardsExcelTemplate() {
		return loadcardsExcelTemplate;
	}

	public StreamedContent getOffloadcardsExcelTemplate() {
		return offloadcardsExcelTemplate;
	}

	private String getRandomDigitPassword()
	{
		StringBuilder sb = new StringBuilder();
		
		Random ran = new Random();
		for(int i=0; i<8; i++)
		{
			sb.append(ran.nextInt(10));
		}
		
		return sb.toString();
	}
	
	private void writeFileToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName, String mimeType)
	{
		try
		{
			externalContext.responseReset();
			externalContext.setResponseContentType(mimeType);
			externalContext.setResponseHeader("Expires", "0");
			externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			externalContext.setResponseHeader("Pragma", "public");
			externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName);
			externalContext.setResponseContentLength(baos.size());
			OutputStream out = externalContext.getResponseOutputStream();
			baos.writeTo(out);
			externalContext.responseFlushBuffer();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
