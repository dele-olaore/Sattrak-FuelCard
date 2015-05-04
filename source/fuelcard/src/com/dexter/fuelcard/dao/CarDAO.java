package com.dexter.fuelcard.dao;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fuelcard.model.Car;
import com.dexter.fuelcard.model.Partner;
import com.dexter.fuelcard.model.User;

public class CarDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("fuelcard-CarDAO");
    
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
    
    @SuppressWarnings("unchecked")
	public Vector<Car> getCars(Partner partner)
    {
    	Vector<Car> list = new Vector<Car>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Car e where e.partner = :partner order by e.regNumber");
    		q.setParameter("partner", partner);
			list = (Vector<Car>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for cars. " + ex);
    	}
    	
    	return list;
    }
    
    @SuppressWarnings("unchecked")
	public Vector<Car> getCarsNotAssigned(Partner partner)
    {
    	Vector<Car> list = new Vector<Car>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Car e where e.partner = :partner and e.assigned = :assigned order by e.regNumber");
    		q.setParameter("assigned", false);
    		q.setParameter("partner", partner);
			list = (Vector<Car>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for cars. " + ex);
    	}
    	
    	return list;
    }
}
