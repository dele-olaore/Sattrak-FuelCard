package com.dexter.fmr.mbean;

import java.math.BigDecimal;
//import java.net.Authenticator;
//import java.net.PasswordAuthentication;
import java.rmi.RemoteException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.rpc.ServiceException;

import org.datacontract.schemas._2004._07.ZONIntegrationWCFService.EResult;
import org.datacontract.schemas._2004._07.ZONIntegrationWCFService.SAATRAKUnitEvent;
import org.datacontract.schemas._2004._07.ZONIntegrationWCFService.SATTRACKUnitEventResult;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Synchronized;
import org.tempuri.IZONService;
import org.tempuri.ZONServiceLocator;

import com.dexter.fmr.model.BankRecord;
import com.dexter.fmr.model.Car;
import com.dexter.fmr.model.TrackerLastID;
import com.dexter.fmr.model.TrackerRecord;
import com.zenithbank.mtn.fleet.ArrayOfTransaction;
import com.zenithbank.mtn.fleet.Service;
import com.zenithbank.mtn.fleet.ServiceSoap;
import com.zenithbank.mtn.fleet.Transaction;

@Name("interfaceMBean")
@Scope(ScopeType.APPLICATION)
@Startup
@Synchronized
public class InterfaceMBean implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    //private static EntityManagerFactory factory;
    //private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-InterfaceMBean");

	Timer downloadTimer;
	TimerTask downloadTask;
	
	private int tracker_interval = 5; // interval in minutes
	private Long lastReceivedId = 1L;
	
	private static boolean started = false;
	
	private Properties config;
	// initial download time
    private boolean initial_download_set = false;
    private int initial_download_date_year;
    private int initial_download_date_month;
    private int initial_download_date_day;
    private int initial_download_time_hour;
    private int initial_download_time_minute;
	private boolean firstRun = true;
	
	static
	{
		disableSslVerification();
	}
	
	private static void disableSslVerification()
	{
		try
		{
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager()
			{
				public java.security.cert.X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
						String authType) throws CertificateException
				{}
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
						String authType) throws CertificateException
				{}
			}
			};

			// Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier()
	        {
	        	@Override
				public boolean verify(String arg0, SSLSession arg1)
	        	{
	        		return true;
				}
	        };
	        
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    }
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch(KeyManagementException e)
		{
			e.printStackTrace();
		}
	}
	
	public InterfaceMBean()
	{
		//factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		/*Authenticator.setDefault(new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("username", "password".toCharArray());
            }
        });*/
		
		config = new Properties();
        try
        {
            config.load(this.getClass().getResourceAsStream("tracker.properties"));
            if(config.containsKey("initial.download.date.year") && config.containsKey("initial.download.date.month") &&
                    config.containsKey("initial.download.date.day") && config.containsKey("initial.download.time.hour") &&
                    config.containsKey("initial.download.time.minute"))
            {
                try
                {
                    initial_download_date_year = Integer.parseInt(config.getProperty("initial.download.date.year"));
                    initial_download_date_month = Integer.parseInt(config.getProperty("initial.download.date.month"));
                    initial_download_date_day = Integer.parseInt(config.getProperty("initial.download.date.day"));
                    initial_download_time_hour = Integer.parseInt(config.getProperty("initial.download.time.hour"));
                    initial_download_time_minute = Integer.parseInt(config.getProperty("initial.download.time.minute"));
                    
                    initial_download_set = true;
                    firstRun = true;
                }
                catch(Exception ex)
                {
                    logger.info("Initial download settings in config is in wrong format. " + ex.getMessage());
                }
            }
            
            try
            {
            	if(config.containsKey("tracker.lastid"))
            		lastReceivedId = Long.parseLong(config.getProperty("tracker.lastid"));
            }
            catch(Exception ex)
            {
                logger.info("Last tracker ID in wrong format. " + ex.getMessage());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
		
        downloadTimer = new Timer();
		downloadTask = new TimerTask()
		{
			@Override
			public void run()
			{
				logger.info("Starting tracker info download from the tracker web service");
				
				ZONServiceLocator zonLocator = new ZONServiceLocator();
				try
				{
					Calendar c = Calendar.getInstance();
					Calendar c2 = Calendar.getInstance();
					
					c.add(Calendar.MINUTE, -tracker_interval);
					
					if(firstRun && initial_download_set)
	                {
	                    c.set(initial_download_date_year, initial_download_date_month-1, initial_download_date_day, initial_download_time_hour, initial_download_time_minute);
	                }
	                
					IZONService zonService = zonLocator.getBasicHttpBinding_IZONService();
					SATTRACKUnitEventResult result = zonService.SATTRAK_GetDailyEvents(c, c2, lastReceivedId);
					if(result == null)
						System.out.println("Tracker result object is null");
					if(result != null && result.getResult() != null)
						System.out.println("Tracker Result: " + result.getResult().getValue());
					if(result != null && result.getResult() != null && result.getResult() == EResult.Ok)
					{
						lastReceivedId = result.getLastQueriedId();
						TrackerLastID tlID = new TrackerLastID();
						tlID.setLastTrackerID(lastReceivedId);
						tlID.setTranTime(new Date());
						
						System.out.println("Tracker last TID: " + lastReceivedId);
						SAATRAKUnitEvent[] unitEvents = result.getUnitEvents();
						System.out.println("Tracker records count: " + unitEvents.length);
						if(unitEvents != null && unitEvents.length > 0)
						{
							EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
							EntityManager em = factory.createEntityManager();
							EntityTransaction utx = null;
							try
					    	{
								utx = em.getTransaction();
					            utx.begin();
					            
					            em.persist(tlID);
					            
					            for(SAATRAKUnitEvent e : unitEvents)
								{
					            	TrackerRecord tr = new TrackerRecord();
					            	tr.setCrt_dt(new Date());
					            	
					            	Query q = em.createQuery("Select e from Car e where e.zonControlId = :zonControlId");
					            	q.setParameter("zonControlId", e.getUnitID());
					            	
					            	Object vObj = q.getSingleResult();
					            	if(vObj != null)
					            	{
					            		Car vehicle = (Car)vObj;
					            		tr.setVehicle(vehicle);
					            	}
									
									tr.setUnitID(e.getUnitID()); // vehicle identifier
									tr.setTranType(e.getEventType().getValue());
									
									// the tran time coming from the tracker is in GTM, so we add 1 hour to it to get the GTM+1 value, which is our time zone
									Calendar tranTimeCal = Calendar.getInstance();
									tranTimeCal.setTime(e.getEventTime().getTime());
									tranTimeCal.add(Calendar.HOUR, -1);
									
									tr.setTranTime(tranTimeCal.getTime());
									tr.setUnitName(e.getEventName());
									tr.setCost(e.getEventValue());
									tr.setInitFuelLvl(e.getEventInitValue());
									tr.setFinalFuelLvl(e.getEventFinalValue());
									tr.setOdometer(e.getDistance());
									tr.setAddress(e.getAddress());
									
									if(firstRun)
					            	{
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
									else
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
								utx.commit();
								logger.info("Commited database storage transaction for the downloaded events");
					    	}
							catch(Exception ex)
					    	{
								ex.printStackTrace();
					    		try
					    		{
					    			if(utx != null && utx.isActive())
					    			{
					    				utx.rollback();
					    				logger.info("Rolled back database storage transaction for the downloaded events");
					    			}
					    		}
					    		catch(Exception ig)
					    		{}
					    		logger.log(Level.SEVERE, "Persist failed for galoli event record. " + ex);
					    	}
							finally
							{
								em.close();
								logger.info("Closed database connection.");
							}
						}
					}
					else
					{
						logger.info("Null result returned from tracker.");
					}
				}
				catch(ServiceException ex)
            	{
					ex.printStackTrace();
				}
            	catch(RemoteException ex)
            	{
					ex.printStackTrace();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				
				if(firstRun)
                    firstRun = false;
				
				logger.info("Starting transactions download from zenith web service.");
            	
            	// Download the financial information here by calling the required web service
            	Service serviceLocator = new Service();
            	try
            	{
					ServiceSoap zenServiceSoap = serviceLocator.getServiceSoap();
					
					//zenServiceSoap.updateMaxTransaction(new BigDecimal(0));
					
					BigDecimal max = new BigDecimal(0);
					ArrayOfTransaction transactions = zenServiceSoap.getCardTrans(true);
					logger.info(transactions.getTransaction().size() + " transactions downloaded from zenith web service.");
					if(transactions.getTransaction().size() > 0)
					{
						EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
						EntityManager em = factory.createEntityManager();
						EntityTransaction utx = null;
						try
				    	{
				    		utx = em.getTransaction();
				            utx.begin();
				            logger.info("Started database storage transaction for the downloaded transactions from zenith web service");
							for(Transaction t : transactions.getTransaction())
							{
								if(max.doubleValue() < t.getTransID().doubleValue())
									max = new BigDecimal(t.getTransID().doubleValue());
								
								BankRecord br = new BankRecord();
								br.setCrt_dt(new Date());
								br.setTranID(t.getTransID().doubleValue());
								
								try
								{
									Query q = em.createQuery("Select e from Car e where e.cardPan = :cardPan");
					            	q.setParameter("cardPan", t.getPan().substring(t.getPan().length()-4));
					            	
					            	Object vObj = q.getSingleResult();
					            	if(vObj != null)
					            	{
					            		Car vehicle = (Car)vObj;
					            		br.setVehicle(vehicle);
					            	}
								}
								catch(Exception ex)
								{}
								
								br.setCardAcceptorId(t.getCardAcceptorID());
								br.setCardAcceptorLoc(t.getCardAcceptorLocation());
								br.setCardBal(t.getAvailBalance());
								br.setCardPan(t.getPan());
								br.setRetrievalRefNum(t.getRetrievalReference());
								br.setSchemeOwner(t.getSchemeOwnerName());
								br.setTranAmt(t.getAmount());
								br.setTranFees(t.getFees());
								br.setTranStatus(t.getTransactionStatus());
								
								SimpleDateFormat formatter = new SimpleDateFormat("M/d/y h:m:s a"); // EEEE, MMM dd, yyyy
								Date date = formatter.parse(t.getTransactionDate());
								br.setTranTime(date);
								
								br.setTranTimeStr(t.getTransactionDate());
								br.setTranType(t.getTransactionType());
								
					            //em.persist(t);
					            em.persist(br);
					            try
					            {
					                em.flush();
					            }
					            catch(Exception ex)
					            {
					                ex.printStackTrace();
					            }
							}
							utx.commit();
							logger.info("Commited database storage transaction for the downloaded transactions");
							boolean ret = zenServiceSoap.updateMaxTransaction(max);
							logger.info("Updated zenith web services for the max downloaded transaction id. Return: " + ret);
				    	}
				    	catch(Exception ex)
				    	{
				    		ex.printStackTrace();
				    		try
				    		{
				    			if(utx != null && utx.isActive())
				    			{
				    				utx.rollback();
				    				logger.info("Rolled back database storage transaction for the downloaded transactions");
				    			}
				    		}
				    		catch(Exception ig)
				    		{}
				    		logger.log(Level.SEVERE, "Persist failed for zen bank record. " + ex);
				    	}
						finally
						{
							em.close();
							logger.info("Closed database connection.");
						}
					}
				}
            	catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
			}
		};
    	
    	start();
	}
	
	private void start()
	{
		if(!InterfaceMBean.started)
		{
			/*ServiceLocator serviceLocator = new ServiceLocator();
			try
        	{
				ServiceSoap zenServiceSoap = serviceLocator.getServiceSoap();
				zenServiceSoap.updateMaxTransaction(new BigDecimal(0));
				logger.info("call update max transaction for first time");
        	}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			*/
			//financialTimer.scheduleAtFixedRate(financialTask, new Date(), 1000 * 60 * 3); // every three minutes after first call
			
			//downloadTimer.scheduleAtFixedRate(downloadTask, new Date(), 1000 * 60 * tracker_interval); // every 5 minutes after first call
	    	InterfaceMBean.started = true;
		}
	}
}
