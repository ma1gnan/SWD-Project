package com.employeemgmt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.employeemgmt.db.DatabaseConnectionManager;
import com.employeemgmt.model.EmployeeJobTitle;

public class EmployeeJobTitleDAOImpl implements EmployeeJobTitleDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    @Override
    public EmployeeJobTitle insert(EmployeeJobTitle employeeJobTitle) throws SQLException {
        String sql = SQLConstants.EmployeeJobTitle.INSERT;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeJobTitle.getEmployeeId());
            ps.setInt(2, employeeJobTitle.getJobTitleId());
            ps.executeUpdate();
        }

        return employeeJobTitle;
    }

    @Override
    public boolean delete(Integer employeeId, Integer jobTitleId) throws SQLException {
        String sql = SQLConstants.EmployeeJobTitle.DELETE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, jobTitleId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<EmployeeJobTitle> findById(Integer employeeId, Integer jobTitleId) throws SQLException {
        String sql = SQLConstants.EmployeeJobTitle.FIND_BY_ID;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, jobTitleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployeeJobTitle(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<EmployeeJobTitle> findAll() throws SQLException {
        String sql = SQLConstants.EmployeeJobTitle.FIND_ALL;
        List<EmployeeJobTitle> employeeJobTitles = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employeeJobTitles.add(mapResultSetToEmployeeJobTitle(rs));
            }
        }

        return employeeJobTitles;
    }

    @Override
    public List<EmployeeJobTitle> findByEmployeeId(Integer employeeId) throws SQLException {
        String sql = SQLConstants.EmployeeJobTitle.FIND_BY_EMPLOYEE_ID;
        List<EmployeeJobTitle> employeeJobTitles = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employeeJobTitles.add(mapResultSetToEmployeeJobTitle(rs));
                }
            }
        }

        return employeeJobTitles;
    }

    @Override
    public List<EmployeeJobTitle> findByJobTitleId(Integer jobTitleId) throws SQLException {
        String sql = SQLConstants.EmployeeJobTitle.FIND_BY_JOB_TITLE_ID;
        List<EmployeeJobTitle> employeeJobTitles = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobTitleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employeeJobTitles.add(mapResultSetToEmployeeJobTitle(rs));
                }
            }
        }

        return employeeJobTitles;
    }

    private EmployeeJobTitle mapResultSetToEmployeeJobTitle(ResultSet rs) throws SQLException {
        EmployeeJobTitle employeeJobTitle = new EmployeeJobTitle();
        employeeJobTitle.setEmployeeId(rs.getInt("employee_id"));
        employeeJobTitle.setJobTitleId(rs.getInt("job_title_id"));
        return employeeJobTitle;
    }
}

