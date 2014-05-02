package com.medicinealarm.model;

import java.util.List;

public class MedicineTypeList {
	
	List<MedicineType> medicineTypeList;

	public List<MedicineType> getMedicineList() {
		return medicineTypeList;
	}

	public void setMedicineList(List<MedicineType> medicineTypeList) {
		this.medicineTypeList = medicineTypeList;
	}
	
	public void add(MedicineType medicineType) {
		this.medicineTypeList.add(medicineType);
	}
	
}