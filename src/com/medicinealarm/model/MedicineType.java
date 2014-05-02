package com.medicinealarm.model;

public class MedicineType {

	int id;
	String type;
	
	public MedicineType() {
		super();
	}

	public MedicineType(int id, String type) {
		super();
		this.id = id;
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}


