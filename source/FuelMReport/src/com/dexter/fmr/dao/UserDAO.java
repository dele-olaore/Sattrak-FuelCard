package com.dexter.fmr.dao;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fmr.model.User;

public class UserDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-UserDAO");
    
    public UserDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public String createUser(User u)
    {
    	String ret = "";
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(u);
            try
            {
                em.flush();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            utx.commit();
            ret = "Success: User created";
    	}
    	catch(Exception ex)
    	{
    		ret = "Error: User create failed: " + ex.getMessage();
    		logger.log(Level.SEVERE, "Persist failed for user. " + ex);
    	}
    	
    	return ret;
    }
    
    public String updateUser(User u)
    {
    	String ret = "";
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.merge(u);
            try
            {
                em.flush();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            utx.commit();
            ret = "Success: User updated";
    	}
    	catch(Exception ex)
    	{
    		ret = "Error: User update failed: " + ex.getMessage();
    		logger.log(Level.SEVERE, "Persist update failed for user. " + ex);
    	}
    	
    	return ret;
    }
    
    public boolean deleteUser(User u)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            
            u = em.find(User.class, u.getId());
            
            em.remove(u);
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
    		ret = false;
    		logger.log(Level.SEVERE, "Delete failed for user. " + ex);
    	}
    	
    	return ret;
    }
    
    public User getUserById(Long id)
    {
    	return em.find(User.class, id);
    }
    
    public User getUserByLogin(String username, String password)
    {
    	User u = null;
    	
    	try
    	{
    		Query q = em.createQuery("Select e from User e where e.username = :username and e.password = :password");
    		q.setParameter("username", username);
    		q.setParameter("password", password);
    		
    		Object uObj = q.getSingleResult();
    		if(uObj != null && uObj instanceof User)
    		{
    			u = (User) uObj;
    		}
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Exception during authentication: " + ex);
    	}
    	
    	return u;
    }
    
    public Vector<User> getUsers()
    {
    	Vector<User> list = new Vector<User>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from User e order by e.username");
			list = (Vector<User>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for users. " + ex);
    	}
    	
    	return list;
    }
    
    public Vector<User> getDrivers()
    {
    	Vector<User> list = new Vector<User>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from User e where e.role.name = :role_name order by e.username");
    		q.setParameter("role_name", "DRIVER");
			list = (Vector<User>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for users. " + ex);
    	}
    	
    	return list;
    }
}
