package com.tony.hibernate.entities;

import java.util.Set;

public class Department {
	private Integer id;
	private String name;
	private Set<Employee> emps;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Department [id=" + id + ", name=" + name + ", emps=" + emps + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Employee> getEmps() {
		return emps;
	}

	public void setEmps(Set<Employee> emps) {
		this.emps = emps;
	}

}
