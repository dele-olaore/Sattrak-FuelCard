package com.dexter.fuelcard.dao;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fuelcard.model.CardBalanceNotification;
import com.dexter.fuelcard.model.Partner;

public class CardBalanceDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-CardBalanceDAO");
    
    public CardBalanceDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public boolean setBalance(CardBalanceNotification bal)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(bal);
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
    		logger.log(Level.SEVERE, "Persist failed for card balance notification. " + ex);
    	}
    	
    	return ret;
    }
    
    public boolean updateBalance(CardBalanceNotification bal)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.merge(bal);
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
    		logger.log(Level.SEVERE, "Persist failed for card balance notification. " + ex);
    	}
    	
    	return ret;
    }
    
    @SuppressWarnings("unchecked")
	public Vector<CardBalanceNotification> getSettings(long partner_id)
    {
    	Vector<CardBalanceNotification> list = new Vector<CardBalanceNotification>();
    	try
    	{
    		Query q = em.createQuery("Select e from CardBalanceNotification e where e.partner.id = :partner_id");
    		q.setParameter("partner_id", partner_id);
    		Object obj = q.getResultList();
    		if(obj != null)
    			list = (Vector<CardBalanceNotification>) obj;
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for card notification settings. " + ex);
    	}
    	
    	return list;
    }
    
    @SuppressWarnings("unchecked")
	public CardBalanceNotification getBalanceNotification(Partner partner)
    {
    	CardBalanceNotification e = null;
    	
    	Vector<CardBalanceNotification> list = new Vector<CardBalanceNotification>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from CardBalanceNotification e where e.partner = :partner");
    		q.setParameter("partner", partner);
    		Object obj = q.getResultList();
    		if(obj != null)
			list = (Vector<CardBalanceNotification>) obj;
			
			e = list.get(0);
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for card notification balances. " + ex);
    	}
    	
    	return e;
    }
}
