package com.medicinealarm.dao;

import com.medicinealarm.model.Patient;

public interface PatientDAO {

	public void insert(Patient patient);
	public void updatePoints(int id, int points);
	public Patient findPatientById(int id);
	
}
