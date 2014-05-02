package com.medicinealarm.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.medicinealarm.dao.MedicineTypeDAO;
import com.medicinealarm.model.MedicineType;

public class MedicineTypeDaoImpl implements MedicineTypeDAO {
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public void insert(MedicineType medicineType) {
		
		String sql = "INSERT INTO MedicineType (type) VALUES (?)";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setString(1, medicineType.getType());
			
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public MedicineType findMedicineTypeById(int id) {
		
		String sql = "SELECT * FROM MedicineType WHERE id = ?";		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			MedicineType medicineType = null;
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				medicineType = new MedicineType(rs.getInt("id"),
									rs.getString("type"));
			}
			rs.close();
			ps.close();
			
			return medicineType;
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
