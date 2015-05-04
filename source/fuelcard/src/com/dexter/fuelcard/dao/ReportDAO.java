package com.dexter.fuelcard.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.dexter.fuelcard.model.BankRecord;
import com.dexter.fuelcard.model.Car;
import com.dexter.fuelcard.model.Partner;
import com.dexter.fuelcard.model.TrackerRecord;
import com.dexter.fuelcard.util.Env;

public class ReportDAO
{
	/*
	 * Private members for database access, retrieval and manipulation.
	 * */
	private Connection con;
	private PreparedStatement statement;
	private ResultSet result;
	
	//private int output = -1;
	
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
	
	public ArrayList<String[]> getTopVehicleMakes(Long partner_id)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			connectToDB();
			
			if(partner_id != null)
			{
				statement = con.prepareStatement("SELECT vm.name, count(cr.id) as 'cars_count' FROM VEHICLEMAKE_TB vm " +
						"inner join VEHICLEMODEL_TB model on model.make_id = vm.id inner join car_tb cr on cr.model_id = model.id " +
						"where vm.partner_id = ? group by vm.name order by cars_count");
				statement.setLong(1, partner_id);
			}
			else
			{
				statement = con.prepareStatement("SELECT vm.name, count(cr.id) as 'cars_count' FROM VEHICLEMAKE_TB vm " +
						"inner join VEHICLEMODEL_TB model on model.make_id = vm.id inner join car_tb cr on cr.model_id = model.id " +
						"group by vm.name order by cars_count");
			}
			
			result = statement.executeQuery();
			while(result.next())
			{
				String[] e = new String[2];
				for(int i=0; i<2; i++)
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
	
	public ArrayList<String[]> getTopVehicleTypes(Long partner_id)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			connectToDB();
			
			if(partner_id != null)
			{
				statement = con.prepareStatement("SELECT vt.name, count(cr.id) as 'cars_count' FROM VEHICLETYPE_TB vt " +
						"inner join VEHICLEMODEL_TB model on model.type_id = vt.id inner join car_tb cr on cr.model_id = model.id " +
						"where vt.partner_id = ? group by vt.name order by cars_count");
				statement.setLong(1, partner_id);
			}
			else
			{
				statement = con.prepareStatement("SELECT vt.name, count(cr.id) as 'cars_count' FROM VEHICLETYPE_TB vt " +
						"inner join VEHICLEMODEL_TB model on model.type_id = vt.id inner join car_tb cr on cr.model_id = model.id " +
						"group by vt.name order by cars_count");
			}
			
			result = statement.executeQuery();
			while(result.next())
			{
				String[] e = new String[2];
				for(int i=0; i<2; i++)
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
	
	@SuppressWarnings("deprecation")
	public ArrayList<String[]> searchDailyLogSheet(Date d1, Date d2, Car car)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			d2.setHours(23);
			d2.setMinutes(59);
			d2.setSeconds(59);
		}
		catch(Exception ex){}
		
		try
		{
			Vector<BankRecord> brList = new BankRecordDAO().search(d1, d2, car);
			Vector<TrackerRecord> trList = new TrackerRecordDAO().search(d1, d2, car);
			
			BankRecord curBankRec = null;
			TrackerRecord curTrackerRec = null;
			
			TrackerRecord prevTrackerRec = null;
			
			if(brList.size() == trList.size())
			{
				for(int i=0; i<brList.size(); i++)
				{
					curBankRec = brList.get(i);
					curTrackerRec = trList.get(i);
					
					String[] e = new String[12];
					
					e[0] = curBankRec.getTranTime().toLocaleString(); // date and time
					e[1] = curBankRec.getCardAcceptorLoc(); // location
					e[11] = curTrackerRec.getAddress();
					
					try
					{
						double cardBal = curBankRec.getCardBal();
						double tranAmt = curBankRec.getTranAmt();
						/*try
						{
						cardBal = cardBal/100.00;
						tranAmt = tranAmt/100.00;
						}
						catch(Exception ig){}*/
						
						double amtOnCard = cardBal+tranAmt;
						
						e[2] = ""+amtOnCard; // amount on card, which is cardbalance+tranamt
					}
					catch(Exception ex)
					{
						e[2] = "ERROR"; // number error
					}
					
					if(prevTrackerRec == null)
						e[3] = "N/A";
					else if(prevTrackerRec != null)
						e[3] = ""+prevTrackerRec.getOdometer(); // previous odometer
					
					e[4] = ""+curTrackerRec.getOdometer(); // current odometer
					
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
					
					try
					{
						e[6] = ""+(curBankRec.getTranAmt()); // amount /100.00
					}
					catch(Exception ex)
					{
						e[6] = ""+curBankRec.getTranAmt();
					}
					
					try
					{
						double init = curTrackerRec.getInitFuelLvl();
						double finalf = curTrackerRec.getFinalFuelLvl();
						
						double bought = finalf-init;
						e[7] = ""+bought; // litres bought = cost/rate
					}
					catch(Exception ex)
					{
						e[7] = "N/A";
					}
					
					try
					{
						e[8] = ""+(curBankRec.getCardBal()); // balance on card /100.00
					}
					catch(Exception ex)
					{
						e[8] = ""+curBankRec.getCardBal();
					}
					
					e[9] = ""+curTrackerRec.getInitFuelLvl();
					e[10] = ""+curTrackerRec.getFinalFuelLvl();
					
					prevTrackerRec = curTrackerRec;
					
					list.add(e);
				}
			}
			else if(brList.size() > trList.size())
			{
				for(int i=0; i<brList.size(); i++)
				{
					curBankRec = brList.get(i);
					curTrackerRec = null;
					
					for(TrackerRecord tr : trList)
					{
						long diff = curBankRec.getTranTime().getTime() - tr.getTranTime().getTime();
						diff = Math.abs(diff);
						
						if(diff <= (1000*60*14)) // 14 minutes
						{
							curTrackerRec = tr;
							break;
						}
					}
					
					String[] e = new String[12];
					
					e[0] = curBankRec.getTranTime().toLocaleString(); // date and time
					e[1] = curBankRec.getCardAcceptorLoc(); // location
					
					if(curTrackerRec != null)
						e[11] = curTrackerRec.getAddress();
					else
						e[11] = "N/A";
					
					try
					{
						double cardBal = curBankRec.getCardBal();
						double tranAmt = curBankRec.getTranAmt();
						/*try
						{
						cardBal = cardBal/100.00;
						tranAmt = tranAmt/100.00;
						}
						catch(Exception ig){}*/
						double amtOnCard = cardBal+tranAmt;
						
						e[2] = ""+amtOnCard; // amount on card, which is cardbalance+tranamt
					}
					catch(Exception ex)
					{
						e[2] = "ERROR"; // number error
					}
					
					if(curTrackerRec != null)
					{
						if(prevTrackerRec == null)
							e[3] = "N/A";
						else if(prevTrackerRec != null)
						{
							try
							{
								e[3] = ""+prevTrackerRec.getOdometer(); // previous odometer
							}
							catch(Exception ex)
							{
								e[3] = "N/A";
							}
						}
						e[4] = ""+curTrackerRec.getOdometer(); // current odometer
						
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
					}
					else
					{
						e[3] = "N/A";
						e[4] = "N/A";
						e[5] = "N/A";
					}
					
					try
					{
						e[6] = ""+(curBankRec.getTranAmt()); // amount /100.00
					}
					catch(Exception ex)
					{
						e[6] = ""+curBankRec.getTranAmt(); // amount
					}
					
					if(curTrackerRec != null)
					{
						try
						{
							double init = curTrackerRec.getInitFuelLvl();
							double finalf = curTrackerRec.getFinalFuelLvl();
							
							double bought = finalf-init;
							e[7] = ""+bought; // litres bought = cost/rate
						}
						catch(Exception ex)
						{
							e[7] = "N/A";
						}
					}
					else
					{
						e[7] = "N/A";
					}
					
					try
					{
						e[8] = ""+(curBankRec.getCardBal()); // balance on card /100.00
					}
					catch(Exception ex)
					{
						e[8] = ""+curBankRec.getCardBal(); // balance on card
					}
					
					if(curTrackerRec != null)
					{
						e[9] = ""+curTrackerRec.getInitFuelLvl();
						e[10] = ""+curTrackerRec.getFinalFuelLvl();
					}
					else
					{
						e[9] = "N/A";
						e[10] = "N/A";
					}
					
					prevTrackerRec = curTrackerRec;
					
					list.add(e);
				}
			}
			else
			{
				for(int i=0; i<trList.size(); i++)
				{
					curBankRec = null;
					curTrackerRec = trList.get(i);
					
					for(BankRecord br : brList)
					{
						long diff = curTrackerRec.getTranTime().getTime() - br.getTranTime().getTime();
						diff = Math.abs(diff);
						
						if(diff <= (1000*60*14)) // 14 minutes
						{
							curBankRec = br;
							break;
						}
					}
					
					String[] e = new String[12];
					
					e[11] = curTrackerRec.getAddress();
					
					if(curBankRec != null)
					{
						e[0] = curBankRec.getTranTime().toLocaleString(); // date and time
						e[1] = curBankRec.getCardAcceptorLoc(); // location
						
						try
						{
							double cardBal = curBankRec.getCardBal();
							double tranAmt = curBankRec.getTranAmt();
							/*try
							{
							cardBal = cardBal/100.00;
							tranAmt = tranAmt/100.00;
							}
							catch(Exception ig){}*/
							double amtOnCard = cardBal+tranAmt;
							
							e[2] = ""+amtOnCard; // amount on card, which is cardbalance+tranamt
						}
						catch(Exception ex)
						{
							e[2] = "ERROR"; // number error
						}
					}
					else
					{
						e[0] = "N/A";
						e[1] = "N/A";
						e[2] = "N/A";
					}
					
					if(prevTrackerRec == null)
						e[3] = "N/A";
					else if(prevTrackerRec != null)
					{
						try
						{
							e[3] = ""+prevTrackerRec.getOdometer(); // previous odometer
						}
						catch(Exception ex)
						{
							e[3] = "N/A";
						}
					}
					e[4] = ""+curTrackerRec.getOdometer(); // current odometer
					
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
					
					if(curBankRec != null)
					{
						try
						{
							e[6] = ""+(curBankRec.getTranAmt()); // amount /100.00
						}
						catch(Exception ex)
						{
							e[6] = ""+curBankRec.getTranAmt(); // amount
						}
					}
					else
						e[6] = "N/A";
					
					try
					{
						double init = curTrackerRec.getInitFuelLvl();
						double finalf = curTrackerRec.getFinalFuelLvl();
						
						double bought = finalf-init;
						e[7] = ""+bought; // litres bought = cost/rate
					}
					catch(Exception ex)
					{
						e[7] = "N/A";
					}
					
					if(curBankRec != null)
					{
						try
						{
							e[8] = ""+(curBankRec.getCardBal()); // balance on card /100.00
						}
						catch(Exception ex)
						{
							e[8] = ""+curBankRec.getCardBal(); // balance on card
						}
					}
					else
						e[8] = "N/A";
					
					e[9] = ""+curTrackerRec.getInitFuelLvl();
					e[10] = ""+curTrackerRec.getFinalFuelLvl();
					
					prevTrackerRec = curTrackerRec;
					
					list.add(e);
				}
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
	
	@SuppressWarnings("deprecation")
	public ArrayList<String[]> searchFuelPurchaseReport2(Date d1, Date d2, long vehicle_id)
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
			
			statement = con.prepareStatement("SELECT br.tranTime, cr.regnumber, br.tranAmt, cr.fuelType, br.VEHICLE_ID, br.cardAcceptorLoc FROM bankrecord_tb br " +
					"inner join car_tb cr on cr.ID = br.VEHICLE_ID " +
					"where (br.tranTime between ? and ?) and br.VEHICLE_ID = ? order by br.tranTime");
			statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			statement.setLong(3, vehicle_id);
			
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
			CarDAO cDAO = new CarDAO();
			for(int i=0; i<size; i++)
			{
				String[] rec = all.get(i);
				
				String[] e = new String[8];
				e[0] = rec[0];
				e[1] = rec[1];
				e[2] = rec[2];
				e[3] = rec[3];
				
				Car car = cDAO.getCarById(Long.parseLong(rec[4]));
				if(car != null)
				{
					Partner partner = car.getPartner();
					double unitP = 1;
					if(e[3]!=null && e[3].equalsIgnoreCase("DISEL"))
					{
						unitP = partner.getDisealUnitPrice();
					}
					else if(e[3]!=null && e[3].equalsIgnoreCase("PETROL"))
					{
						unitP = partner.getPetrolUnitPrice();
					}
					
					e[4] = ""+unitP;
					
					BigDecimal amt = new BigDecimal(Double.parseDouble(e[2]));
					BigDecimal qty = amt.divide(new BigDecimal(unitP));
					qty = qty.setScale(2, RoundingMode.HALF_UP);
					
					e[5] = qty.toPlainString();
				}
				
				e[6] = rec[5];
				if(car != null && car.getAssignedUser() != null)
				{
					e[7] = car.getAssignedUser().getFullname();
				}
				else
					e[7] = "N/A";
				
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
	
	@SuppressWarnings("deprecation")
	public ArrayList<String[]> searchFuelPurchaseReport(Date d1, Date d2, long vehicle_id)
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
			
			/*
			 * 
				select d.ID, d.LOCATION, d.COST, d.FINALFUELLVL, d.FUELRATE, d.INITFUELLVL, d.ODOMETER, d.QUANTITY, d.TRANTIME, d.DRIVER_ID, d.CAR_ID, " +
					"t.ID, t.ADDRESS, t.COMP, t.COST, t.FINALFUELLVL, t.INITFUELLVL, t.MODEL, t.ODOMETER, t.QUANTITY, t.TRANTIME, t.TRANTYPE, t.UNITNAME, t.YEAR, " +
					"d.REGNUMBER, u.FULLNAME from [dbo].[DRIVERRECORD_TB] d inner join [dbo].[TRACKERRECORD_TB] t on t.UNITNAME = d.REGNUMBER and d.ID = t.ID inner join [dbo].[USER_TB] u on u.ID = d.DRIVER_ID " +
					"where d.TRANTIME between ? and ?
			 * */
			// 14 minutes
			statement = con.prepareStatement("SELECT tr.trantime, cr.regnumber, 'ADMIN', '', '', tr.odometer, " +
					"tr.finalfuellvl-tr.initfuellvl as fuelqty, br.tranamt, tr.initfuellvl, tr.finalfuellvl," +
					"'PETROL', br.cardacceptorloc, ur.fullname FROM trackerrecord_tb tr " +
					"inner join car_tb cr on cr.ID = tr.VEHICLE_ID " +
					"left outer join user_tb ur on ur.ID = cr.assignedUser_id " + 
					"left outer join bankrecord_tb br on br.VEHICLE_ID = tr.VEHICLE_ID and " +
					"ABS(TIMESTAMPDIFF(minute, tr.trantime, br.trantime)) <= 60 " +
					"where (tr.trantime between ? and ?) and tr.VEHICLE_ID = ? order by tr.trantime");
			statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
			statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			statement.setLong(3, vehicle_id);
			
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
				
				e[0] = rec[0]; // date and time
				e[1] = rec[1]; // vin, reg number
				e[2] = rec[2]; // created by
				
				if(i == 0)
				{
					e[3] = "N/A";
					e[4] = "N/A";
				}
				else if(i > 0)
				{
					e[3] = all.get(i)[3]; // previous odometer
					e[4] = all.get(i-1)[5]; // previous odometer
				}
				
				e[5] = rec[4]; // driver odometer
				e[6] = rec[5]; // tracker odometer
				
				try
				{
					double prev = Double.parseDouble(e[4]);
					double curr = Double.parseDouble(e[6]);
					double covered = curr - prev;
					e[7] = ""+covered; // covered = current-previous
				}
				catch(Exception ex)
				{
					e[7] = "N/A";
				}
				
				try
				{
					//double rate = Double.parseDouble(rec[4]);
					//double cost = Double.parseDouble(rec[2]);
					
					//double bought = cost/rate;
					//e[8] = ""+bought; // litres bought = cost/rate
					e[8] = rec[6];
				}
				catch(Exception ex)
				{
					e[8] = "N/A";
				}
				
				try
				{
					e[9] = ""+Double.parseDouble(rec[7]); // amount /100.00
				}
				catch(Exception ex)
				{
					e[9] = rec[7]; // amount
				}
				
				// (Current Odometer reading - Previous Odometer reading)/(Final Fuel Level - Initial Fuel Level)
				try
				{
					double curOdo = Double.parseDouble(e[6]);
					double prevOdo = Double.parseDouble(e[4]);
					
					double finFuelLvl = Double.parseDouble(rec[9]);
					double iniFuelLvl = Double.parseDouble(rec[8]);
					
					e[13] = ""+((curOdo-prevOdo) / (finFuelLvl-iniFuelLvl));
					//Double.parseDouble(e[13]);
				}
				catch(Exception ex)
				{
					e[13] = "N/A";
				}
				
				e[10] = "PETROL"; // fuel brand
				e[11] = rec[11]; // dealer
				e[12] = rec[12]; // purchase by
				
				e[14] = rec[8];
				e[15] = rec[9];
				
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
	
	@SuppressWarnings("deprecation")
	public ArrayList<String[]> exceptionTransactions(Date d1, Date d2, Long partner_id)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		// Tracker record without bank record
		// Tracker record with final fuel level more than vehicle calibrated capacity
		
		try
		{
			d2.setHours(23);
			d2.setMinutes(59);
			d2.setSeconds(59);
		}
		catch(Exception ex){}
		
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		try
		{
			connectToDB();
			
			// Bank record without tracker record
			Vector<BankRecord> brList = new BankRecordDAO().search(d1, d2, partner_id);
			for(BankRecord br : brList)
			{
				if(br.getVehicle() != null)
				{
					// 14 minutes
					statement = con.prepareStatement("SELECT * FROM trackerrecord_tb " +
							"where VEHICLE_ID = ? and " +
							"ABS(TIMESTAMPDIFF(minute, trantime, ?)) <= 14 ");
					statement.setLong(1, br.getVehicle().getId());
					statement.setTimestamp(2, Timestamp.valueOf(sdformat.format(br.getTranTime())));// br.getTranTime().getTime()
					
					result = statement.executeQuery();
					boolean found = false;
					while(result.next())
						found = true;
					if(!found)
					{
						// this is an exception
						String[] e = new String[4];
						e[0] = "BANK-WITHOUT-TRACKER";
						e[1] = br.getVehicle().getRegNumber();
						e[2] = br.getTranTime().toLocaleString();
						try
						{
						e[3] = "Amount: " + (br.getTranAmt()); // /100.00
						}
						catch(Exception ex)
						{
							e[3] = "Amount: " + br.getTranAmt();
						}
						list.add(e);
					}
				}
			}
			
			Vector<TrackerRecord> trList = new TrackerRecordDAO().search(d1, d2, partner_id);
			for(TrackerRecord tr : trList)
			{
				if(tr.getVehicle() != null)
				{
					// 14 minutes
					statement = con.prepareStatement("SELECT * FROM bankrecord_tb " +
							"where VEHICLE_ID = ? and " +
							"ABS(TIMESTAMPDIFF(minute, trantime, ?)) <= 14 ");
					statement.setLong(1, tr.getVehicle().getId());
					statement.setTimestamp(2, Timestamp.valueOf(sdformat.format(tr.getTranTime())));// tr.getTranTime().getTime()
					
					result = statement.executeQuery();
					boolean found = false;
					while(result.next())
						found = true;
					if(!found)
					{
						// this is an exception
						String[] e = new String[4];
						e[0] = "TRACKER-WITHOUT-BANK";
						e[1] = tr.getVehicle().getRegNumber();
						e[2] = tr.getTranTime().toLocaleString();
						e[3] = "Litres: " + (tr.getFinalFuelLvl()-tr.getInitFuelLvl());
						list.add(e);
					}
					
					if(tr.getFinalFuelLvl() > tr.getVehicle().getCalibratedCapacity())
					{
						String[] e = new String[4];
						e[0] = "FUEL-LEVEL-ERROR";
						e[1] = tr.getVehicle().getRegNumber();
						e[2] = tr.getTranTime().toLocaleString();
						e[3] = "Final fuel level: " + tr.getFinalFuelLvl() + ", Calibrated capacity: " + tr.getVehicle().getCalibratedCapacity();
						list.add(e);
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return list;
	}
	
	@SuppressWarnings("deprecation")
	public ArrayList<String[]> highestFuelConsumption(Date d1, Date d2, Long partner_id)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			d2.setHours(23);
			d2.setMinutes(59);
			d2.setSeconds(59);
		}
		catch(Exception ig){}
		
		try
		{
			Hashtable<Integer, ArrayList<TrackerRecord>> vehicleTRList = new Hashtable<Integer, ArrayList<TrackerRecord>>();
			
			Vector<TrackerRecord> trList = new TrackerRecordDAO().search(d1, d2, partner_id);
			for(TrackerRecord tr : trList)
			{
				ArrayList<TrackerRecord> arrL = new ArrayList<TrackerRecord>();
				if(vehicleTRList.containsKey(tr.getUnitID()))
					arrL = vehicleTRList.get(tr.getUnitID());
				
				arrL.add(tr);
				vehicleTRList.put(tr.getUnitID(), arrL);
			}
			Integer[] keys = new Integer[vehicleTRList.keySet().size()];
			keys = vehicleTRList.keySet().toArray(keys);
			Hashtable<Double, Integer> allList = new Hashtable<Double, Integer>();
			for(int i=0; i<keys.length; i++)
			{
				ArrayList<TrackerRecord> vtrsList = vehicleTRList.get(keys[i]);
				double vtconsumption = 0.0;
				for(int a=0; a<vtrsList.size(); a++)
				{
					TrackerRecord tr = vtrsList.get(a);
					if(a == 0) // this is the first record
					{}
					else // inbetween records
					{
						double curcomp = vtrsList.get(a-1).getFinalFuelLvl()-tr.getInitFuelLvl();
						vtconsumption += curcomp;
					}
				}
				allList.put(vtconsumption, keys[i]);
			}
			Double[] comsupvalues = new Double[allList.size()];
			comsupvalues = allList.keySet().toArray(comsupvalues);
			Arrays.sort(comsupvalues);
			for(int i=(comsupvalues.length-1); i>=0; i--) // take from the highest down
			{
				int key = allList.get(comsupvalues[i]);
				ArrayList<TrackerRecord> vtrsList = vehicleTRList.get(key);
				String[] e = new String[]{vtrsList.get(0).getVehicle().getRegNumber(), ""+comsupvalues[i]};
				list.add(e);
				if(i == comsupvalues.length-10) // top ten
					break;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return list;
	}
	
	@SuppressWarnings("deprecation")
	public ArrayList<String[]> highestFuelPurchase(Date d1, Date d2, Long partner_id)
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
			if(partner_id != null)
			{
				statement = con.prepareStatement("select br.vehicle_id, c.regnumber, (sum(br.tranamt)) as total from bankrecord_tb br " +
						"inner join car_tb c on c.id = br.vehicle_id " +
						"where (br.trantime between ? and ?) and c.partner.id=:partner_id" +
						"group by br.vehicle_id, c.regnumber order by total desc;");
				statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
				statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
				statement.setLong(3, partner_id);
			}
			else
			{
				statement = con.prepareStatement("select br.vehicle_id, c.regnumber, (sum(br.tranamt)) as total from bankrecord_tb br " +
						"inner join car_tb c on c.id = br.vehicle_id " +
						"where br.trantime between ? and ? " +
						"group by br.vehicle_id, c.regnumber order by total desc;");
				statement.setTimestamp(1, Timestamp.valueOf(y1+"-"+m1+"-"+dd1+" 00:00:00"));
				statement.setTimestamp(2, Timestamp.valueOf(y2+"-"+m2+"-"+dd2+" 23:59:59"));
			}
			
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
	
	@SuppressWarnings("deprecation")
	public ArrayList<String[]> longestDistance(Date d1, Date d2, Long partner_id)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			d2.setHours(23);
			d2.setMinutes(59);
			d2.setSeconds(59);
		}
		catch(Exception ig){}
		
		try
		{
			Hashtable<Integer, ArrayList<TrackerRecord>> vehicleTRList = new Hashtable<Integer, ArrayList<TrackerRecord>>();
			
			Vector<TrackerRecord> trList = new TrackerRecordDAO().search(d1, d2, partner_id);
			for(TrackerRecord tr : trList)
			{
				ArrayList<TrackerRecord> arrL = new ArrayList<TrackerRecord>();
				if(vehicleTRList.containsKey(tr.getUnitID()))
					arrL = vehicleTRList.get(tr.getUnitID());
				
				arrL.add(tr);
				vehicleTRList.put(tr.getUnitID(), arrL);
			}
			Integer[] keys = new Integer[vehicleTRList.keySet().size()];
			keys = vehicleTRList.keySet().toArray(keys);
			Hashtable<Double, Integer> allList = new Hashtable<Double, Integer>();
			for(int i=0; i<keys.length; i++)
			{
				ArrayList<TrackerRecord> vtrsList = vehicleTRList.get(keys[i]);
				double vtdistance = 0.0;
				for(int a=0; a<vtrsList.size(); a++)
				{
					TrackerRecord tr = vtrsList.get(a);
					if(a == 0) // this is the first record
					{}
					else // inbetween records
					{
						double curdistance = tr.getOdometer() - vtrsList.get(a-1).getOdometer();
						vtdistance += curdistance;
					}
				}
				allList.put(vtdistance, keys[i]);
			}
			Double[] comsupvalues = new Double[allList.size()];
			comsupvalues = allList.keySet().toArray(comsupvalues);
			Arrays.sort(comsupvalues);
			for(int i=(comsupvalues.length-1); i>=0; i--) // take from the highest down
			{
				int key = allList.get(comsupvalues[i]);
				ArrayList<TrackerRecord> vtrsList = vehicleTRList.get(key);
				String[] e = new String[]{vtrsList.get(0).getVehicle().getRegNumber(), ""+comsupvalues[i]};
				list.add(e);
				if(i == comsupvalues.length-10) // top ten
					break;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		/*try
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
		}*/
		
		return list;
	}
	
	@SuppressWarnings("deprecation")
	public ArrayList<String[]> bestEfficiency(Date d1, Date d2, Long partner_id)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try
		{
			d2.setHours(23);
			d2.setMinutes(59);
			d2.setSeconds(59);
		}
		catch(Exception ig){}
		
		try
		{
			Hashtable<Integer, ArrayList<TrackerRecord>> vehicleTRList = new Hashtable<Integer, ArrayList<TrackerRecord>>();
			
			Vector<TrackerRecord> trList = new TrackerRecordDAO().search(d1, d2, partner_id);
			for(TrackerRecord tr : trList)
			{
				ArrayList<TrackerRecord> arrL = new ArrayList<TrackerRecord>();
				if(vehicleTRList.containsKey(tr.getUnitID()))
					arrL = vehicleTRList.get(tr.getUnitID());
				
				arrL.add(tr);
				vehicleTRList.put(tr.getUnitID(), arrL);
			}
			Integer[] keys = new Integer[vehicleTRList.keySet().size()];
			keys = vehicleTRList.keySet().toArray(keys);
			Hashtable<Double, Integer> allList = new Hashtable<Double, Integer>();
			for(int i=0; i<keys.length; i++)
			{
				ArrayList<TrackerRecord> vtrsList = vehicleTRList.get(keys[i]);
				double vtefficiency = 0.0;
				for(int a=0; a<vtrsList.size(); a++)
				{
					TrackerRecord tr = vtrsList.get(a);
					if(a == 0) // this is the first record
					{}
					else // inbetween records
					{
						double curodochange = tr.getOdometer()-vtrsList.get(a-1).getOdometer();
						double curcomp = vtrsList.get(a-1).getFinalFuelLvl()-tr.getInitFuelLvl();
						vtefficiency += (curodochange/curcomp);
					}
				}
				allList.put(vtefficiency, keys[i]);
			}
			Double[] comsupvalues = new Double[allList.size()];
			comsupvalues = allList.keySet().toArray(comsupvalues);
			Arrays.sort(comsupvalues);
			for(int i=(comsupvalues.length-1); i>=0; i--) // take from the highest down
			{
				int key = allList.get(comsupvalues[i]);
				ArrayList<TrackerRecord> vtrsList = vehicleTRList.get(key);
				String[] e = new String[]{vtrsList.get(0).getVehicle().getRegNumber(), ""+comsupvalues[i]};
				list.add(e);
				if(i == comsupvalues.length-10) // top ten
					break;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		/*try
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
		}*/
		
		return list;
	}
	
	@SuppressWarnings("deprecation")
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
	
	@SuppressWarnings("deprecation")
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
