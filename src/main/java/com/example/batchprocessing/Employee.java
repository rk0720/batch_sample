package com.example.batchprocessing;

public class Employee {
	
	private String name;
	private String department;
	
	public Employee() {
		
	}
	
	public Employee(String name, String department) {
		this.name = name;
		this.department = department;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getDepartment() {
		return this.department;
	}
	
	@Override
	public String toString() {
		return "name: " + this.name + " department: " + this.department;
	}

}
