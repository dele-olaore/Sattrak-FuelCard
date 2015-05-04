package com.dexter.fuelcard.dao;

import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fuelcard.model.BankRecord;
import com.dexter.fuelcard.model.Car;

public class BankRecordDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-BankRecordDAO");
    
    public BankRecordDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public boolean createBankRecord(BankRecord br)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(br);
            try
            {
                em.flush();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            utx.commit();
            ret = true;
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Persist failed for bank record. " + ex);
    	}
    	
    	return ret;
    }
    
    @SuppressWarnings("unchecked")
	public Vector<BankRecord> search(Date d1, Date d2, Car car)
    {
    	Vector<BankRecord> list = new Vector<BankRecord>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from BankRecord e where e.vehicle=:vehicle and e.tranStatus=:tranStatus and (e.tranTime between :stdt and :eddt) order by e.tranTime");
    		q.setParameter("vehicle", car);
    		q.setParameter("tranStatus", "Processed");
    		q.setParameter("stdt", d1);
    		q.setParameter("eddt", d2);
    		
    		list = (Vector<BankRecord>)q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for bank record: " + ex);
    	}
    	
    	return list;
    }
    
    @SuppressWarnings("unchecked")
	public Vector<BankRecord> search(Date d1, Date d2, Long partner_id)
    {
    	Vector<BankRecord> list = new Vector<BankRecord>();
    	
    	try
    	{
    		Query q = null;
    		if(partner_id != null)
    		{
	    		q = em.createQuery("Select e from BankRecord e where e.tranStatus=:tranStatus and e.vehicle.partner.id=:partner_id and (e.tranTime between :stdt and :eddt) order by e.tranTime");
	    		q.setParameter("tranStatus", "Processed");
	    		q.setParameter("partner_id", partner_id);
	    		q.setParameter("stdt", d1);
	    		q.setParameter("eddt", d2);
    		}
    		else
    		{
    			q = em.createQuery("Select e from BankRecord e where e.tranStatus=:tranStatus and (e.tranTime between :stdt and :eddt) order by e.tranTime");
	    		q.setParameter("tranStatus", "Processed");
	    		q.setParameter("stdt", d1);
	    		q.setParameter("eddt", d2);
    		}
    		
    		list = (Vector<BankRecord>)q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for bank record: " + ex);
    	}
    	
    	return list;
    }
}
