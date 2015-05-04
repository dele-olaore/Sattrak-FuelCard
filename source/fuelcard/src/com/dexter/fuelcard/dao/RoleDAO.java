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
import com.dexter.fuelcard.model.Role;
import com.dexter.fuelcard.model.RoleFunction;

public class RoleDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-RoleDAO");
    
    public RoleDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public boolean createRole(Role r)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(r);
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
    		logger.log(Level.SEVERE, "Persist failed for role. " + ex);
    	}
    	
    	return ret;
    }
    
    public boolean updateRole(Role r)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.merge(r);
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
    		logger.log(Level.SEVERE, "Update persist failed for role. " + ex);
    	}
    	
    	return ret;
    }
    
    public Role getRoleById(Long id)
    {
    	return em.find(Role.class, id);
    }
    
    @SuppressWarnings("unchecked")
	public Vector<Role> getRoles(Partner p)
    {
    	Vector<Role> list = new Vector<Role>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Role e where e.partner = :partner order by e.name");
    		q.setParameter("partner", p);
			list = (Vector<Role>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for roles. " + ex);
    	}
    	
    	return list;
    }
    
    @SuppressWarnings("unchecked")
	public Vector<RoleFunction> getRoleFunctions(Role e)
    {
    	Vector<RoleFunction> list = new Vector<RoleFunction>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from RoleFunction e where e.role = :role");
    		q.setParameter("role", e);
			list = (Vector<RoleFunction>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for role functions. " + ex);
    	}
    	
    	return list;
    }
    
    public boolean addFunctionToRole(RoleFunction e)
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
    		logger.log(Level.SEVERE, "Persist failed for role function. " + ex);
    	}
    	
    	return ret;
    }
    
    public boolean removeFunctionFromRole(RoleFunction e)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            
            e = em.find(RoleFunction.class, e.getId());
            
            //em.merge(e);
            em.remove(e);
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
    		logger.log(Level.SEVERE, "Delete failed for role function. " + ex);
    	}
    	
    	return ret;
    }
}
