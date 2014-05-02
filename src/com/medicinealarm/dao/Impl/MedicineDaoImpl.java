package com.medicinealarm.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import com.medicinealarm.dao.MedicineDAO;
import com.medicinealarm.model.Medicine;

public class MedicineDaoImpl implements MedicineDAO {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public void insert(Medicine medicine) {
		
	}

	@Override
	public ArrayList<Medicine> findMedicineListByPatientId(int id) {
		
		String sql = "SELECT * FROM Medicine WHERE patient_id = ?";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ArrayList<Medicine> medicineList = new ArrayList<Medicine>();
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				Medicine medicine = new Medicine(rs.getInt("patient_id"),
												rs.getInt("type_id"),
												rs.getTime("time_to_take"));
				medicineList.add(medicine);
			}
			rs.close();
			ps.close();
			
			return medicineList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
	}

}
