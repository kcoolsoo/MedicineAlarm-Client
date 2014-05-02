package com.medicinealarm.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.medicinealarm.dao.PatientDAO;
import com.medicinealarm.model.Patient;

public class PatientDaoImpl implements PatientDAO {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void insert(Patient patient) {

		String sql = "INSERT INTO Patient (password, name, age, addr_street, addr_city, addr_state, zip, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;

	//	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = conn.prepareStatement(sql);

			//ps.setString(1, passwordEncoder.encode(patient.getPassword()));
			//ps.setString(1, patient.getPassword());
			ps.setString(2, patient.getName());
			ps.setInt(3, patient.getAge());
			ps.setString(4, patient.getAddress_street());
			ps.setString(5, patient.getAddress_city());
			ps.setString(6, patient.getAddress_state());
			ps.setInt(7, patient.getZip());
			ps.setString(8, patient.getPhone());
		
			ps.executeUpdate();
			ps.close();
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
	
	public void updatePoints(int id, int points) {
		String sql = "UPDATE Patient SET points = ? WHERE id = ?";
		Connection conn = null;

		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setInt(1, points);
			ps.setInt(2, id);
		
			ps.executeUpdate();
			ps.close();
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

	public Patient findPatientById(int id) {

		String sql = "SELECT * FROM Patient WHERE id = ?";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			Patient patient = null;
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				patient = new Patient(rs.getInt("id"),
						rs.getString("password"), rs.getString("name"),
						rs.getInt("age"), rs.getString("addr_street"),
						rs.getString("addr_city"), rs.getString("addr_state"),
						rs.getInt("zip"), rs.getString("phone"),
						rs.getInt("points"));
			}
			rs.close();
			ps.close();

			return patient;
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
