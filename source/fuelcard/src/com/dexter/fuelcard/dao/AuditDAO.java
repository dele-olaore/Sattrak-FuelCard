package com.dexter.fuelcard.dao;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fuelcard.model.AuditTrail;

public class AuditDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-AuditDAO");
    
    public AuditDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public boolean save(AuditTrail audit)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(audit);
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
    		logger.log(Level.SEVERE, "Persist failed for audit. " + ex);
    	}
    	
    	return ret;
    }
    
    @SuppressWarnings("unchecked")
	public Vector<AuditTrail> getAudits()
    {
    	Vector<AuditTrail> list = new Vector<AuditTrail>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from AuditTrail e order by e.auditTime desc");
			list = (Vector<AuditTrail>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for audits. " + ex);
    	}
    	
    	return list;
    }
    
    @SuppressWarnings("unchecked")
	public Vector<AuditTrail> searchAudits(String actP, String username, Boolean succ)
    {
    	Vector<AuditTrail> list = new Vector<AuditTrail>();
    	
    	try
    	{
    		String query = "Select e from AuditTrail e where e.id > 0 ";
    		
    		if(actP != null)
    			query += " and e.actionPerformed like :actP";
    		if(username != null)
    			query += " and e.username = :username";
    		if(succ != null)
    			query += " and e.success=:success";
    		query += " order by e.auditTime desc";
    		
    		Query q = em.createQuery(query);
    		if(actP != null)
    			q.setParameter("actP", "%"+actP+"%");
    		if(username != null)
    			q.setParameter("username", username);
    		if(succ != null)
    			q.setParameter("success", succ);
			list = (Vector<AuditTrail>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for audits. " + ex);
    	}
    	
    	return list;
    }
    
    public boolean clearAudits()
    {
    	boolean ret = false;
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.createQuery("Delete from AuditTrail");
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
    		logger.log(Level.SEVERE, "Persist failed for audit. " + ex);
    	}
    	return ret;
    }
}
