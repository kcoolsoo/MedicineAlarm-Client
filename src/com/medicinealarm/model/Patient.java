package com.medicinealarm.model;

public class Patient {
	
	int id;
	String password;
	String name;
	int age;
	String address_street;
	String address_city;
	String address_state;
	int zip;
	String phone;
	int points;
	
	public Patient() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Patient(int id, String password, String name, int age,
			String address_street, String address_city, String address_state,
			int zip, String phone, int points) {
		super();
		this.id = id;
		this.password = password;
		this.name = name;
		this.age = age;
		this.address_street = address_street;
		this.address_city = address_city;
		this.address_state = address_state;
		this.zip = zip;
		this.phone = phone;
		this.points = points;
	}
	/* Getters and Setters */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getAddress_street() {
		return address_street;
	}
	public void setAddress_street(String address_street) {
		this.address_street = address_street;
	}
	public String getAddress_city() {
		return address_city;
	}
	public void setAddress_city(String address_city) {
		this.address_city = address_city;
	}
	public String getAddress_state() {
		return address_state;
	}
	public void setAddress_state(String address_state) {
		this.address_state = address_state;
	}
	public int getZip() {
		return zip;
	}
	public void setZip(int zip) {
		this.zip = zip;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	
}
