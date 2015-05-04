
package com.dexter.fuelcard;

import com.dexter.fuelcard.model.BankRecord;
import com.dexter.fuelcard.model.Car;
import com.dexter.fuelcard.model.CardBalanceNotification;
import com.dexter.fuelcard.model.TrackerRecord;
import com.dexter.fuelcard.util.SMSGateway;
import com.zenithbank.mtn.fleet.ServiceLocator;
import com.zenithbank.mtn.fleet.ServiceSoap;
import com.zenithbank.mtn.fleet.Transaction;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Download service for the Fuel Card.
 * @author oladele
 */
public class DownloadService extends Thread
{
    private static Logger logger = Logger.getLogger("FuelCardDS-DownloadService");
    private Properties config;
    
    static
    {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
        new javax.net.ssl.HostnameVerifier()
        {
            @Override
            public boolean verify(String hostname,
                    javax.net.ssl.SSLSession sslSession)
            {
                /*if (hostname.equals("localhost") || hostname.equals("app-t.ncs.gov.ng")
                        || hostname.equals("app.ncs.gov.ng")) {
                    return true;
                }
                return false;*/
                return true;
            }
        });
    }
    
    private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    
    Timer timer, updateZenIdTimer;
    TimerTask task, updateZenIdTask;
    
    BigDecimal max = new BigDecimal(0);
    private int tracker_interval = 5; // interval in minutes
    
    private boolean startNotificationService = false;
    
    private boolean updateZenithTranId = false;
    private long zenithTranId = 0L;
    
    private String userName = "admin1", userHost = "v5", userOrg = "zon_sattrak_telematics", password = "sattrak123";
    
    public DownloadService()
    {
        config = new Properties();
        try
        {
        	config.load(this.getClass().getResourceAsStream("tracker.properties"));
            try
            {
            	if(config.containsKey("tracker.interval.minute"))
            		tracker_interval = Integer.parseInt(config.getProperty("tracker.interval.minute"));
            }
            catch(Exception ex)
            {
            	System.out.println("Tracker interval in wrong format. " + ex.getMessage());
            }
            
            try
            {
            	if(config.containsKey("start.notificationservice"))
                    startNotificationService = Boolean.parseBoolean(config.getProperty("start.notificationservice"));
            }
            catch(Exception ex)
            {
            	System.out.println("notification start config in wrong format. " + ex.getMessage());
            }
            
            try
            {
            	if(config.containsKey("update.zentranid"))
            		updateZenithTranId = Boolean.parseBoolean(config.getProperty("update.zentranid"));
            }
            catch(Exception ex)
            {
            	System.out.println("update zenith tran id config in wrong format. " + ex.getMessage());
            }
            
            /*try
            {
            	if(config.containsKey("last.gmttime"))
            	{
            		lastGMTTime = config.getProperty("last.gmttime");
            	}
            }
            catch(Exception ex)
            {
            	System.out.println("last GTM Time in wrong format. " + ex.getMessage());
            }*/
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        if(updateZenithTranId)
        {
        	try
            {
            	if(config.containsKey("zen.tranid"))
            		zenithTranId = Long.parseLong(config.getProperty("zen.tranid"));
            }
            catch(Exception ex)
            {
            	System.out.println("zenith tran id config in wrong format. " + ex.getMessage());
            }
        	if(zenithTranId > 0L)
        	{
	        	try
	        	{
	        		ServiceLocator serviceLocator = new ServiceLocator();
	        		ServiceSoap zenServiceSoap = serviceLocator.getServiceSoap(new URL("https://webservices.zenithbank.com/MtnFleetService/Service.asmx"));
	        		boolean ret = zenServiceSoap.updateMaxTransaction(new BigDecimal(zenithTranId));
	                logger.info("Updated zenith web services for the max transaction id " + zenithTranId + ". Return: " + ret);
	        	}
	        	catch(Exception ex)
	        	{
	        		ex.printStackTrace();
	        	}
        	}
        }
        
        updateZenIdTask = new TimerTask()
        {
        	@Override
        	public void run()
        	{
        		try
	        	{
	        		ServiceLocator serviceLocator = new ServiceLocator();
	        		ServiceSoap zenServiceSoap = serviceLocator.getServiceSoap(new URL("https://webservices.zenithbank.com/MtnFleetService/Service.asmx"));
	        		boolean ret = zenServiceSoap.updateMaxTransaction(max);
	        		logger.info("Updated zenith web services for the max downloaded transaction id " + max.longValue() + ". Return: " + ret);
	        	}
	        	catch(Exception ex)
	        	{
	        		ex.printStackTrace();
	        	}
        	}
        };
        
        task = new TimerTask()
        {
            @SuppressWarnings("deprecation")
			@Override
            public void run()
            {
            	System.out.println("Starting tracker info download from the tracker web service");
            	
            	String eventRequestedProps = "event_type,unit_id,event_time,event_name,event_text,event_value,h_address,h_distance,unit_name";
            	
            	/*String uri = "http://galoolitools.dnsalias.com/galooliDevKitService/galooliDevKitService.svc/json/GetRealTimeData?";
            	uri += "userName="+userName+"&";
            	uri += "userHost="+userHost+"&";
            	uri += "userOrg="+userOrg+"&";
            	uri += "password="+password+"&";
            	uri += "requestedPropertiesStr="+requestedProps+"&";
            	uri += "lastGMTUpdateTime=" + URLEncoder.encode(lastGMTTime);*/            
            	
            	String uri = "http://galoolitools.dnsalias.com/galooliDevKitService/galooliDevKitService.svc/json/GetEventsInformation?";
            	uri += "userName="+userName+"&";
            	uri += "userHost="+userHost+"&";
            	uri += "userOrg="+userOrg+"&";
            	uri += "password="+password+"&";
            	uri += "requestedUnits=any&";
            	uri += "requestedEvents=6001&";//any
            	uri += "requestedPropertiesStr="+eventRequestedProps+"&";
            	
            	Calendar c = Calendar.getInstance();
            	c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startTimeStr = sdf.format(c.getTime());
                c.set(Calendar.HOUR_OF_DAY, 23);
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);
                String endTimeStr = sdf.format(c.getTime());
            	uri += "startTime=" + URLEncoder.encode(startTimeStr) + "&";
            	uri += "endTime=" + URLEncoder.encode(endTimeStr);
            	
            	//GalooliDevKitService zonLocator = new GalooliDevKitService(); // new URL("http://galoolitools.dnsalias.com/galooliDevKitService/galooliDevKitService.svc")
                try
                {
                	URL url = new URL(uri);
                	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                	connection.setRequestMethod("GET");
                	connection.setRequestProperty("Accept", "application/json");
                	InputStream is = connection.getInputStream();
                	
                	String resultString = null;
                	try {
                		resultString = getStringFromInputStream(is);
                		//System.out.println(resultString);
                	} catch(Exception ex){
                		ex.printStackTrace();
                	}
                	
                	JSONObject resultJSON = null;
                	if(resultString != null)
                	{
	                	try {
	                		resultJSON = new JSONObject(resultString);
	                	} catch(Exception ex){
	                		ex.printStackTrace();
	                	}
                	}
                	
                    /*GregorianCalendar c = new GregorianCalendar();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    GregorianCalendar c2 = new GregorianCalendar();
                    c2.set(Calendar.HOUR_OF_DAY, 23);
                    c2.set(Calendar.MINUTE, 59);
                    c2.set(Calendar.SECOND, 59);*/
                    //System.out.println("Date: " + c.getTime());
                    
                    //IDevKitService zonService = zonLocator.getBasicHttpBindingIDevKitService();
                    
                	if(resultJSON != null)
                	{
                		JSONObject commonResultObj = resultJSON.getJSONObject("CommonResult");
                		//String maxGMTUpdateTime = resultJSON.getString("MaxGmtUpdateTime");
                		
                		int resultCode = commonResultObj.getInt("ResultCode");
                		System.out.println("result code: " + resultCode);
	                    /*ResultRealTime result = zonService.getRealTimeData(userName, userHost, userOrg, password, requestedProps, lastGMTTime);
	                    if(result == null || result.getCommonResult() == null)
	                        System.out.println("Tracker result object or list object is null");
	                    if(result != null && result.getCommonResult() != null && result.getCommonResult().getValue() != null)
	                        System.out.println("Tracker Result Size: " + result.getCommonResult().getValue().getDataSet().getValue().getArrayOfstring().size() + ", result code: " + result.getCommonResult().getValue().getResultCode());
	                    if(result != null && result.getCommonResult() != null && result.getCommonResult().getValue().getResultCode() == ResultEResultCode.EXECUTED_OK)
	                    	*/
                		if(resultCode == 0)
	                    {
                			JSONArray dataSet = commonResultObj.getJSONArray("DataSet");
                			
	                    	//List<ArrayOfstring> events = result.getCommonResult().getValue().getDataSet().getValue().getArrayOfstring();
	                        System.out.println("Tracker records count: " + dataSet.length());
	                        if(dataSet.length() > 0)
	                        {
	                            EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	                            EntityManager em = factory.createEntityManager();
	                            EntityTransaction utx = null;
	                            try
	                            {
	                                utx = em.getTransaction();
	                                utx.begin();
	                                
	                                //for(ArrayOfstring e : events)
	                                for(int i=0; i<dataSet.length(); i++)
	                                {
	                                	JSONArray e_values = dataSet.getJSONArray(i);
	                                	
	                                	//List<String> e_values = e.getString();
	                                	
	                                	// event_type,unit_id,event_time,event_name,event_text,event_value,h_address,h_distance,unit_name
	                                	String event_type = e_values.getString(0);
	                                	String unit_id = e_values.getString(1);
	                                	String event_time = e_values.getString(2);event_time = event_time.replaceAll("'", "");
	                                	String event_name = e_values.getString(3);event_name = event_name.replaceAll("'", "");
	                                	String event_text = e_values.getString(4);event_text = event_text.replaceAll("'", ""); // initial value
	                                	String event_value = e_values.getString(5); // current value
	                                	String h_address = e_values.getString(6);h_address = h_address.replaceAll("'", "");
	                                	String h_distance = e_values.getString(7);
	                                	String unit_name = e_values.getString(8);unit_name = unit_name.replaceAll("'", "");
	                                	
	                                	/*for(int a=0; a<e_values.length(); a++)
	                                		System.out.println(e_values.getString(a));*/
	                                	
	                                	if(event_type.equalsIgnoreCase("6001"))
	                                	{
	                                		TrackerRecord tr = new TrackerRecord();
		                                    tr.setCrt_dt(new Date());
		                                    
		                                    try
		                                    {
			                                    Query q = em.createQuery("Select e from Car e where e.zonControlId = :zonControlId");
			                                    q.setParameter("zonControlId", Integer.parseInt(unit_id));
			                                    
			                                    Object vObj = q.getSingleResult();
			                                    if(vObj != null)
			                                    {
			                                        Car vehicle = (Car)vObj;
			                                        tr.setVehicle(vehicle);
			                                    }
		                                    }
		                                    catch(Exception ex){}
		                                    
		                                    tr.setUnitID(Integer.parseInt(unit_id)); // vehicle identifier
		                                    tr.setTranType(event_name);
		                                    
		                                    // the tran time coming from the tracker is in GTM, so we add 1 hour to it to get the GTM+1 value, which is our time zone
		                                    Calendar tranTimeCal = Calendar.getInstance();
		                                    tranTimeCal.setTime(sdf.parse(event_time));
		                                    tranTimeCal.add(Calendar.HOUR, 1);
											
		                                    tr.setTranTime(tranTimeCal.getTime());
		                                    tr.setUnitName(unit_name);
		                                    tr.setQuantity(Double.parseDouble(event_value));
		                                    tr.setInitFuelLvl(Double.parseDouble(event_text));
		                                    tr.setFinalFuelLvl(tr.getQuantity() + tr.getInitFuelLvl());
		                                    tr.setOdometer(Double.parseDouble(h_distance));
		                                    tr.setAddress(h_address);
											
		                                    boolean exists = false;
		                                    // check if the current record exists before creating it, only do this for first time runs
		                                    Query checkQ = em.createQuery("Select e from TrackerRecord e where e.vehicle = :vehicle and e.tranTime = :tranTime");
		                                    checkQ.setParameter("vehicle", tr.getVehicle());
		                                    checkQ.setParameter("tranTime", tr.getTranTime());
		                                    try
		                                    {
		                                        @SuppressWarnings("rawtypes")
		                                        List checkObj = checkQ.getResultList();
		                                        if(checkObj != null && checkObj.size() > 0)
		                                        {
		                                            exists = true;
		                                        }
		                                    }
		                                    catch(Exception ex)
		                                    {
		                                        ex.printStackTrace();
		                                    }
											
		                                    if(!exists)
		                                    {
		                                        em.persist(tr);
		                                        try
		                                        {
		                                            em.flush();
		                                        }
		                                        catch(Exception ex)
		                                        {
		                                            ex.printStackTrace();
		                                        }
		                                    }
	                                	}
	                                	
	                                    /*if(e_values.get(0).equalsIgnoreCase("Fuel_Refueled"))
	                                    {
		                                    TrackerRecord tr = new TrackerRecord();
		                                    tr.setCrt_dt(new Date());
		                                    
		                                    try
		                                    {
			                                    Query q = em.createQuery("Select e from Car e where e.zonControlId = :zonControlId");
			                                    q.setParameter("zonControlId", e.getUnitID());
			                                    
			                                    Object vObj = q.getSingleResult();
			                                    if(vObj != null)
			                                    {
			                                        Car vehicle = (Car)vObj;
			                                        tr.setVehicle(vehicle);
			                                    }
		                                    }
		                                    catch(Exception ex){}
		                                    
		                                    tr.setUnitID(e.getUnitID()); // vehicle identifier
		                                    tr.setTranType(e.getEventType().getValue());
		                                    
		                                    // the tran time coming from the tracker is in GTM, so we add 1 hour to it to get the GTM+1 value, which is our time zone
		                                    Calendar tranTimeCal = Calendar.getInstance();
		                                    tranTimeCal.setTime(e.getEventTime().getTime());
		                                    tranTimeCal.add(Calendar.HOUR, 1);
											
		                                    tr.setTranTime(tranTimeCal.getTime());
		                                    tr.setUnitName(e.getEventName());
		                                    tr.setCost(e.getEventValue());
		                                    tr.setInitFuelLvl(e.getEventInitValue());
		                                    tr.setFinalFuelLvl(e.getEventFinalValue());
		                                    tr.setOdometer(e.getDistance());
		                                    tr.setAddress(e.getAddress());
											
		                                    boolean exists = false;
		                                    // check if the current record exists before creating it, only do this for first time runs
		                                    Query checkQ = em.createQuery("Select e from TrackerRecord e where e.vehicle = :vehicle and e.tranTime = :tranTime");
		                                    checkQ.setParameter("vehicle", tr.getVehicle());
		                                    checkQ.setParameter("tranTime", tr.getTranTime());
		                                    try
		                                    {
		                                        @SuppressWarnings("rawtypes")
		                                        List checkObj = checkQ.getResultList();
		                                        if(checkObj != null && checkObj.size() > 0)
		                                        {
		                                            exists = true;
		                                        }
		                                    }
		                                    catch(Exception ex)
		                                    {
		                                        ex.printStackTrace();
		                                    }
											
		                                    if(!exists)
		                                    {
		                                        em.persist(tr);
		                                        try
		                                        {
		                                            em.flush();
		                                        }
		                                        catch(Exception ex)
		                                        {
		                                            ex.printStackTrace();
		                                        }
		                                    }
	                                    }*/
	                                }
	                                utx.commit();
	                                System.out.println("Commited database storage transaction for the tracker downloaded events");
	                            }
	                            catch(Exception ex)
	                            {
	                                ex.printStackTrace();
	                                try
	                                {
	                                    if(utx != null && utx.isActive())
	                                    {
	                                        utx.rollback();
	                                        System.out.println("Rolled back database storage transaction for the tracker downloaded events");
	                                    }
	                                }
	                                catch(Exception ig)
	                                {}
	                                logger.log(Level.SEVERE, "Persist failed for galoli event record. " + ex);
	                            }
	                            finally
	                            {
	                                em.close();
	                                System.out.println("Closed database connection.");
	                            }
	                        }
	                    }
	                    else
	                    {
	                    	System.out.println("0 events returned from tracker.");
	                    }
                	}
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

                System.out.println("Starting transactions download from zenith web service.");

                // Download the financial information here by calling the required web service
                ServiceLocator serviceLocator = new ServiceLocator();
                try
                {
                	ServiceSoap zenServiceSoap = serviceLocator.getServiceSoap(new URL("https://webservices.zenithbank.com/MtnFleetService/Service.asmx"));
                    Transaction[] transactions = zenServiceSoap.getCardTrans(true);
                    if(transactions != null)
                    	System.out.println(transactions.length + " transactions downloaded from zenith web service.");
                    if(transactions != null && transactions.length > 0)
                    {
                        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                        EntityManager em = factory.createEntityManager();
                        EntityTransaction utx = null;
                        try
                        {
                            utx = em.getTransaction();
                            utx.begin();
                            System.out.println("Started database storage transaction for the downloaded transactions from zenith web service");
                            for(Transaction t : transactions)
                            {
                            	if(t.getAmount().intValue()<=0)
                                    continue;
                            	
                            	CardBalanceNotification setting = null;
                            	
                            	if(max.doubleValue() < t.getTransID().doubleValue())
                                    max = new BigDecimal(t.getTransID().doubleValue());

                                BankRecord br = new BankRecord();
                                br.setCrt_dt(new Date());
                                br.setTranID(t.getTransID().doubleValue());

                                try
                                {
                                    Query q = em.createQuery("Select e from Car e where e.cardPan = :cardPan");
                                    q.setParameter("cardPan", t.getPan()); // t.getPan().substring(t.getPan().length()-4)

                                    Object vObj = q.getSingleResult();
                                    if(vObj != null)
                                    {
                                        Car vehicle = (Car)vObj;
                                        br.setVehicle(vehicle);
                                        
                                        try
                                        {
                                            q = em.createQuery("Select e from CardBalanceNotification e where e.region = :region");
                                            q.setParameter("region", br.getVehicle().getRegion());
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
                                    }
                                }
                                catch(Exception ex)
                                {}

                                br.setCardAcceptorId(t.getCardAcceptorID());
                                br.setCardAcceptorLoc(t.getCardAcceptorLocation());
                                try{
                                	br.setCardBal(t.getAvailBalance().doubleValue()); // /100.00, this comes in naira
                                } catch(Exception ig){
                                	br.setCardBal(t.getAvailBalance().doubleValue());
                                }
                                br.setCardPan(t.getPan());
                                br.setRetrievalRefNum(t.getRetrievalReference());
                                br.setSchemeOwner(t.getSchemeOwnerName());
                                try{
                                	br.setTranAmt(t.getAmount().doubleValue()/100.00); // divide by 100 because it comes as kobo from zenith bank
                                } catch(Exception ig) {
                                	br.setTranAmt(t.getAmount().doubleValue()); // divide by 100 because it comes as kobo from zenith bank
                                }
                                try
                                {
                                	br.setTranFees(t.getFees().doubleValue()/100.00);
                                } catch(Exception ex){
                                	br.setTranFees(t.getFees().doubleValue());
                                }
                                br.setTranStatus(t.getTransactionStatus());

                                SimpleDateFormat formatter = new SimpleDateFormat("M/d/y h:m:s a"); // EEEE, MMM dd, yyyy
                                Date date = formatter.parse(t.getTransactionDate());
                                br.setTranTime(date);

                                br.setTranTimeStr(t.getTransactionDate());
                                br.setTranType(t.getTransactionType());

                                boolean exists = false;
                                // check if the current record exists before creating it, only do this for first time runs
                                Query checkQ = em.createQuery("Select e from BankRecord e where e.tranID = :tranID");
                                checkQ.setParameter("tranID", br.getTranID());
                                try
                                {
                                    @SuppressWarnings("rawtypes")
                                    List checkObj = checkQ.getResultList();
                                    if(checkObj != null && checkObj.size() > 0)
                                    {
                                        exists = true;
                                    }
                                }
                                catch(Exception ex)
                                {
                                    ex.printStackTrace();
                                }

                                if(!exists)
                                {
                                    //em.persist(t);
                                    em.persist(br);
                                }
                                
                                try
                                {
                                    if(setting != null && !exists)
                                    {
                                    	double unitP = 1;
        	                            if(br.getVehicle().getFuelType().equalsIgnoreCase("PETROL"))
        	                            	unitP = br.getVehicle().getPartner().getPetrolUnitPrice();
        	                            else
        	                            	unitP = br.getVehicle().getPartner().getDisealUnitPrice();
        	                            double quantity = br.getTranAmt()/unitP;
        	                            
        	                            if(setting.getAlertMobileNumbers() != null && setting.getAlertMobileNumbers().trim().length() > 0)
                                        {
                                            String sms_message = "Refuel event for vehicle: " + (br.getVehicle() != null ? br.getVehicle().getRegNumber() : "N/A") + ", Amount: " + NumberFormat.getInstance().format(br.getTranAmt()) + ", Litres: " + NumberFormat.getInstance().format(quantity) + ", Time: " + br.getTranTimeStr() + ".";
                                            SMSGateway.sendSMS("Fuelcard", setting.getAlertMobileNumbers(), sms_message);
                                        }
                                        else
                                        {
                                        	System.out.println("No Transaction Mobile Set");
                                        }
        	                            
                                        if(setting.getTransactionAlertEmail() != null && setting.getTransactionAlertEmail().trim().length() > 0)
                                        {
                                            // TODO: Send transaction alert here
                                            String email = setting.getTransactionAlertEmail();
                                            String[] recipients = email.split(",");
                                            String message = "<html><body><p><strong>Hello</strong></p><p>This is to notify you of a refuel transaction event on the fuel card platform for vehicle: " + (br.getVehicle() != null ? br.getVehicle().getRegNumber() : "N/A") + ", amount: " + NumberFormat.getInstance().format(br.getTranAmt()) + ", Litres: " + NumberFormat.getInstance().format(quantity) + ", Time: " + br.getTranTimeStr() + ". Please go to the platform to review the reports!</p><p>Regards<br/><strong>Fuel Card Platform</strong></p></body></html>";
                                            
                                            NotificationService notificationService = new NotificationService();
                                            notificationService.postMail(recipients, "Transaction Alert", message, notificationService.getSmtp_username());
                                            br.setAlerted(true);
                                            em.merge(br);
                                        }
                                        else
                                        {
                                        	System.out.println("No Transaction Email Set");
                                        }
                                        
                                        if(setting.getThresholdAlertEmail() != null && setting.getMinbalance() != null && setting.getThresholdAlertEmail().trim().length() > 0)
                                        {
                                            // TODO: Check the card balance here, then send notification is required
                                            if(br.getCardBal() <= setting.getMinbalance().doubleValue())
                                            {
                                                // send alert here
                                                String email = setting.getThresholdAlertEmail();
                                                String[] recipients = email.split(",");
                                                String message = "<html><body><p><strong>Hello</strong></p><p>This is to notify you of a card balance threshold breach event on the fuel card platform for card: " + br.getCardPan() + ". Balance on card: " + NumberFormat.getInstance().format(br.getCardBal()) + " as at: " + br.getTranTimeStr() + ". Please go to the platform to review the reports!</p><p>Regards<br/><strong>Fuel Card Platform</strong></p></body></html>";

                                                NotificationService notificationService = new NotificationService();
                                                notificationService.postMail(recipients, "Balance Threshold Alert", message, notificationService.getSmtp_username());
                                            }
                                        }
                                        else
                                        {
                                        	System.out.println("No Threshold Email Set");
                                        }
                                    }
                                    else
                                    {
                                    	System.out.println("Setting is null");
                                    }
                                    
                                    em.flush();
                                }
                                catch(Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                            utx.commit();
                            logger.info("Commited database storage transaction for the downloaded transactions");
                            //boolean ret = zenServiceSoap.updateMaxTransaction(max);
                            //logger.info("Updated zenith web services for the max downloaded transaction id. Return: " + ret);
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                            try
                            {
                                if(utx != null && utx.isActive())
                                {
                                    utx.rollback();
                                    System.out.println("Rolled back database storage transaction for the downloaded transactions");
                                }
                            }
                            catch(Exception ig)
                            {}
                            logger.log(Level.SEVERE, "Persist failed for zen bank record. " + ex);
                        }
                        finally
                        {
                            em.close();
                            System.out.println("Closed database connection.");
                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };
        
        timer = new Timer();
        updateZenIdTimer = new Timer();
    }
    
    @Override
    public void run()
    {
        timer.scheduleAtFixedRate(task, new Date(), 1000 * 60 * tracker_interval); // 5 minutes
        logger.info("Download timer tbread: Started");
        
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        
        updateZenIdTimer.scheduleAtFixedRate(updateZenIdTask, c.getTime(), 1000 * 60 * 60 * 24);
        logger.info("Zen Updating timer tbread: Started");
    }
    
    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is)
    {
    	BufferedReader br = null;
    	StringBuilder sb = new StringBuilder();
    	
    	String line;
    	try
    	{
    		br = new BufferedReader(new InputStreamReader(is));
    		while ((line = br.readLine()) != null) {
    			sb.append(line);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	return sb.toString();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
    	try
    	{
            PrintStream out = new PrintStream(new FileOutputStream("log.txt"));
            System.setOut(out);
        } catch(Exception ex){}
    	
        DownloadService dService = new DownloadService();
        dService.start();
        DownloadService.logger.info("Downloader Thread Started.");
        
        if(dService.startNotificationService)
        {
            NotificationService nService = new NotificationService();
            nService.start();
            NotificationService.logger.info("Notification Thread Started.");
        }
    }
}
