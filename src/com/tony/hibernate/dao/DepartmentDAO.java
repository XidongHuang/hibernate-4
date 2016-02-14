package com.tony.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tony.hibernate.entities.Department;
import com.tony.hibernate.hibernate.HibernateUtils;

public class DepartmentDAO {

	private SessionFactory sessionFactory;
	
	public void setSessionFactory() {
		
	}
	
	
	public void save(Department dept){
		
		//Gain "Session" from inside
		//Gain the object of "Session" that is binding with the current thread
		//1. Do not need to transport a "Session" object
		//2. Many different DAO methods can use a same transaction 
		Session session = HibernateUtils.getInstance().getSession();
		System.out.println(session.hashCode());
//		session.save(dept);
		
	}
	
	
	/**
	 * If need to input a "Session" Object, then it means the "Service" needs to gain "Session" object
	 * So it is "Coupling" with API. (It is not good)
	 * 
	 * @param session
	 * @param dept
	 */
	public void save(Session session, Department dept){
		
		session.save(dept);
		
	}
	
}
