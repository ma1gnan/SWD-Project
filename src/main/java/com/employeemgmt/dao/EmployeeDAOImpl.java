package com.employeemgmt.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.employeemgmt.db.DatabaseConnectionManager;
import com.employeemgmt.model.Employee;

public class EmployeeDAOImpl implements EmployeeDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    @Override
    public Employee insert(Employee employee) throws SQLException {
        String sql = SQLConstants.Employee.INSERT;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getSsn());
            ps.setString(4, employee.getEmail());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    employee.setEmployeeId(rs.getInt(1));
                }
            }
        }

        return employee;
    }

    @Override
    public boolean update(Employee employee) throws SQLException {
        String sql = SQLConstants.Employee.UPDATE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getSsn());
            ps.setString(4, employee.getEmail());
            ps.setInt(5, employee.getEmployeeId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer employeeId) throws SQLException {
        String sql = SQLConstants.Employee.DELETE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Employee> findById(Integer employeeId) throws SQLException {
        String sql = SQLConstants.Employee.FIND_BY_ID;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Employee> findAll() throws SQLException {
        String sql = SQLConstants.Employee.FIND_ALL;
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        }

        return employees;
    }

    @Override
    public Optional<Employee> findBySSN(String ssn) throws SQLException {
        String sql = SQLConstants.Employee.FIND_BY_SSN;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ssn);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Employee> searchByName(String nameFragment) throws SQLException {
        String sql = SQLConstants.Employee.SEARCH_BY_NAME;
        List<Employee> employees = new ArrayList<>();
        String searchPattern = "%" + nameFragment + "%";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        }

        return employees;
    }

    @Override
    public int updateSalaryByPercentage(double percentage, BigDecimal min, BigDecimal max) throws SQLException {
        String sql = SQLConstants.Employee.UPDATE_SALARY_BY_PERCENTAGE;

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, percentage);
                ps.setBigDecimal(2, min);
                ps.setBigDecimal(3, max);

                int rowsAffected = ps.executeUpdate();
                conn.commit();
                return rowsAffected;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setSsn(rs.getString("SSN"));
        employee.setEmail(rs.getString("email"));
        return employee;
    }
}

