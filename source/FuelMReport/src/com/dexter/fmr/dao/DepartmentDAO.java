package com.dexter.fmr.dao;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dexter.fmr.model.Department;

public class DepartmentDAO
{
	private static final String PERSISTENCE_UNIT_NAME = "fuelm";
    private static EntityManagerFactory factory;
    private EntityManager em;
	
    private EntityTransaction utx;
    
    final Logger logger = Logger.getLogger("FuelMReport-DepartmentDAO");
    
    public DepartmentDAO()
    {
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
    
    public boolean save(Department department)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.persist(department);
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
    
    public boolean update(Department department)
    {
    	boolean ret = false;
    	
    	try
    	{
    		utx = em.getTransaction();
            utx.begin();
            em.merge(department);
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
	public Vector<Department> getDepartments()
    {
    	Vector<Department> list = new Vector<Department>();
    	
    	try
    	{
    		Query q = em.createQuery("Select e from Department e order by e.name");
			list = (Vector<Department>) q.getResultList();
    	}
    	catch(Exception ex)
    	{
    		logger.log(Level.SEVERE, "Retieve failed for regions. " + ex);
    	}
    	
    	return list;
    }
}
