package com.employeemgmt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.employeemgmt.db.DatabaseConnectionManager;
import com.employeemgmt.model.EmployeeDivision;

public class EmployeeDivisionDAOImpl implements EmployeeDivisionDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    @Override
    public EmployeeDivision insert(EmployeeDivision employeeDivision) throws SQLException {
        String sql = SQLConstants.EmployeeDivision.INSERT;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeDivision.getEmployeeId());
            ps.setInt(2, employeeDivision.getDivisionId());
            ps.executeUpdate();
        }

        return employeeDivision;
    }

    @Override
    public boolean delete(Integer employeeId, Integer divisionId) throws SQLException {
        String sql = SQLConstants.EmployeeDivision.DELETE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, divisionId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<EmployeeDivision> findById(Integer employeeId, Integer divisionId) throws SQLException {
        String sql = SQLConstants.EmployeeDivision.FIND_BY_ID;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, divisionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployeeDivision(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<EmployeeDivision> findAll() throws SQLException {
        String sql = SQLConstants.EmployeeDivision.FIND_ALL;
        List<EmployeeDivision> employeeDivisions = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employeeDivisions.add(mapResultSetToEmployeeDivision(rs));
            }
        }

        return employeeDivisions;
    }

    @Override
    public List<EmployeeDivision> findByEmployeeId(Integer employeeId) throws SQLException {
        String sql = SQLConstants.EmployeeDivision.FIND_BY_EMPLOYEE_ID;
        List<EmployeeDivision> employeeDivisions = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employeeDivisions.add(mapResultSetToEmployeeDivision(rs));
                }
            }
        }

        return employeeDivisions;
    }

    @Override
    public List<EmployeeDivision> findByDivisionId(Integer divisionId) throws SQLException {
        String sql = SQLConstants.EmployeeDivision.FIND_BY_DIVISION_ID;
        List<EmployeeDivision> employeeDivisions = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, divisionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employeeDivisions.add(mapResultSetToEmployeeDivision(rs));
                }
            }
        }

        return employeeDivisions;
    }

    private EmployeeDivision mapResultSetToEmployeeDivision(ResultSet rs) throws SQLException {
        EmployeeDivision employeeDivision = new EmployeeDivision();
        employeeDivision.setEmployeeId(rs.getInt("employee_id"));
        employeeDivision.setDivisionId(rs.getInt("division_id"));
        return employeeDivision;
    }
}

