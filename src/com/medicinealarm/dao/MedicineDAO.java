package com.medicinealarm.dao;

import java.util.ArrayList;

import com.medicinealarm.model.Medicine;

public interface MedicineDAO {

	public void insert(Medicine medicine);
	public ArrayList<Medicine> findMedicineListByPatientId(int id);
	
}
