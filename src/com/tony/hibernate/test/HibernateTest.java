package com.tony.hibernate.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.ListModel;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tony.hibernate.dao.DepartmentDAO;
import com.tony.hibernate.entities.Department;
import com.tony.hibernate.entities.Employee;
import com.tony.hibernate.hibernate.HibernateUtils;

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
	public void testBatch(){
		
		session.doWork(new Work() {
			
			@Override
			public void execute(Connection arg0) throws SQLException {

				//Execute JDBC's original API will be the fastest 
				
			}
		});
		
	}
	
	
	@Test
	public void testManageSession(){
		
		//Gain Session
		//Open transaction
		Session session = HibernateUtils.getInstance().getSession();
		System.out.println("--> " + session.hashCode());
		Transaction transaction = session.beginTransaction();
		
		
		DepartmentDAO departmentDAO = new DepartmentDAO();
		
		
		Department dept = new Department();
		dept.setName("Tony");
		departmentDAO.save(dept);
		departmentDAO.save(dept);
		departmentDAO.save(dept);
		
		transaction.commit();
		System.out.println(session.isOpen());
		
	}
	
	
	public void testQueryIterate(){
		
		Department dept = session.get(Department.class, 80);
		System.out.println(dept.getName());
		System.out.println(dept.getEmps().size());
		
		Query query = session.createQuery("FROM Employee e WHERE e.dept.id = 80");
//		List<Employee> emps = query.list();
//		System.out.println(emps.size());
		
		Iterator<Employee> emptIt = query.iterate();
		
		while(emptIt.hasNext()){
			
			System.out.println(emptIt.next().getName());
			
			
		}
	}
	
	
	@Test
	public void testUpdateTimeStampCache(){
		
		Query query = session.createQuery("FROM Employee");
		query.setCacheable(true);
		
		
		List<Employee> emps = query.list();
		System.out.println(emps.size());
		
		emps = query.list();
		System.out.println(emps.size());
		
		Employee employee = session.get(Employee.class, 100);
		employee.setSalary(30000);
		
		emps = query.list();
		System.out.println(emps.size());
	}
	
	
	@Test
	public void testQueryCache(){
		
		Query query = session.createQuery("FROM Employee");
		query.setCacheable(true);
		
		
		List<Employee> emps = query.list();
		System.out.println(emps.size());
		
		emps = query.list();
		System.out.println(emps.size());
		
		Criteria criteria = session.createCriteria(Employee.class);
		criteria.setCacheable(true);
	}
	
	
	@Test
	public void testCollectionSecondLevelCache(){
		Department dept = session.get(Department.class, 80);
		System.out.println(dept.getName());
		System.out.println(dept.getEmps().size());
		
		transation.commit();
		session.close();

		session = sessionFactory.openSession();
		transation = session.beginTransaction();

		Department dept2 = session.get(Department.class, 80);
		System.out.println(dept2.getName());
		System.out.println(dept2.getEmps().size());
		
		
	}
	
	
	@Test
	public void testHibernateSecondLevelCache() {

		Employee employee = session.get(Employee.class, 100);
		System.out.println(employee.getName());

		transation.commit();
		session.close();

		session = sessionFactory.openSession();
		transation = session.beginTransaction();

		Employee employee2 = session.get(Employee.class, 100);
		System.out.println(employee2.getName());

	}
	

	@Test
	public void testLeftJoinFetch2() {
		String hql = "SELECT e FROM Employee e INNER JOIN FETCH e.dept";
		Query query = session.createQuery(hql);

		List<Employee> emps = query.list();
		System.out.println(emps.size());

		for (Employee e : emps) {

			System.out.println(e.getName());

		}

	}

	@Test
	public void testLeftJoin() {
		String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN d.emps";
		Query query = session.createQuery(hql);

		List<Department> depts = query.list();
		System.out.println(depts.size());

		for (Department dept : depts) {

			System.out.println(dept.getName());

		}

		// List<Object[]> result = query.list();
		// result = new ArrayList<>(new LinkedHashSet<>(result));
		//
		// System.out.println(result);
		//
		// for(Object[] obj : result){
		//
		// System.out.println(Arrays.asList(obj));
		//
		// }

	}

	@Test
	public void testLeftJoinFetch() {
		// String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN FETCH
		// d.emps";
		String hql = "FROM Department d INNER JOIN FETCH d.emps";
		Query query = session.createQuery(hql);

		List<Department> departments = query.list();
		departments = new ArrayList<>(new LinkedHashSet(departments));

		System.out.println(departments.size());

		for (Department department : departments) {
			System.out.println(department.getName() + "-" + department.getEmps().size());

		}

	}

	@Test
	public void testGroupBy() {

		String hql = "SELECT min(e.salary), max(e.salary) FROM Employees e GROUP BY e.dept HAVING min(salary) :minSal";

		Query query = session.createQuery(hql).setFloat("minSal", 5000);

		List<Object[]> result = query.list();

		for (Object[] objs : result) {

			System.out.println(Arrays.asList(objs));

		}

	}

	@Test
	public void testFieldQuery2() {

		String hql = "SELECT new Employee(e.salary, e.email, e.dept) FROM Employees e WHERE e.dept = :dept";
		Query query = session.createQuery(hql);

		Department dept = new Department();
		dept.setId(80);
		List<Employee> result = query.setEntity("dept", dept).list();

		for (Employee rl : result) {
			System.out.println(rl.getId() + ", " + rl.getEmail() + ", " + rl.getSalary() + ", " + rl.getDept());

		}

	}

	@Test
	public void testFieldQuery() {

		String hql = "SELECT e.email, e.salary FROM Employees e WHERE e.dept = :dept";
		Query query = session.createQuery(hql);

		Department dept = new Department();
		dept.setId(80);
		List<Object[]> result = query.setEntity("dept", dept).list();

		for (Object[] rl : result) {
			System.out.println(Arrays.asList(rl));

		}

	}

	@Test
	public void testNamedQuery() {

		Query query = session.getNamedQuery("salaryEmps");
		List<Employee> emps = query.setFloat("minSal", 5000).setFloat("maxSal", 10000).list();

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
	public void testHQLUpdate() {
		String hql = "DELETE FROM Department d WHERE d.id = :id";

		session.createQuery(hql).setInteger("id", 280).executeUpdate();

	}

	@Test
	public void testNativeSQL() {
		String sql = "INSERT INTO gg_department VALUES(?,?)";
		Query query = session.createSQLQuery(sql);

		query.setInteger(0, 280).setString(1, "Tony").executeUpdate();

	}

	@Test
	public void testQBC4() {

		Criteria criteria = session.createCriteria(Employee.class);

		// 1. Add sorting
		criteria.addOrder(Order.asc("salary"));
		criteria.addOrder(Order.desc("email"));

		// 2. Add page method
		int pageSize = 5;
		int pageNo = 3;
		criteria.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();

	}

	@Test
	public void testQBC3() {

		Criteria criteria = session.createCriteria(Employee.class);

		// Static Query: Use Projection: Use static methods of "Projections"
		criteria.setProjection(Projections.max("salary"));

		System.out.println(criteria.uniqueResult());

	}

	@Test
	public void testQBC2() {
		Criteria criteria = session.createCriteria(Employee.class);

		// 1. AND: Use "Conjunction"
		// "Conjunction" is a "Criterion" object, and can add "Criterion" object
		// in it
		Conjunction conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.like("name", "a", MatchMode.ANYWHERE));
		Department dept = new Department();
		dept.setId(80);
		conjunction.add(Restrictions.eq("dept", dept));
		System.out.println(conjunction);

		// 2. OR
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.ge("salary", 6000F));
		disjunction.add(Restrictions.isNull("email"));

		criteria.add(disjunction);
		criteria.add(conjunction);

		criteria.list();

	}

	@Test
	public void testQBC() {

		// 1. Create a "Criteria" object
		Criteria crIterator = session.createCriteria(Employee.class);

		// 2. Add query requirement: In QBC, querying requirements are shown as
		// Criterion
		// "Criterion" can be gained by static methods of "Restrictions"
		crIterator.add(Restrictions.eq("email", "SKUMAR"));
		crIterator.add(Restrictions.gt("salary", 5000F));

		// 3. Execute Querying
		Employee employee = (Employee) crIterator.uniqueResult();
		System.out.println(employee);

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
