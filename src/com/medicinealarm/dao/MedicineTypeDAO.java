package com.medicinealarm.dao;

import com.medicinealarm.model.MedicineType;

public interface MedicineTypeDAO {

	public void insert(MedicineType medicineType);
	public MedicineType findMedicineTypeById(int id);
	
}
