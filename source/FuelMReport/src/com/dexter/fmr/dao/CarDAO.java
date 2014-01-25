package com.dexter.fmr.dao;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fmr.model.Car;
import com.dexter.fmr.model.User;

public class CarDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-CarDAO");
    
    public CarDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public boolean createCar(Car c)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(c);
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
    		logger.log(Level.SEVERE, "Persist failed for car. " + ex);
    	}
    	
    	return ret;
    }
    
    public boolean updateCar(Car c)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.merge(c);
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
    		logger.log(Level.SEVERE, "Update failed for car. " + ex);
    	}
    	
    	return ret;
    }
    
    public Car getCarById(Long id)
    {
    	return em.find(Car.class, id);
    }
    
    public Car getCarByRegNumber(String regNumber)
    {
    	Car c = null;
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Car e where e.regNumber = :regNumber");
    		q.setParameter("regNumber", regNumber);
    		
    		c = (Car)q.getSingleResult();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for car. " + ex);
    	}
    	
    	return c;
    }
    
    public Car getCarByDriver(User driver)
    {
    	Car c = null;
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Car e where e.assignedUser = :assignedUser");
    		q.setParameter("assignedUser", driver);
    		
    		c = (Car)q.getSingleResult();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for car. " + ex);
    	}
    	
    	return c;
    }
    
    public Vector<Car> getCars()
    {
    	Vector<Car> list = new Vector<Car>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Car e order by e.regNumber");
			list = (Vector<Car>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for cars. " + ex);
    	}
    	
    	return list;
    }
    
    public Vector<Car> getCarsNotAssigned()
    {
    	Vector<Car> list = new Vector<Car>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Car e where e.assigned = :assigned order by e.regNumber");
    		q.setParameter("assigned", false);
			list = (Vector<Car>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for cars. " + ex);
    	}
    	
    	return list;
    }
}
