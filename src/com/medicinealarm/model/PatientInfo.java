package com.medicinealarm.model;

import java.sql.Time;
import java.util.ArrayList;

public class PatientInfo {
	
	int id;
	String name;
	int points;
	ArrayList<String> typeList = new ArrayList<String>();
	ArrayList<Time> timeList = new ArrayList<Time>();
	
//	public PatientInfo(int id, String name, int points,
//			ArrayList<String> typeList, ArrayList<Time> timeList) {
//		super();
//		this.id = id;
//		this.name = name;
//		this.points = points;
//		this.typeList = typeList;
//		this.timeList = timeList;
//	}

	public PatientInfo() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public ArrayList<String> getTypeList() {
		return typeList;
	}

	public void setTypeList(ArrayList<String> typeList) {
		this.typeList = typeList;
	}

	public ArrayList<Time> getTimeList() {
		return timeList;
	}

	public void setTimeList(ArrayList<Time> timeList) {
		this.timeList = timeList;
	}
	
}
