package com.tony.hibernate.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.ListModel;

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
	public void testLeftJoinFetch2(){
		String hql = "SELECT e FROM Employee e INNER JOIN FETCH e.dept";
		Query query = session.createQuery(hql);
		
		List<Employee> emps = query.list();
		System.out.println(emps.size());
		
		for(Employee e: emps){
			
			System.out.println(e.getName());
			
		}
		
	}
	
	
	
	@Test
	public void testLeftJoin(){
		String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN d.emps";
		Query query = session.createQuery(hql);
		
		List<Department> depts = query.list();
		System.out.println(depts.size());
		
		for(Department dept:depts){
			
			System.out.println(dept.getName());
			
		}
		
//		List<Object[]> result = query.list();
//		result = new ArrayList<>(new LinkedHashSet<>(result));
//		
//		System.out.println(result);
//		
//		for(Object[] obj : result){
//			
//			System.out.println(Arrays.asList(obj));
//			
//		}
		
		
		
	}
	
	@Test
	public void testLeftJoinFetch(){
//		String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.emps";
		String hql = "FROM Department d INNER JOIN FETCH d.emps";
		Query query = session.createQuery(hql);
		
		List<Department> departments = query.list();
		departments = new ArrayList<>(new LinkedHashSet(departments));
		
		System.out.println(departments.size());
		
		for(Department department:departments){
			System.out.println(department.getName() + "-" + department.getEmps().size());
			
		}
		
		
	}
	
	
	
	@Test
	public void testGroupBy(){
		
		String hql = "SELECT min(e.salary), max(e.salary) FROM Employees e GROUP BY e.dept HAVING min(salary) :minSal";
		
		Query query = session.createQuery(hql).setFloat("minSal", 5000);
		
		List<Object[]> result = query.list();
		
		for(Object[] objs: result){
			
			System.out.println(Arrays.asList(objs));
			
			
		}
				
		
	}
	

	@Test
	public void testFieldQuery2(){
		
		String hql = "SELECT new Employee(e.salary, e.email, e.dept) FROM Employees e WHERE e.dept = :dept";
		Query query =session.createQuery(hql);
		
		Department dept = new Department();
		dept.setId(80);
		List<Employee> result = query.setEntity("dept", dept).list();
		
		for(Employee rl: result){
			System.out.println(rl.getId() + ", " + rl.getEmail() + ", " + rl.getSalary() + ", "+ rl.getDept());
			
		}
		
	}
		
		
		@Test
		public void testFieldQuery(){
			
			String hql = "SELECT e.email, e.salary FROM Employees e WHERE e.dept = :dept";
			Query query =session.createQuery(hql);
			
			Department dept = new Department();
			dept.setId(80);
			List<Object[]> result = query.setEntity("dept", dept).list();
			
			for(Object[] rl: result){
				System.out.println(Arrays.asList(rl));
				
			}
		
		
		
		
	}
	
	
	
	@Test
	public void testNamedQuery(){
		
		Query query = session.getNamedQuery("salaryEmps");
		List<Employee> emps = query.setFloat("minSal", 5000)
								.setFloat("maxSal", 10000).list();
		
		System.out.println(emps);
		
		
	}
	
	
	
	@Test
	public void testPageQuery() {
		String hql = "FROM Employee";
		Query query = session.createQuery(hql);

		int pageNo = 3;
		int pageSize = 5;

		List<Employee> emps = query.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();

		System.out.println(emps);
		
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
