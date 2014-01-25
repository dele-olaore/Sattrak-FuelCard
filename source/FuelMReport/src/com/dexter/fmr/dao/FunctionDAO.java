package com.dexter.fmr.dao;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fmr.model.Function;

public class FunctionDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-FunctionDAO");
    
    public FunctionDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public boolean createFunction(Function e)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(e);
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
    		logger.log(Level.SEVERE, "Persist failed for function. " + ex);
    	}
    	
    	return ret;
    }
    
    public Vector<Function> getAllFunctions()
    {
    	Vector<Function> list = new Vector<Function>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Function e order by e.name");
			list = (Vector<Function>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for all functions. " + ex);
    	}
    	
    	return list;
    }
    
    public Function getFunctionById(Long id)
    {
    	return em.find(Function.class, id);
    }
}
