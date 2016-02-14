package com.tony.hibernate.test;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tony.hibernate.entities.Department;
import com.tony.hibernate.entities.Employee;

public class HibernateTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transation;

	@Before
	public void init() {

		Configuration configuration = new Configuration().configure();

		StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml")
				.build();
		Metadata metadata = new MetadataSources(standardRegistry).getMetadataBuilder().build();

		sessionFactory = metadata.getSessionFactoryBuilder().build();

		session = sessionFactory.openSession();
		transation = session.beginTransaction();
	}

	@After
	public void destory() {

		transation.commit();
		session.close();
		sessionFactory.close();

	}

	@Test
	public void testHQLNamedParameter() {

		// 1. Create Query object
		String hql = "FROM Employee e WHERE e.salary > :sal AND e.email LIKE :email";
		Query query = session.createQuery(hql);

		// 2. Binding parameters
		query.setFloat("sal", 7000);
		query.setString("email", "%B%");

		// 3. Execute Querying
		List<Employee> emps = query.list();
		System.out.println(emps);

	}

	@Test
	public void testHQL() {

		// 1. Create Query object
		// Base place holder
		String hql = "FROM Employee e WHERE e.salary > ? AND e.email LIKE ? AND e.dept = ? ORDER BY e.salary";
		Query query = session.createQuery(hql);

		// 2. Binding parameters
		// Query object invokes "setXxx()" method to support method chain
		// program style
		Department dept = new Department();
		dept.setId(80);

		query.setFloat(0, 6000).setString(1, "%A%").setEntity(2, dept);

		// 3. Execute Querying
		List<Employee> emps = query.list();
		System.out.println(emps);

	}

}
