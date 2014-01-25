package com.dexter.fmr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.dexter.fmr.model.Car;
import com.dexter.fmr.util.Env;

public class ReportDAO
{
	/*
	 * Private members for database access, retrieval and manipulation.
	 * */
	private Connection con;
	private PreparedStatement statement;
	private ResultSet result;
	
	private int output = -1;
	
	public ReportDAO()
	{}
	
	/**
	 * Used internally to connect to the database.
	 * */
	private void connectToDB() throws Exception
	{
		closeConnection();
		
		con = Env.getConnection();
	}
	
	/**
	 * Used internally to close the connection after use. 
	 * */
	private void closeConnection()
	{
		if(result != null)
		{
			try
			{
				result.close();
				result = null;
			}
			catch(Exception ignore){}
		}
		if(con != null)
		{
			try
			{
				con.close();
				con = null;
			}
			catch(Exception ignore){}
		}
	}
	
	public ArrayList<String[]> searchDailyLogSheet(Date d1, Date d2, Car car)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			/*java.util.Calendar c = java.util.Calendar.getInstance();
			c.set(java.util.Calendar.MONTH, month-1);
			
			String y1 = ""+c.get(c.YEAR), m1 = ""+(c.get(c.MONTH)+1), dd1 = ""+c.get(c.DAY_OF_MONTH);
			String y2 = ""+c.get(c.YEAR), m2 = ""+(c.get(c.MONTH)+1), dd2 = ""+c.getMaximum(c.DAY_OF_MONTH);
			
			m1 = (m1.length() == 1) ? "0"+m1 : m1;
			dd1 = (dd1.length() == 1) ? "0"+dd1 : dd1;
			m2 = (m2.length() == 1) ? "0"+m2 : m2;
			dd2 = (dd2.length() == 1) ? "0"+dd2 : dd2;*/
			String y1 = ""+(d1.getYear()+1900), m1 = ""+(d1.getMonth()+1), dd1 = ""+d1.getDate();
			String y2 = ""+(d2.getYear()+1900), m2 = ""+(d2.getMonth()+1), dd2 = ""+d2.getDate();
			
			m1 = (m1.length() == 1) ? "0"+m1 : m1;
			dd1 = (dd1.length() == 1) ? "0"+dd1 : dd1;
			m2 = (m2.length() == 1) ? "0"+m2 : m2;
			dd2 = (dd2.length() == 1) ? "0"+dd2 : dd2;
			
			ArrayList<String[]> all = new ArrayList<String[]>();
			
			connectToDB();
			
			statement = con.prepareStatement("select b.ID, b.CARDACCEPTORID, b.CARDACCEPTORLOC, b.CARDBAL, b.CARDPAN, " +
					"b.CARDSTATUS, b.CUSNAME, b.CUSPHONE, b.RETRIEVALREFNUM, b.SCHEMEOWNER, b.TRANAMT, b.TRANFEES, b.TRANSTATUS, " +
					"b.TRANTIME, b.TRANTYPE, d.ID, d.LOCATION, d.COST, d.FINALFUELLVL, d.FUELRATE, d.INITFUELLVL, d.ODOMETER, d.QUANTITY, d.TRANTIME, d.DRIVER_ID, d.CAR_ID, d.REFNUMBER " +
					"from [dbo].[BANKRECORD_TB] b inner join [dbo].[DRIVERRECORD_TB] d on d.REGNUMBER = b.CUSNAME and d.REFNUMBER = b.RETRIEVALREFNUM where b.CUSNAME = ? and d.CAR_ID = ? " +
					"and b.TRANTIME between ? and ?");
			statement.setString(1, car.getRegNumber());
			statement.setLong(2, car.getId());
			statement.setTimestamp(3, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(4, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				all.add(e);
			}
			int size = all.size();
			for(int i=0; i<size; i++)
			{
				String[] rec = all.get(i);
				
				String[] e = new String[11];
				
				e[0] = rec[13]; // date and time
				e[1] = rec[16]; // location
				
				try
				{
					double cardBal = Double.parseDouble(rec[3]);
					double tranAmt = Double.parseDouble(rec[10]);
					
					double amtOnCard = cardBal+tranAmt;
					
					e[2] = ""+amtOnCard; // amount on card, which is cardbalance+tranamt
				}
				catch(Exception ex)
				{
					e[2] = "ERROR"; // number error
				}
				
				if(i == 0)
					e[3] = "N/A";
				else if(i > 0)
					e[3] = all.get(i-1)[21]; // previous odometer
				
				e[4] = rec[21]; // current odometer
				
				try
				{
					double prev = Double.parseDouble(e[3]);
					double curr = Double.parseDouble(e[4]);
					double covered = curr - prev;
					e[5] = ""+covered; // covered = current-previous
				}
				catch(Exception ex)
				{
					e[5] = "N/A";
				}
				
				e[6] = rec[17]; // amount
				
				try
				{
					double rate = Double.parseDouble(rec[19]);
					double cost = Double.parseDouble(rec[17]);
					
					double bought = cost/rate;
					e[7] = ""+bought; // litres bought = cost/rate
				}
				catch(Exception ex)
				{
					e[7] = "N/A";
				}
				
				e[8] = rec[3]; // balance on card
				
				e[9] = rec[20];
				e[10] = rec[18];
				
				list.add(e);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
	
	public ArrayList<String[]> searchFuelPurchaseReport(Date d1, Date d2)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			String y1 = ""+(d1.getYear()+1900), m1 = ""+(d1.getMonth()+1), dd1 = ""+d1.getDate();
			String y2 = ""+(d2.getYear()+1900), m2 = ""+(d2.getMonth()+1), dd2 = ""+d2.getDate();
			
			m1 = (m1.length() == 1) ? "0"+m1 : m1;
			dd1 = (dd1.length() == 1) ? "0"+dd1 : dd1;
			m2 = (m2.length() == 1) ? "0"+m2 : m2;
			dd2 = (dd2.length() == 1) ? "0"+dd2 : dd2;
			
			ArrayList<String[]> all = new ArrayList<String[]>();
			
			connectToDB();
			
			statement = con.prepareStatement("select d.ID, d.LOCATION, d.COST, d.FINALFUELLVL, d.FUELRATE, d.INITFUELLVL, d.ODOMETER, d.QUANTITY, d.TRANTIME, d.DRIVER_ID, d.CAR_ID, " +
					"t.ID, t.ADDRESS, t.COMP, t.COST, t.FINALFUELLVL, t.INITFUELLVL, t.MODEL, t.ODOMETER, t.QUANTITY, t.TRANTIME, t.TRANTYPE, t.UNITNAME, t.YEAR, " +
					"d.REGNUMBER, u.FULLNAME from [dbo].[DRIVERRECORD_TB] d inner join [dbo].[TRACKERRECORD_TB] t on t.UNITNAME = d.REGNUMBER and d.ID = t.ID inner join [dbo].[USER_TB] u on u.ID = d.DRIVER_ID " +
					"where d.TRANTIME between ? and ?");
			statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				all.add(e);
			}
			int size = all.size();
			for(int i=0; i<size; i++)
			{
				String[] rec = all.get(i);
				
				String[] e = new String[16];
				
				e[0] = rec[8]; // date and time
				e[1] = rec[24]; // vin, reg number
				e[2] = "ADMIN"; // created by
				
				if(i == 0)
				{
					e[3] = "N/A";
					e[4] = "N/A";
				}
				else if(i > 0)
				{
					e[3] = all.get(i-1)[6]; // previous odometer
					e[4] = all.get(i-1)[18]; // previous odometer
				}
				
				e[5] = rec[6]; // driver odometer
				e[6] = rec[18]; // tracker odometer
				
				try
				{
					double prev = Double.parseDouble(e[3]);
					double curr = Double.parseDouble(e[5]);
					double covered = curr - prev;
					e[7] = ""+covered; // covered = current-previous
				}
				catch(Exception ex)
				{
					e[7] = "N/A";
				}
				
				try
				{
					double rate = Double.parseDouble(rec[4]);
					double cost = Double.parseDouble(rec[2]);
					
					double bought = cost/rate;
					e[8] = ""+bought; // litres bought = cost/rate
				}
				catch(Exception ex)
				{
					e[8] = "N/A";
				}
				e[9] = rec[2]; // amount
				
				// (Current Odometer reading - Previous Odometer reading)/(Final Fuel Level - Initial Fuel Level)
				try
				{
					double curOdo = Double.parseDouble(e[5]);
					double prevOdo = Double.parseDouble(e[3]);
					
					double finFuelLvl = Double.parseDouble(rec[15]);
					double iniFuelLvl = Double.parseDouble(rec[16]);
					
					e[13] = ""+((curOdo-prevOdo) / (finFuelLvl-iniFuelLvl));
					Double.parseDouble(e[13]);
				}
				catch(Exception ex)
				{
					e[13] = "0";
				}
				
				e[10] = "PETROL"; // fuel brand
				e[11] = rec[1]; // dealer
				e[12] = rec[25]; // purchase by
				
				e[14] = rec[16];
				e[15] = rec[15];
				
				list.add(e);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
	
	public ArrayList<String[]> exceptionTransactions(Date d1, Date d2)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			String y1 = ""+(d1.getYear()+1900), m1 = ""+(d1.getMonth()+1), dd1 = ""+d1.getDate();
			String y2 = ""+(d2.getYear()+1900), m2 = ""+(d2.getMonth()+1), dd2 = ""+d2.getDate();
			
			m1 = (m1.length() == 1) ? "0"+m1 : m1;
			dd1 = (dd1.length() == 1) ? "0"+dd1 : dd1;
			m2 = (m2.length() == 1) ? "0"+m2 : m2;
			dd2 = (dd2.length() == 1) ? "0"+dd2 : dd2;
			
			ArrayList<String[]> all = new ArrayList<String[]>();
			
			connectToDB();
			
			statement = con.prepareStatement("select d.ID, d.LOCATION, d.COST, d.FINALFUELLVL, d.FUELRATE, d.INITFUELLVL, d.ODOMETER, d.QUANTITY, d.TRANTIME, d.DRIVER_ID, d.CAR_ID, " +
					"t.ID, t.ADDRESS, t.COMP, t.COST, t.FINALFUELLVL, t.INITFUELLVL, t.MODEL, t.ODOMETER, t.QUANTITY, t.TRANTIME, t.TRANTYPE, t.UNITNAME, t.YEAR, " +
					"d.REGNUMBER, u.FULLNAME from [dbo].[DRIVERRECORD_TB] d inner join [dbo].[TRACKERRECORD_TB] t on t.UNITNAME = d.REGNUMBER and d.ID = t.ID inner join [dbo].[USER_TB] u on u.ID = d.DRIVER_ID " +
					"where d.TRANTIME between ? and ?");
			statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				all.add(e);
			}
			int size = all.size();
			for(int i=0; i<size; i++)
			{
				boolean b = false;
				String[] rec = all.get(i);
				
				String[] e = new String[16];
				
				e[0] = rec[8]; // date and time
				e[1] = rec[24]; // vin, reg number
				e[2] = "ADMIN"; // created by
				
				if(i == 0)
				{
					e[3] = "N/A";
					e[4] = "N/A";
				}
				else if(i > 0)
				{
					e[3] = all.get(i-1)[6]; // previous odometer
					e[4] = all.get(i-1)[18]; // previous odometer
				}
				
				e[5] = rec[6]; // driver odometer
				e[6] = rec[18]; // tracker odometer
				
				try
				{
					double prev = Double.parseDouble(e[3]);
					double curr = Double.parseDouble(e[5]);
					double covered = curr - prev;
					e[7] = ""+covered; // covered = current-previous
				}
				catch(Exception ex)
				{
					e[7] = "N/A";
				}
				
				try
				{
					double rate = Double.parseDouble(rec[4]);
					double cost = Double.parseDouble(rec[2]);
					
					double bought = cost/rate;
					e[8] = ""+bought; // litres bought = cost/rate
				}
				catch(Exception ex)
				{
					e[8] = "N/A";
				}
				e[9] = rec[2]; // amount
				
				// (Current Odometer reading - Previous Odometer reading)/(Final Fuel Level - Initial Fuel Level)
				try
				{
					double curOdo = Double.parseDouble(e[5]);
					double prevOdo = Double.parseDouble(e[3]);
					
					double finFuelLvl = Double.parseDouble(rec[15]);
					double iniFuelLvl = Double.parseDouble(rec[16]);
					
					e[13] = ""+((curOdo-prevOdo) / (finFuelLvl-iniFuelLvl));
					Double.parseDouble(e[13]);
				}
				catch(Exception ex)
				{
					e[13] = "0";
				}
				
				e[10] = "PETROL"; // fuel brand
				e[11] = rec[1]; // dealer
				e[12] = rec[25]; // purchase by
				
				e[14] = rec[16];
				e[15] = rec[15];
				
				try
				{
					double prev = Double.parseDouble(e[3]);
					double curr = Double.parseDouble(e[5]);
					double covered = curr - prev;
					
					double finFuelLvl = Double.parseDouble(rec[15]);
					double iniFuelLvl = Double.parseDouble(rec[16]);
					
					if(covered == 0 && (finFuelLvl-iniFuelLvl)==0)
						list.add(e);
				}
				catch(Exception ex)
				{}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
	
	public ArrayList<String[]> highestFuelConsumption(Date d1, Date d2)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			connectToDB();
			
			String y1 = ""+(d1.getYear()+1900), m1 = ""+(d1.getMonth()+1), dd1 = ""+d1.getDate();
			String y2 = ""+(d2.getYear()+1900), m2 = ""+(d2.getMonth()+1), dd2 = ""+d2.getDate();
			
			m1 = (m1.length() == 1) ? "0"+m1 : m1;
			dd1 = (dd1.length() == 1) ? "0"+dd1 : dd1;
			m2 = (m2.length() == 1) ? "0"+m2 : m2;
			dd2 = (dd2.length() == 1) ? "0"+dd2 : dd2;
			
			statement = con.prepareStatement("select '', sum(d.COST), sum(d.QUANTITY) as 'QUANTITY', d.DRIVER_ID, d.CAR_ID, d.REGNUMBER, u.FULLNAME" +
					" from [dbo].[DRIVERRECORD_TB] d inner join [dbo].[USER_TB] u on u.ID = d.DRIVER_ID " +
					"where d.TRANTIME between ? and ? group by d.CAR_ID,d.DRIVER_ID,d.REGNUMBER,u.FULLNAME order by QUANTITY desc");
			statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				list.add(e);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
	
	public ArrayList<String[]> highestFuelPurchase(Date d1, Date d2)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			connectToDB();
			
			String y1 = ""+(d1.getYear()+1900), m1 = ""+(d1.getMonth()+1), dd1 = ""+d1.getDate();
			String y2 = ""+(d2.getYear()+1900), m2 = ""+(d2.getMonth()+1), dd2 = ""+d2.getDate();
			
			m1 = (m1.length() == 1) ? "0"+m1 : m1;
			dd1 = (dd1.length() == 1) ? "0"+dd1 : dd1;
			m2 = (m2.length() == 1) ? "0"+m2 : m2;
			dd2 = (dd2.length() == 1) ? "0"+dd2 : dd2;
			
			statement = con.prepareStatement("select '', sum(d.COST) as 'COST', sum(d.QUANTITY), d.DRIVER_ID, d.CAR_ID, d.REGNUMBER, u.FULLNAME" +
					" from [dbo].[DRIVERRECORD_TB] d inner join [dbo].[USER_TB] u on u.ID = d.DRIVER_ID " +
					"where d.TRANTIME between ? and ? group by d.CAR_ID,d.DRIVER_ID,d.REGNUMBER,u.FULLNAME order by COST desc");
			statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				list.add(e);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
	
	public ArrayList<String[]> longestDistance(Date d1, Date d2)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			connectToDB();
			
			String y1 = ""+(d1.getYear()+1900), m1 = ""+(d1.getMonth()+1), dd1 = ""+d1.getDate();
			String y2 = ""+(d2.getYear()+1900), m2 = ""+(d2.getMonth()+1), dd2 = ""+d2.getDate();
			
			m1 = (m1.length() == 1) ? "0"+m1 : m1;
			dd1 = (dd1.length() == 1) ? "0"+dd1 : dd1;
			m2 = (m2.length() == 1) ? "0"+m2 : m2;
			dd2 = (dd2.length() == 1) ? "0"+dd2 : dd2;
			
			statement = con.prepareStatement("select (max(d.ODOMETER)-min(d.ODOMETER)) as 'DISTANCE', sum(d.COST) as 'COST', sum(d.QUANTITY), d.DRIVER_ID, d.CAR_ID, d.REGNUMBER, u.FULLNAME" +
					" from [dbo].[DRIVERRECORD_TB] d inner join [dbo].[USER_TB] u on u.ID = d.DRIVER_ID " +
					"where d.TRANTIME between ? and ? group by d.CAR_ID,d.DRIVER_ID,d.REGNUMBER,u.FULLNAME order by DISTANCE desc");
			statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				list.add(e);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
	
	public ArrayList<String[]> bestEfficiency(Date d1, Date d2)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			connectToDB();
			
			String y1 = ""+(d1.getYear()+1900), m1 = ""+(d1.getMonth()+1), dd1 = ""+d1.getDate();
			String y2 = ""+(d2.getYear()+1900), m2 = ""+(d2.getMonth()+1), dd2 = ""+d2.getDate();
			
			m1 = (m1.length() == 1) ? "0"+m1 : m1;
			dd1 = (dd1.length() == 1) ? "0"+dd1 : dd1;
			m2 = (m2.length() == 1) ? "0"+m2 : m2;
			dd2 = (dd2.length() == 1) ? "0"+dd2 : dd2;
			
			statement = con.prepareStatement("select ((max(d.ODOMETER)-min(d.ODOMETER))/sum(d.QUANTITY)) as 'EFFICIENCY', sum(d.COST) as 'COST', sum(d.QUANTITY), d.DRIVER_ID, d.CAR_ID, d.REGNUMBER, u.FULLNAME" +
					" from [dbo].[DRIVERRECORD_TB] d inner join [dbo].[USER_TB] u on u.ID = d.DRIVER_ID " +
					"where d.TRANTIME between ? and ? group by d.CAR_ID,d.DRIVER_ID,d.REGNUMBER,u.FULLNAME order by EFFICIENCY desc");
			statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				list.add(e);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
	
	public ArrayList<String[]> searchReport(Date tranDate)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			connectToDB();
			
			statement = con.prepareStatement("select * from transactions where [Transaction Date] between ? and ?");
			
			String yr = ""+(1900 + tranDate.getYear());
			String mm = ""+(tranDate.getMonth()+1);
			if(mm.length() == 1)
				mm = "0"+mm;
			String dd = ""+tranDate.getDate();
			if(dd.length() == 1)
				dd = "0"+dd;
			
			String date = yr+"-"+mm+"-"+dd;
			statement.setTimestamp(1, Timestamp.valueOf(date + " 00:00:000"));
			statement.setTimestamp(2, Timestamp.valueOf(date + " 23:59:000"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			/*String[] cols = new String[colCount];
			for(int i=0; i<colCount; i++)
				cols[i] = rsMeta.getColumnLabel(i+1);
			list.add(cols);*/
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				list.add(e);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
	
	public ArrayList<String[]> searchReport2(Date tranDate)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			connectToDB();
			
			statement = con.prepareStatement("select * from fuel_efficiency where [Transaction Date] between ? and ?");
			
			String yr = ""+(1900 + tranDate.getYear());
			String mm = ""+(tranDate.getMonth()+1);
			if(mm.length() == 1)
				mm = "0"+mm;
			String dd = ""+tranDate.getDate();
			if(dd.length() == 1)
				dd = "0"+dd;
			
			String date = yr+"-"+mm+"-"+dd;
			//statement.setDate(1, new java.sql.Date(tranDate.getTime()));
			statement.setTimestamp(1, Timestamp.valueOf(date + " 00:00:000"));
			statement.setTimestamp(2, Timestamp.valueOf(date + " 23:59:000"));
			
			result = statement.executeQuery();
			ResultSetMetaData rsMeta = result.getMetaData();
			int colCount = rsMeta.getColumnCount();
			
			/*String[] cols = new String[colCount];
			for(int i=0; i<colCount; i++)
				cols[i] = rsMeta.getColumnLabel(i+1);
			list.add(cols);*/
			
			while(result.next())
			{
				String[] e = new String[colCount];
				for(int i=0; i<colCount; i++)
					e[i] = result.getString(i+1);
				
				list.add(e);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
		
		return list;
	}
}
