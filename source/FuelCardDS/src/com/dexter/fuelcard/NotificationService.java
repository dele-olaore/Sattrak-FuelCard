/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dexter.fuelcard;

import com.dexter.fuelcard.model.BankRecord;
import com.dexter.fuelcard.model.CardBalanceNotification;
import com.dexter.fuelcard.model.Partner;
import com.dexter.fuelcard.model.Region;
import com.dexter.fuelcard.model.TrackerRecord;
import com.dexter.fuelcard.util.SMSGateway;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author oladele
 */
public class NotificationService extends Thread
{
    public static Logger logger = Logger.getLogger("FuelCardDS-NotificationService");
    
    private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    
    Timer timer, timerT1;
    TimerTask task, taskT1;
    
    private int tracker_interval = 5; // interval in minutes
    private String mail_host = "smtp.sattrakservices.com", mail_port = "25", smtp_username = "fms@sattrakservices.com", smtp_password = "Password";
    private String URL = "jdbc:mysql://localhost:3307/fuelcard", USER = "root", PASS = "sattrak";
    
    private String exceptionEventTableHTML = null;
    private String exceptionT1EventTableHTML = null;
    
    public NotificationService()
    {
        try
        {
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
        }
        catch(Exception ex)
        {
            System.out.println("Error: unable to load driver class!");
            ex.printStackTrace();
            System.exit(1);
        }
        
        Properties config = new Properties();
        try
        {
            config.load(this.getClass().getResourceAsStream("tracker.properties"));
            try
            {
            	if(config.containsKey("notification.interval.minute"))
                    tracker_interval = Integer.parseInt(config.getProperty("notification.interval.minute"));
                
                if(config.containsKey("mail.host"))
                    mail_host = config.getProperty("mail.host");
                
                if(config.containsKey("mail.port"))
                    mail_port = config.getProperty("mail.port");
                
                if(config.containsKey("smtp.username"))
                    smtp_username = config.getProperty("smtp.username");
                
                if(config.containsKey("smtp.password"))
                    smtp_password = config.getProperty("smtp.password");
                
                if(config.containsKey("db.url"))
                    URL = config.getProperty("db.url");
                
                if(config.containsKey("db.user"))
                    USER = config.getProperty("db.user");
                
                if(config.containsKey("db.pass"))
                    PASS = config.getProperty("db.pass");
            }
            catch(Exception ex)
            {
                logger.info("Tracker interval in wrong format. " + ex.getMessage());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        task = new TimerTask()
        {
            @SuppressWarnings("unchecked")
			@Override
            public void run()
            {
            	SendAlertForNonAlerted();
            	
            	EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                EntityManager em = factory.createEntityManager();
            	
                logger.info("Starting notification check from database data...");
                
                Vector<Partner> partners = new Vector<Partner>();
                Vector<Region> regions = new Vector<Region>();
                try
                {
                	Query q = em.createQuery("Select e from Partner e order by e.name");
                	partners = (Vector<Partner>)q.getResultList();
                }
                catch (Exception ig){}
                for(Partner p : partners)
                {
	                try
	                {
	                    Query q = em.createQuery("Select e from Region e where e.partner=:partner order by e.name");
	                    q.setParameter("partner", p);
	                    regions = (Vector<Region>) q.getResultList();
	                } catch(Exception ex){}
	                if(regions != null && regions.size() > 0)
	                {
	                    for(Region e : regions)
	                    {
	                        if(isThereException(e))
	                        {
	                            sendMailNotification(e);
	                        }
	                    }
	                }
                }
            }
        };
        
        taskT1 = new TimerTask()
        {
            @SuppressWarnings("unchecked")
			@Override
            public void run()
            {
                logger.info("Starting notification check from database data...");
                
                EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                EntityManager em = factory.createEntityManager();
                
                Vector<Partner> partners = new Vector<Partner>();
                Vector<Region> regions = new Vector<Region>();
                try
                {
                	Query q = em.createQuery("Select e from Partner e order by e.name");
                	partners = (Vector<Partner>)q.getResultList();
                }
                catch (Exception ig){}
                for(Partner p : partners)
                {
	                try
	                {
	                    Query q = em.createQuery("Select e from Region e where e.partner=:partner order by e.name");
	                    q.setParameter("partner", p);
	                    regions = (Vector<Region>) q.getResultList();
	                } catch(Exception ex){}
	                logger.info("Regions size: " + regions.size());
	                if(regions != null && regions.size() > 0)
	                {
	                    for(Region e : regions)
	                    {
	                        boolean exceptionFound = isThereT1Exception(e);
	                        if(exceptionFound)
	                        {
	                            logger.info("Exception found for: " + e.getName());
	                            sendMailNotificationT1(e);
	                        }
	                        else
	                            logger.info("No exception found for: " + e.getName());
	                    }
	                }
                }
            }
        };
        
        timer = new Timer();
        timerT1 = new Timer();
    }
    
    @SuppressWarnings("unchecked")
	private void SendAlertForNonAlerted()
    {
    	try
        {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            EntityManager em = factory.createEntityManager();
            EntityTransaction utx = null;
            
            Vector<BankRecord> list = new Vector<BankRecord>();
            
            utx = em.getTransaction();
            utx.begin();
            
            try
            {
	    		Query q = em.createQuery("Select e from BankRecord e where e.vehicle != null and e.alerted=:alerted");
	    		q.setParameter("alerted", false);
	    		Object obj = q.getResultList();
	    		if(obj != null)
	    			list = (Vector<BankRecord>) obj;
            }
            catch(Exception ex)
            {
            	ex.printStackTrace();
            }
            if(list != null && list.size()>0)
            {
            	try
            	{
	            	for(BankRecord e : list)
	            	{
	            		CardBalanceNotification setting = null;
	            		try
	                    {
	            			Query q = em.createQuery("Select e from CardBalanceNotification e where e.region = :region");
	                        q.setParameter("region", e.getVehicle().getRegion());
	                        @SuppressWarnings("rawtypes")
							List sObjs = q.getResultList();
	                        if(sObjs != null && sObjs.size() > 0)
	                        {
	                            for(int i=0; i<sObjs.size(); i++)
	                                setting = (CardBalanceNotification)sObjs.get(i);
	                        }
	                    }
	                    catch(Exception ex)
	                    {
	                    	ex.printStackTrace();
	                    }
	            		if(setting != null)
	                    {
	                        if(setting.getTransactionAlertEmail() != null && setting.getTransactionAlertEmail().trim().length() > 0)
	                        {
	                            // TODO: Send transaction alert here
	                            String email = setting.getTransactionAlertEmail();
	                            //System.out.println("emails: " + email);
	                            String[] recipients = email.split(",");
	                            
	                            double unitP = 1;
	                            if(e.getVehicle().getFuelType().equalsIgnoreCase("PETROL"))
	                            	unitP = e.getVehicle().getPartner().getPetrolUnitPrice();
	                            else
	                            	unitP = e.getVehicle().getPartner().getDisealUnitPrice();
	                            
	                            double quantity = e.getTranAmt()/unitP;
	                            
	                            String message = "<html><body><p><strong>Hello</strong></p><p>This is to notify you of a refuel transaction event on the fuel card platform for vehicle: " + (e.getVehicle() != null ? e.getVehicle().getRegNumber() : "N/A") + ", amount: " + NumberFormat.getInstance().format(e.getTranAmt()) + ", Litres: " + NumberFormat.getInstance().format(quantity) + ", Time: " + e.getTranTimeStr() + ". Please go to the platform to review the reports!</p><p>Regards<br/><strong>Fuel Card Platform</strong></p></body></html>";
	                            
	                            if(setting.getAlertMobileNumbers() != null && setting.getAlertMobileNumbers().trim().length() > 0)
                                {
                                    String sms_message = "Refuel event for vehicle: " + (e.getVehicle() != null ? e.getVehicle().getRegNumber() : "N/A") + ", Amount: " + NumberFormat.getInstance().format(e.getTranAmt()) + ", Litres: " + NumberFormat.getInstance().format(quantity) + ", Time: " + e.getTranTimeStr() + ".";
                                    SMSGateway.sendSMS("Fuelcard", setting.getAlertMobileNumbers(), sms_message);
                                }
	                            
	                            postMail(recipients, "Transaction Alert", message, getSmtp_username());
	                            e.setAlerted(true);
	                            em.merge(e);
	                            em.flush();
	                        }
	                        else
	                        {
	                        	System.out.println("No Transaction Email Set");
	                        }
	                    }
	            		else
	            		{
	            			System.out.println("Setting is null");
	            		}
	            	}
	            	utx.commit();
            	}
            	catch(Exception ex)
            	{
            		try
                    {
                        if(utx != null && utx.isActive())
                        {
                            utx.rollback();
                            logger.info("Rolled back database update for transactions not alerted");
                        }
                    }
                    catch(Exception ig)
                    {}
                    logger.log(Level.SEVERE, "Persist failed for update or transactions not alerted. " + ex);
            	}
            }
            else
            {
            	System.out.println("No Un-Alerted Transactions found!");
            }
        }
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    @SuppressWarnings("unchecked")
	private void sendMailNotification(Region region)
    {
    	try
        {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            EntityManager em = factory.createEntityManager();
            
            CardBalanceNotification e = null;
            Vector<CardBalanceNotification> list = new Vector<CardBalanceNotification>();
            
            try
            {
	    		Query q = em.createQuery("Select e from CardBalanceNotification e where e.region=:region");
	    		q.setParameter("region", region);
	    		Object obj = q.getResultList();
	    		if(obj != null)
	    			list = (Vector<CardBalanceNotification>) obj;
	    		e = list.get(0);
            }
            catch(Exception ex)
            {
            	logger.log(Level.SEVERE, "Retieve failed for card notification balances. " + ex);
            }
            finally
            {
                try {em.close();}finally{}
            }
            
            if(e != null)
            {
                String email = e.getExpectionAlertEmail();
                String[] recipients = email.split(",");
                
                String message = "<html><body><p><strong>Hello</strong></p><p>This is to notify you of an exception event.</p><br/><br/>" + exceptionEventTableHTML + "<br/><p> Please go to the platform to review the reports!</p><p>Regards<br/><strong>Fuel Card Platform</strong></p></body></html>"; // <br/><strong>Fuel Card Platform</strong>
                //String message = "<html><body><p><strong>Hello</strong></p><p>This is to notify you of an exception event on the fuel card platform. Please go to the platform to review the reports!</p><p>Regards<br/><strong>Fuel Card Platform</strong></p></body></html>";
                postMail(recipients, "Exception Notification", message, smtp_username);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    @SuppressWarnings({ "unchecked", "deprecation" })
	private void sendMailNotificationT1(Region region)
    {
        try
        {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            EntityManager em = factory.createEntityManager();
            
            CardBalanceNotification e = null;
            Vector<CardBalanceNotification> list = new Vector<CardBalanceNotification>();
            
            try
            {
    		Query q = em.createQuery("Select e from CardBalanceNotification e where e.region=:region");
                q.setParameter("region", region);
    		Object obj = q.getResultList();
    		if(obj != null)
                    list = (Vector<CardBalanceNotification>) obj;
                e = list.get(0);
            }
            catch(Exception ex)
            {
    		logger.log(Level.SEVERE, "Retieve failed for card notification balances. " + ex);
            }
            finally
            {
                try {em.close();}finally{}
            }
            
            if(e != null)
            {
                String email = e.getExpectionAlertEmail();
                String[] recipients = email.split(",");
                
                logger.info("Setting found for region: " + region.getName() + ", Email(s)" + email);
                
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, -1);
                
                String message = "<html><body><p><strong>Hello</strong></p><p>This is a summary exception report for: " + c.getTime().toLocaleString() + ": -</p><br/><br/>" + exceptionT1EventTableHTML + "<br/><p> Please go to the platform to review the reports!</p><p>Regards</p></body></html>";
                postMail(recipients, "Exception Report for " + region.getName() + " Region - " + c.getTime().toLocaleString(), message, smtp_username);
            }
            else
                logger.info("Setting not found for region: " + region.getName());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    @SuppressWarnings({ "unchecked", "deprecation", "unused" })
	private boolean isThereException(Region region)
    {
    	exceptionEventTableHTML = null;
    	
    	boolean ret = false;
        Connection conn = null;
        PreparedStatement statement;
        ResultSet result;
        
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        
        try
        {
            conn = DriverManager.getConnection(URL, USER, PASS);
            
            Vector<BankRecord> banklist = new Vector<BankRecord>();
            
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.MINUTE, -tracker_interval);
            
            Date d1 = c1.getTime(), d2 = new Date();
            d2.setHours(23);
            d2.setMinutes(59);
            d2.setSeconds(59);
            
            String html = "<table border=1><tr><th>Reg. Number</th><th>Exception</th><th>Date/Time</th><th>Details</th></tr>";
            if(region != null && region.getId() != null)
            {
                try
                {
                    Query q = em.createQuery("Select e from BankRecord e where e.vehicle.region=:region and e.tranStatus=:tranStatus and (e.crt_dt between :stdt and :eddt) order by e.tranTime");
                    q.setParameter("region", region);
                    q.setParameter("tranStatus", "Processed");
                    q.setParameter("stdt", d1);
                    q.setParameter("eddt", d2);

                    banklist = (Vector<BankRecord>)q.getResultList();
                }
                catch(Exception ex)
                {
                    logger.log(Level.SEVERE, "Retieve failed for bank record: " + ex);
                }

                for(BankRecord br : banklist)
                {
                	if(br.getVehicle() != null)
                    {
                		BigDecimal amt = new BigDecimal(br.getTranAmt());
                        amt = amt.setScale(2, RoundingMode.HALF_UP);
                        
	                    // 120 minutes
	                    statement = conn.prepareStatement("SELECT * FROM trackerrecord_tb " + 
	                            "where VEHICLE_ID = ? and ABS(TIMESTAMPDIFF(minute, trantime, ?)) <= 120 ");
	                    statement.setLong(1, br.getVehicle().getId());
	                    statement.setTimestamp(2, Timestamp.valueOf(sdformat.format(br.getTranTime())));// br.getTranTime().getTime()
	
	                    result = statement.executeQuery();
	                    double trackerQuantity = 0;
	                    boolean found = false;
	                    while(result.next())
	                    {
	                        found = true;
	                        trackerQuantity = result.getDouble("quantity");
	                    }
	                    
	                    if(found)
	                    {
	                    	if(br.getVehicle().getPartner() != null)
                            {
                                double unitP = 1;
                                if(br.getVehicle().getFuelType().equalsIgnoreCase("PETROL"))
                                    unitP = br.getVehicle().getPartner().getPetrolUnitPrice();
                                else
                                    unitP = br.getVehicle().getPartner().getDisealUnitPrice();
                                
                                if(unitP > 0)
                                {
	                                double estLitres = br.getTranAmt()/unitP;
	                                double tenPercent = estLitres * 0.1;
	                                try{
	                                BigDecimal trackerQuantityBD = new BigDecimal(trackerQuantity);
	                                trackerQuantityBD = trackerQuantityBD.setScale(2, RoundingMode.HALF_UP);
	                                trackerQuantity = trackerQuantityBD.doubleValue();
	                                } catch(Exception ex){}
	                                try{
	                                BigDecimal estLitresBD = new BigDecimal(estLitres);
	                                estLitresBD = estLitresBD.setScale(2, RoundingMode.HALF_UP);
	                                estLitres = estLitresBD.doubleValue();
	                                } catch(Exception ex){}
	                                
	                                if(Math.abs(trackerQuantity-estLitres) <= tenPercent); // 10%
	                                else
	                                {
	                                    html += "<tr><td>" + br.getVehicle().getRegNumber() + "</td><td>Transaction amount not inline with purchased litres</td><td>" + br.getTranTimeStr() + "</td><td>Transaction Amount: " + NumberFormat.getInstance().format(amt.doubleValue()) + ", Unit Price: " + NumberFormat.getInstance().format(unitP) + ", Expected Litres: " + NumberFormat.getInstance().format(estLitres) + ", Found Litres: " + NumberFormat.getInstance().format(trackerQuantity) + ", Diff: " + NumberFormat.getInstance().format(estLitres-trackerQuantity) + "</td></tr>";
	                                }
                                }
                            }
	                    }
	                    
	                    /*if(!found)
	                    {
	                        ret = true;
	                        
	                        html += "<tr><td>" + br.getVehicle().getRegNumber() + "</td><td>Fuel card used without refuel</td><td>" + br.getTranTimeStr() + "</td><td>Card Pan: " + br.getCardPan() + ", Amount: " + NumberFormat.getInstance().format(amt.doubleValue()) + "</td></tr>";
	                        // BANK-WITHOUT-TRACKER
	                    }*/
                    }
                }
                
                Vector<TrackerRecord> trackerlist = new Vector<TrackerRecord>();

                try
                {
                    Query q = em.createQuery("Select e from TrackerRecord e where e.vehicle.region=:region and (e.crt_dt between :stdt and :eddt) order by e.tranTime");
                    q.setParameter("region", region);
                    q.setParameter("stdt", d1);
                    q.setParameter("eddt", d2);

                    trackerlist = (Vector<TrackerRecord>)q.getResultList();
                }
                catch(Exception ex)
                {
                    logger.log(Level.SEVERE, "Retieve failed for bank record: " + ex);
                }

                for(TrackerRecord tr : trackerlist)
                {
                	if(tr.getVehicle() != null)
                    {
	                    // 120 minutes
	                    statement = conn.prepareStatement("SELECT * FROM bankrecord_tb " +
	                            "where VEHICLE_ID = ? and ABS(TIMESTAMPDIFF(minute, trantime, ?)) <= 120 ");
	                    statement.setLong(1, tr.getVehicle().getId());
	                    statement.setTimestamp(2, Timestamp.valueOf(sdformat.format(tr.getTranTime())));// tr.getTranTime().getTime()
	
	                    result = statement.executeQuery();
	                    boolean found = false;
	                    while(result.next())
	                        found = true;
	                    
	                    /*if(!found)
	                    {
	                        double litres = (tr.getFinalFuelLvl()-tr.getInitFuelLvl());
	                        try{
	                        BigDecimal big_litres = new BigDecimal(litres);
	                        big_litres = big_litres.setScale(2, RoundingMode.HALF_UP);
	                        litres = big_litres.doubleValue();
	                        } catch(Exception ex){}
	                        
	                        if(litres > 10)
	                        {
	                            ret = true;
	                            html += "<tr><td>" + tr.getVehicle().getRegNumber() + "</td><td>Vehicle refueled without fuel card</td><td>" + tr.getTranTime() + "</td><td>Litres: " + NumberFormat.getInstance().format(litres) + "</td></tr>";
	                        }
	                        // TRACKER-WITHOUT-BANK
	                    }*/
	
	                    BigDecimal fl = new BigDecimal(tr.getFinalFuelLvl());
	                    fl = fl.setScale(2, RoundingMode.HALF_UP);
	                    if(fl.doubleValue() > tr.getVehicle().getCalibratedCapacity())
	                    {
	                        ret = true;
	                        html += "<tr><td>" + tr.getVehicle().getRegNumber() + "</td><td>Fuel level above calibrated fuel capacity</td><td>" + tr.getTranTime() + "</td><td>Final fuel level: " + NumberFormat.getInstance().format(fl.doubleValue()) + ", Calibrated capacity: " + tr.getVehicle().getCalibratedCapacity() + "</td></tr>";
	                    }
                    }
                }
            }
            html += "</table>";
            
            if(ret)
                exceptionEventTableHTML = html;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try{conn.close();}catch(Exception e){}
        }
        
        return ret;
    }
    
    @SuppressWarnings("unchecked")
	private boolean isThereT1Exception(Region region)
    {
        exceptionT1EventTableHTML = null;
        
        boolean ret = false;
        Connection conn = null;
        Statement stat;
        ResultSet result;
        
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try
        {
            conn = DriverManager.getConnection(URL, USER, PASS);
            
            Vector<BankRecord> banklist = new Vector<BankRecord>();
            
            Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
            c1.add(Calendar.DATE, -1);
            c1.set(Calendar.HOUR_OF_DAY, c1.getMinimum(Calendar.HOUR_OF_DAY));
            c1.set(Calendar.MINUTE, c1.getMinimum(Calendar.MINUTE));
            c1.set(Calendar.SECOND, c1.getMinimum(Calendar.SECOND));
            c1.set(Calendar.MILLISECOND, c1.getMinimum(Calendar.MILLISECOND));
            
            c2.add(Calendar.DATE, -1);
            c2.set(Calendar.HOUR_OF_DAY, c2.getMaximum(Calendar.HOUR_OF_DAY));
            c2.set(Calendar.MINUTE, c2.getMaximum(Calendar.MINUTE));
            c2.set(Calendar.SECOND, c2.getMaximum(Calendar.SECOND));
            c2.set(Calendar.MILLISECOND, c2.getMaximum(Calendar.MILLISECOND));
            
            Date d1 = c1.getTime(), d2 = c2.getTime();
            
            String html = "<table border=1><tr><th>Reg. Number</th><th>Exception</th><th>Date/Time</th><th>Details</th></tr>";
            if(region != null && region.getId() != null)
            {
                try
                {
                    Query q = em.createQuery("Select e from BankRecord e where e.vehicle.region=:region and e.tranStatus=:tranStatus and (e.crt_dt between :stdt and :eddt) order by e.tranTime");
                    q.setParameter("region", region);
                    q.setParameter("tranStatus", "Processed");
                    q.setParameter("stdt", d1);
                    q.setParameter("eddt", d2);

                    banklist = (Vector<BankRecord>)q.getResultList();
                }
                catch(Exception ex)
                {
                    logger.log(Level.SEVERE, "Retieve failed for bank record: " + ex);
                }

                for(BankRecord br : banklist)
                {
                    // 14 minutes
                    if(br.getVehicle() != null)
                    {
                        BigDecimal amt = new BigDecimal(br.getTranAmt());
                        amt = amt.setScale(2, RoundingMode.HALF_UP);
                        
                        String query = "SELECT * FROM trackerrecord_tb " + 
                                "where VEHICLE_ID = " + br.getVehicle().getId().longValue() +" and "
                                + "ABS(TIMESTAMPDIFF(minute, trantime, '" + sdformat.format(br.getTranTime()) + "')) <= 120";
                        System.out.println("query: " + query);
                        stat = conn.createStatement();
                        
                        result = stat.executeQuery(query);
                        boolean found = false;
                        while(result.next())
                        {
                            found = true;
                        }
                        
                        if(!found)
                        {
                            ret = true;
                            html += "<tr><td>" + br.getVehicle().getRegNumber() + "</td><td>Fuel card used without refuel</td><td>" + br.getTranTimeStr() + "</td><td>Card Pan: " + br.getCardPan() + ", Amount: " + NumberFormat.getInstance().format(amt.doubleValue()) + "</td></tr>";
                            // BANK-WITHOUT-TRACKER
                        }
                    }
                }
                
                Vector<TrackerRecord> trackerlist = new Vector<TrackerRecord>();

                try
                {
                    Query q = em.createQuery("Select e from TrackerRecord e where e.vehicle.region=:region and (e.crt_dt between :stdt and :eddt) order by e.tranTime");
                    q.setParameter("region", region);
                    q.setParameter("stdt", d1);
                    q.setParameter("eddt", d2);

                    trackerlist = (Vector<TrackerRecord>)q.getResultList();
                }
                catch(Exception ex)
                {
                    logger.log(Level.SEVERE, "Retieve failed for tracker record: " + ex);
                }

                for(TrackerRecord tr : trackerlist)
                {
                    // 14 minutes
                    if(tr.getVehicle() != null)
                    {
                        String query = "SELECT * FROM bankrecord_tb " + 
                                "where VEHICLE_ID = " + tr.getVehicle().getId().longValue() +" and "
                                + "ABS(TIMESTAMPDIFF(minute, trantime, '" + sdformat.format(tr.getTranTime()) + "')) <= 120";
                        System.out.println("query: " + query);
                        stat = conn.createStatement();
                        result = stat.executeQuery(query);
                        
                        boolean found = false;
                        while(result.next())
                            found = true;
                        if(!found)
                        {
                            double litres = (tr.getFinalFuelLvl()-tr.getInitFuelLvl());
                            try{
                            BigDecimal big_litres = new BigDecimal(litres);
                            big_litres = big_litres.setScale(2, RoundingMode.HALF_UP);
                            litres = big_litres.doubleValue();
                            } catch(Exception ex){}
                            
                            if(litres > 10)
                            {
                                ret = true;
                                html += "<tr><td>" + tr.getVehicle().getRegNumber() + "</td><td>Vehicle refueled without fuel card</td><td>" + tr.getTranTime() + "</td><td>Litres: " + NumberFormat.getInstance().format(litres) + "</td></tr>";
                            }
                            // TRACKER-WITHOUT-BANK
                        }
                    }
                }
            }
            html += "</table>";
            //System.out.println("Exception table: " + html);
            
            if(ret)
                exceptionT1EventTableHTML = html;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try{conn.close();}catch(Exception e){}
        }
        
        return ret;
    }
    
    public void postMail(String recipients[], String subject, String message , String from) throws MessagingException
    {
        boolean debug = false;
        
        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", mail_host);
        props.put("mail.smtp.port", mail_port);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        
        Authenticator auth = new SMTPAuthenticator(smtp_username, smtp_password);
        // create some properties and get the default Session
        Session session = Session.getDefaultInstance(props, auth);
        session.setDebug(debug);
        
        // create a message
        Message msg = new MimeMessage(session);
        
        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);
        
        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for(int i = 0; i < recipients.length; i++)
        {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);
        
        // Optional : You can also set your custom headers in the Email if you Want
        //msg.addHeader("MyHeaderName", "myHeaderValue");
        
        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/html");
        
        Transport.send(msg);
        System.out.println("Mail Sent");
    }
    
    /**
     * SimpleAuthenticator is used to do simple authentication
     * when the SMTP server requires it.
     */
    private class SMTPAuthenticator extends Authenticator
    {
        private String SMTP_AUTH_USER;
        private String SMTP_AUTH_PWD;
        
        public SMTPAuthenticator(String SMTP_AUTH_USER, String SMTP_AUTH_PWD)
        {
            this.SMTP_AUTH_USER = SMTP_AUTH_USER;
            this.SMTP_AUTH_PWD = SMTP_AUTH_PWD;
        }
        
        public PasswordAuthentication getPasswordAuthentication()
        {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }
    
    @Override
    public void run()
    {
        timer.scheduleAtFixedRate(task, new Date(), 1000 * 60 * tracker_interval); // 5 minutes 
        
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        timerT1.scheduleAtFixedRate(taskT1, c.getTime(), 1000*60*60*24); // 24 hours
        
        logger.info("Download timer tbread: Started");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        NotificationService nService = new NotificationService();
        nService.start();
        NotificationService.logger.info("Notification Thread Started.");
    }

	public String getSmtp_username() {
		return smtp_username;
	}
    
}
