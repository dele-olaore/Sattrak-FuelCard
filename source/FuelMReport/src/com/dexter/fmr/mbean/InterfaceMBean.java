package com.dexter.fmr.mbean;

import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.rpc.ServiceException;

import org.datacontract.schemas._2004._07.ZONIntegrationWCFService.EResult;
import org.datacontract.schemas._2004._07.ZONIntegrationWCFService.SATTRACKUnitEventResult;
import org.datacontract.schemas._2004._07.ZONIntegrationWCFService.UnitEvent;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Synchronized;
import org.tempuri.IZONService;
import org.tempuri.ZONServiceLocator;

import com.dexter.fmr.model.BankRecord;
import com.dexter.fmr.model.Car;
import com.dexter.fmr.model.TrackerRecord;
import com.zenithbank.mtn.fleet.ServiceLocator;
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
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-InterfaceMBean");

	Timer financialTimer, trackerTimer;
	TimerTask financialTask, trackerTask;
	
	private int tracker_interval = 2; // interval in minutes
	private Long lastReceivedId = 0L;
	
	private static boolean started = false;
	
	public InterfaceMBean()
	{
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		/*Authenticator.setDefault(new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("username", "password".toCharArray());
            }
        });*/
		
		trackerTimer = new Timer();
		trackerTask = new TimerTask()
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
					
					IZONService zonService = zonLocator.getBasicHttpBinding_IZONService();
					SATTRACKUnitEventResult result = zonService.SATTRAK_GetDailyEvents(c, c2, lastReceivedId);
					if(result.getResult() == EResult.Ok)
					{
						lastReceivedId = result.getLastQueriedId();
						UnitEvent[] unitEvents = result.getUnitEvents();
						if(unitEvents != null && unitEvents.length > 0)
						{
							em = factory.createEntityManager();
							try
					    	{
					    		utx = em.getTransaction();
					            utx.begin();
					            for(UnitEvent e : unitEvents)
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
									tr.setTranTime(e.getEventTime().getTime());
									tr.setUnitName(e.getEventName());
									tr.setCost(new BigDecimal(e.getEventValue()));
									tr.setInitFuelLvl(new BigDecimal(e.getEventInitValue()));
									tr.setFinalFuelLvl(new BigDecimal(e.getEventFinalValue()));
									tr.setOdometer(new BigDecimal(e.getDistance()));
									tr.setAddress(e.getAddress());
									
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
								utx.commit();
								logger.info("Commited database storage transaction for the downloaded events");
					    	}
							catch(Exception ex)
					    	{
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
				}
				catch(ServiceException ex)
            	{
					ex.printStackTrace();
				}
            	catch(RemoteException ex)
            	{
					ex.printStackTrace();
				}
			}
		};
		
		financialTimer = new Timer();
		financialTask = new TimerTask()
    	{
            @SuppressWarnings("unused")
			@Override
            public void run()
            {
            	logger.info("Starting transactions download from zenith web service.");
            	
            	/*long maxTranId = 0;
            	
            	Query q = em.createQuery("Select Max(e.transID) from Transaction e");
            	Object qRet = q.getSingleResult();
            	if(qRet != null)
            		maxTranId = (Long)qRet;*/
            	
            	//TODO Download the financial information here by calling the required web service
            	ServiceLocator serviceLocator = new ServiceLocator();
            	try
            	{
					ServiceSoap zenServiceSoap = serviceLocator.getServiceSoap();
					
					BigDecimal max = new BigDecimal(0);
					Transaction[] transactions = zenServiceSoap.getCardTrans(true);
					logger.info(transactions.length + " transactions downloaded");
					if(transactions.length > 0)
					{
						em = factory.createEntityManager();
						try
				    	{
				    		utx = em.getTransaction();
				            utx.begin();
				            logger.info("Started database storage transaction for the downloaded transactions");
							for(Transaction t : transactions)
							{
								if(max.compareTo(t.getTransID()) < 0)
									max = new BigDecimal(t.getTransID().doubleValue());
								
								BankRecord br = new BankRecord();
								br.setCrt_dt(new Date());
								
								Query q = em.createQuery("Select e from Car e where e.regNumber = :regNumber");
				            	q.setParameter("regNumber", t.getPan());
				            	
				            	Object vObj = q.getSingleResult();
				            	if(vObj != null)
				            	{
				            		Car vehicle = (Car)vObj;
				            		br.setVehicle(vehicle);
				            	}
								
								br.setCardAcceptorId(t.getCardAcceptorID());
								br.setCardAcceptorLoc(t.getCardAcceptorLocation());
								br.setCardBal(t.getClosingBalance());
								br.setCardPan(t.getPan());
								br.setRetrievalRefNum(t.getRetrievalReference());
								br.setSchemeOwner(t.getSchemeOwnerName());
								br.setTranAmt(t.getAmount());
								br.setTranFees(t.getFees());
								br.setTranStatus(t.getTransactionStatus());
								
								//TODO determine the date format and parse it here before setting to the br
								//br.setTranTime(t.getTransactionDate());
								br.setTranTimeStr(t.getTransactionDate());
								br.setTranType(t.getTransactionType());
								
					            em.persist(t);
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
							logger.info("Updated zenith web services for the max downloaded transaction id");
				    	}
				    	catch(Exception ex)
				    	{
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
            	catch(ServiceException ex)
            	{
					ex.printStackTrace();
				}
            	catch(RemoteException ex)
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
			financialTimer.scheduleAtFixedRate(financialTask, new Date(), 1000 * 60 * 5); // every five minutes after first call
	    	
			ServiceLocator serviceLocator = new ServiceLocator();
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
			
			
	    	Calendar c = Calendar.getInstance();
	    	
	    	trackerTimer.scheduleAtFixedRate(trackerTask, c.getTime(), 1000 * 60 * tracker_interval); // every one day after first call
	    	InterfaceMBean.started = true;
		}
	}
}
