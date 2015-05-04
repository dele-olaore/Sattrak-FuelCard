package com.dexter.fuelcard.mbean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.dexter.common.util.Emailer;
import com.dexter.fuelcard.dao.GeneralDAO;
import com.dexter.fuelcard.dao.ReportDAO;
import com.dexter.fuelcard.model.UserReport;

@ManagedBean(name = "appMBean")
@ApplicationScoped
public class ApplicationMBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	final Logger logger = Logger.getLogger("fuelcard-ApplicationMBean");
	
	public ApplicationMBean()
	{
		logger.info("Initializing appMBean...");
		startTimer();
	}
	
	public void printOut()
	{
		logger.info("Im here");
	}
	
	private void startTimer()
	{
		// TODO: I need to setup a timer here that will execute a certain method at 7:00 AM everyday
		// This method will check the user report table to determine what report to generate and send
		// For daily reports, this will send the previous day's report of the selected report type
		// For weekly reports, this will only generate and send the report if the current day is a Monday. And it will generate the report from the previous Monday to the yesterdays Sunday
		// For monthly reports, this will only generate and send the report if the current day is the first day of a new month. It will generate the report for the previous month.
		
		Calendar can = Calendar.getInstance();
		can.set(Calendar.MINUTE, 0);
		can.set(Calendar.SECOND, 0);
		
		if(can.get(Calendar.HOUR_OF_DAY) > 7)
		{
			can.set(Calendar.DATE, can.get(Calendar.DATE)+1);
		}
		can.set(Calendar.HOUR_OF_DAY, 7);
		
		long period = 1000*60*60*24;
		
		TimerTask task = new TimerTask()
		{
			public void run()
			{
				// TODO: execute the method call here
				executeSchduleReports();
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, can.getTime(), period);
		logger.info("Report Timer Started! First run on: " + can.getTime());
	}
	
	@SuppressWarnings("unchecked")
	private void executeSchduleReports()
	{
		Vector<UserReport> userReports = null;
		
		GeneralDAO gDAO = new GeneralDAO();
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("active", true);
		Object listObj = gDAO.search("UserReport", params);
		gDAO.destroy();
		if(listObj != null)
		{
			userReports = (Vector<UserReport>)listObj;
		}
		
		if(userReports != null)
		{
			Calendar can = Calendar.getInstance();
			String day = can.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
			int date = can.get(Calendar.DATE);
			
			for(UserReport e : userReports)
			{
				Date start_dt = null, end_dt = null;
				switch(e.getDurationType())
				{
				case 1: // daily
				{
					can.set(Calendar.DATE, date-1);
					can.set(Calendar.HOUR_OF_DAY, 0);
					can.set(Calendar.MINUTE, 0);
					can.set(Calendar.SECOND, 0);
					
					Calendar can2 = Calendar.getInstance();
					can2.set(Calendar.DATE, can2.get(Calendar.DATE)-1);
					can2.set(Calendar.HOUR_OF_DAY, 23);
					can2.set(Calendar.MINUTE, 59);
					can2.set(Calendar.SECOND, 59);
					
					start_dt = can.getTime();
					end_dt = can2.getTime();
					break;
				}
				case 2: // weekly
				{
					// check if today is a monday
					if(day != null && day.equalsIgnoreCase("MONDAY"))
					{
						can.set(Calendar.DATE, date-7);
						can.set(Calendar.HOUR_OF_DAY, 0);
						can.set(Calendar.MINUTE, 0);
						can.set(Calendar.SECOND, 0);
						
						Calendar can2 = Calendar.getInstance();
						can2.set(Calendar.DATE, can2.get(Calendar.DATE)-1);
						can2.set(Calendar.HOUR_OF_DAY, 23);
						can2.set(Calendar.MINUTE, 59);
						can2.set(Calendar.SECOND, 59);
						
						start_dt = can.getTime();
						end_dt = can2.getTime();
					}
					break;
				}
				case 3: // monthly
				{
					// check if today is the first of a new month
					if(date == 1)
					{
						can.set(Calendar.MONTH, can.get(Calendar.MONTH)-1);
						can.set(Calendar.DATE, 1);
						can.set(Calendar.HOUR_OF_DAY, 0);
						can.set(Calendar.MINUTE, 0);
						can.set(Calendar.SECOND, 0);
						
						Calendar can2 = Calendar.getInstance();
						can2.set(Calendar.DATE, can2.get(Calendar.DATE)-1);
						can2.set(Calendar.HOUR_OF_DAY, 23);
						can2.set(Calendar.MINUTE, 59);
						can2.set(Calendar.SECOND, 59);
						
						start_dt = can.getTime();
						end_dt = can2.getTime();
					}
					break;
				}
				}
				if(start_dt != null && end_dt != null)
				{
					generateAndSendReport(e, start_dt, end_dt);
				}
			}
		}
	}
	
	private void generateAndSendReport(UserReport userReport, Date start_dt, Date end_dt)
	{
		if(userReport.getUser().getEmail() != null)
		{
			ArrayList<String[]> list = null;
			if(userReport.getReportTitle().equalsIgnoreCase("Daily Fuel Log Sheet"))
			{
				list = new ReportDAO().searchDailyLogSheet(start_dt, end_dt, userReport.getCar());
			}
			else if(userReport.getReportTitle().equalsIgnoreCase("Fuel Purchase Report"))
			{
				list = new ReportDAO().searchFuelPurchaseReport(start_dt, end_dt, userReport.getCar().getId());
			}
			else if(userReport.getReportTitle().equalsIgnoreCase("Exception Report"))
			{
				list = new ReportDAO().exceptionTransactions(start_dt, end_dt, userReport.getUser().getPartner().getId());
			}
			else if(userReport.getReportTitle().equalsIgnoreCase("Highest Fuel Consumption Report"))
			{
				list = new ReportDAO().highestFuelConsumption(start_dt, end_dt, userReport.getUser().getPartner().getId());
			}
			else if(userReport.getReportTitle().equalsIgnoreCase("Highest Fuel Purchase Report"))
			{
				list = new ReportDAO().highestFuelPurchase(start_dt, end_dt, userReport.getUser().getPartner().getId());
			}
			else if(userReport.getReportTitle().equalsIgnoreCase("Longest Distance Report"))
			{
				list = new ReportDAO().longestDistance(start_dt, end_dt, userReport.getUser().getPartner().getId());
			}
			else if(userReport.getReportTitle().equalsIgnoreCase("Best Efficiency Report"))
			{
				list = new ReportDAO().bestEfficiency(start_dt, end_dt, userReport.getUser().getPartner().getId());
			}
			
			if(list != null)
			{
				byte[] doc = generateExcelDocument(userReport.getReportTitle(), list, userReport.getFields());
				String body = "<html><body><p>Dear " + userReport.getUser().getFullname() + ",</p><p>Your report is attached.</p><p>Regards<br/>Fuel Card Platform</p></body></html>";
				Emailer.sendEmail("fuelcard@sattrakservices.com", new String[]{userReport.getUser().getEmail()}, userReport.getReportTitle() + " - " + start_dt + " to " + end_dt, body, doc, "report.xls", "application/vnd.ms-excel");
			}
		}
	}
	
	private byte[] generateExcelDocument(String reportType, ArrayList<String[]> list, String fields)
	{
		byte[] doc = null;
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try
		{
			WritableWorkbook workbook = Workbook.createWorkbook(bout);
			
			if(reportType != null && reportType.equalsIgnoreCase("Daily Fuel Log Sheet"))
			{
				String[] columns = fields.split("|");
				WritableSheet sheet = workbook.createSheet("Daily Fuel Log Sheet", 0);
				for(int i=0; i<columns.length; i++)
				{
					Label cl = new Label(i, 0, columns[i]);
					sheet.addCell(cl);
				}
				for(int row=0; row<list.size(); row++)
				{
					String[] e = list.get(row);
					for(int i=0; i<e.length; i++)
					{
						switch(i)
						{
						case 0:
						{
							checkAndAddToSheet(sheet, row+1, "Transaction Date", columns, e[i]);
							break;
						}
						case 1:
						{
							checkAndAddToSheet(sheet, row+1, "Location", columns, e[i]);
							break;
						}
						case 2:
						{
							checkAndAddToSheet(sheet, row+1, "Amount on Card", columns, e[i]);
							break;
						}
						case 3:
						{
							checkAndAddToSheet(sheet, row+1, "Previous Km Reading", columns, e[i]);
							break;
						}
						case 4:
						{
							checkAndAddToSheet(sheet, row+1, "Current Km Reading", columns, e[i]);
							break;
						}
						case 5:
						{
							checkAndAddToSheet(sheet, row+1, "Kilometres Covered", columns, e[i]);
							break;
						}
						case 6:
						{
							checkAndAddToSheet(sheet, row+1, "Fuel Amount", columns, e[i]);
							break;
						}
						case 7:
						{
							checkAndAddToSheet(sheet, row+1, "Litres Bought", columns, e[i]);
							break;
						}
						case 8:
						{
							checkAndAddToSheet(sheet, row+1, "Balance on Card", columns, e[i]);
							break;
						}
						case 9:
						{
							checkAndAddToSheet(sheet, row+1, "Initial Fuel Level", columns, e[i]);
							break;
						}
						case 10:
						{
							checkAndAddToSheet(sheet, row+1, "Final Fuel Level", columns, e[i]);
							break;
						}
						}
					}
				}
			}
			else if(reportType != null && reportType.equalsIgnoreCase("Fuel Purchase Report"))
			{
				String[] columns = fields.split("|");
				WritableSheet sheet = workbook.createSheet("Fuel Purchase Report", 0);
				for(int i=0; i<columns.length; i++)
				{
					Label cl = new Label(i, 0, columns[i]);
					sheet.addCell(cl);
				}
				for(int row=0; row<list.size(); row++)
				{
					String[] e = list.get(row);
					for(int i=0; i<e.length; i++)
					{
						switch(i)
						{
						case 0:
						{
							checkAndAddToSheet(sheet, row+1, "Purchase Date", columns, e[i]);
							break;
						}
						case 1:
						{
							checkAndAddToSheet(sheet, row+1, "VIN", columns, e[i]);
							break;
						}
						case 2:
						{
							checkAndAddToSheet(sheet, row+1, "Created By", columns, e[i]);
							break;
						}
						case 3:
						{
							checkAndAddToSheet(sheet, row+1, "Driver Km1", columns, e[i]);
							break;
						}
						case 4:
						{
							checkAndAddToSheet(sheet, row+1, "Driver Km2", columns, e[i]);
							break;
						}
						case 5:
						{
							checkAndAddToSheet(sheet, row+1, "Distance", columns, e[i]);
							break;
						}
						case 6:
						{
							checkAndAddToSheet(sheet, row+1, "Fuel Qty", columns, e[i]);
							break;
						}
						case 7:
						{
							checkAndAddToSheet(sheet, row+1, "Amount", columns, e[i]);
							break;
						}
						case 8:
						{
							checkAndAddToSheet(sheet, row+1, "Fuel Efficiency", columns, e[i]);
							break;
						}
						case 9:
						{
							checkAndAddToSheet(sheet, row+1, "Fuel Brand", columns, e[i]);
							break;
						}
						case 10:
						{
							checkAndAddToSheet(sheet, row+1, "Fuel Dealer", columns, e[i]);
							break;
						}
						case 11:
						{
							checkAndAddToSheet(sheet, row+1, "Purchase by", columns, e[i]);
							break;
						}
						case 12:
						{
							checkAndAddToSheet(sheet, row+1, "Initial Fuel Level", columns, e[i]);
							break;
						}
						case 13:
						{
							checkAndAddToSheet(sheet, row+1, "Final Fuel Level", columns, e[i]);
							break;
						}
						}
					}
				}
			}
			else if(reportType != null && reportType.equalsIgnoreCase("Exception Report"))
			{
				String[] columns = new String[]{"Exception Type", "VIN", "Tran Time", "Details"};
				WritableSheet sheet = workbook.createSheet("Exception Report", 0);
				for(int i=0; i<columns.length; i++)
				{
					Label cl = new Label(i, 0, columns[i]);
					sheet.addCell(cl);
				}
				for(int row=0; row<list.size(); row++)
				{
					String[] e = list.get(row);
					for(int i=0; i<e.length; i++)
					{
						Label cl = new Label(i, row+1, e[i]);
						sheet.addCell(cl);
					}
				}
			}
			else if(reportType != null && reportType.equalsIgnoreCase("Highest Fuel Consumption Report"))
			{
				String[] columns = new String[]{"Reg Number", "Quantity"};
				WritableSheet sheet = workbook.createSheet("Highest Fuel Consumption Report", 0);
				for(int i=0; i<columns.length; i++)
				{
					Label cl = new Label(i, 0, columns[i]);
					sheet.addCell(cl);
				}
				for(int row=0; row<list.size(); row++)
				{
					String[] e = list.get(row);
					for(int i=0; i<e.length; i++)
					{
						if(i == 1)
						{
							Label cl = new Label(0, row+1, e[i]);
							sheet.addCell(cl);
						}
						else if(i == 2)
						{
							Label cl = new Label(1, row+1, e[i]);
							sheet.addCell(cl);
						}
					}
				}
			}
			else if(reportType != null && reportType.equalsIgnoreCase("Highest Fuel Purchase Report"))
			{
				String[] columns = new String[]{"Reg Number", "Cost"};
				WritableSheet sheet = workbook.createSheet("Highest Fuel Purchase Report", 0);
				for(int i=0; i<columns.length; i++)
				{
					Label cl = new Label(i, 0, columns[i]);
					sheet.addCell(cl);
				}
				for(int row=0; row<list.size(); row++)
				{
					String[] e = list.get(row);
					for(int i=0; i<e.length; i++)
					{
						if(i == 1)
						{
							Label cl = new Label(0, row+1, e[i]);
							sheet.addCell(cl);
						}
						else if(i == 2)
						{
							Label cl = new Label(1, row+1, e[i]);
							sheet.addCell(cl);
						}
					}
				}
			}
			else if(reportType != null && reportType.equalsIgnoreCase("Longest Distance Report"))
			{
				String[] columns = new String[]{"Reg Number", "Distance"};
				WritableSheet sheet = workbook.createSheet("Longest Distance Report", 0);
				for(int i=0; i<columns.length; i++)
				{
					Label cl = new Label(i, 0, columns[i]);
					sheet.addCell(cl);
				}
				for(int row=0; row<list.size(); row++)
				{
					String[] e = list.get(row);
					for(int i=0; i<e.length; i++)
					{
						if(i == 1)
						{
							Label cl = new Label(0, row+1, e[i]);
							sheet.addCell(cl);
						}
						else if(i == 2)
						{
							Label cl = new Label(1, row+1, e[i]);
							sheet.addCell(cl);
						}
					}
				}
			}
			else if(reportType != null && reportType.equalsIgnoreCase("Best Efficiency Report"))
			{
				String[] columns = new String[]{"Reg Number", "Efficiency"};
				WritableSheet sheet = workbook.createSheet("Best Efficiency Report", 0);
				for(int i=0; i<columns.length; i++)
				{
					Label cl = new Label(i, 0, columns[i]);
					sheet.addCell(cl);
				}
				for(int row=0; row<list.size(); row++)
				{
					String[] e = list.get(row);
					for(int i=0; i<e.length; i++)
					{
						if(i == 1)
						{
							Label cl = new Label(0, row+1, e[i]);
							sheet.addCell(cl);
						}
						else if(i == 2)
						{
							Label cl = new Label(1, row+1, e[i]);
							sheet.addCell(cl);
						}
					}
				}
			}
			
			workbook.write();
		    workbook.close();
		    
		    doc = bout.toByteArray();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch (WriteException e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	private void checkAndAddToSheet(WritableSheet sheet, int row, String header, String[] columns, String value)
	{
		for(int c=0; c<columns.length; c++)
		{
			if(columns[c].equalsIgnoreCase(header))
			{
				Label cl = new Label(c, row, value);
				try {
					sheet.addCell(cl);
				}
				catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
