package com.dexter.fuelcard.dao;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fuelcard.model.Partner;
import com.dexter.fuelcard.model.Region;

public class RegionDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-RegionDAO");
    
    public RegionDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public Region getRegionByName(String name)
    {
    	Region c = null;
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Region e where e.name = :name");
    		q.setParameter("name", name);
    		
    		c = (Region)q.getSingleResult();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for region. " + ex);
    	}
    	
    	return c;
    }
    
    public Region getRegionById(Long id)
    {
    	return em.find(Region.class, id);
    }
    
    public boolean save(Region region)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(region);
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
    		logger.log(Level.SEVERE, "Persist failed for region. " + ex);
    	}
    	
    	return ret;
    }
    
    public boolean update(Region region)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.merge(region);
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
    		logger.log(Level.SEVERE, "Persist failed for region. " + ex);
    	}
    	
    	return ret;
    }
    
    @SuppressWarnings("unchecked")
	public Vector<Region> getRegions(Partner p)
    {
    	Vector<Region> list = new Vector<Region>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Region e where e.partner = :partner order by e.name");
    		q.setParameter("partner", p);
			list = (Vector<Region>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for regions. " + ex);
    	}
    	
    	return list;
    }
}
