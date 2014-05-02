package com.medicinealarm.model;

public class MedicineTimeList implements Comparable<MedicineTimeList>{

	String type;
	String time;
		
	public MedicineTimeList(String type, String time) {
		super();
		this.type = type;
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public int compareTo(MedicineTimeList another) {
		if (this.time.compareTo(another.time) > 0)
			return 1;
		else if (this.time.compareTo(another.time) < 0)
			return -1;
		return 0;
	}

}
