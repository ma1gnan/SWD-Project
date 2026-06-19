package com.employeemgmt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.employeemgmt.db.DatabaseConnectionManager;
import com.employeemgmt.model.Payroll;

public class PayrollDAOImpl implements PayrollDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    @Override
    public Payroll insert(Payroll payroll) throws SQLException {
        String sql = SQLConstants.Payroll.INSERT;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payroll.getEmployeeId());
            ps.setBigDecimal(2, payroll.getAmount());
            ps.setDate(3, java.sql.Date.valueOf(payroll.getPayPeriodStart()));
            ps.setDate(4, java.sql.Date.valueOf(payroll.getPayPeriodEnd()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    payroll.setPayrollId(rs.getInt(1));
                }
            }
        }

        return payroll;
    }

    @Override
    public boolean update(Payroll payroll) throws SQLException {
        String sql = SQLConstants.Payroll.UPDATE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payroll.getEmployeeId());
            ps.setBigDecimal(2, payroll.getAmount());
            ps.setDate(3, java.sql.Date.valueOf(payroll.getPayPeriodStart()));
            ps.setDate(4, java.sql.Date.valueOf(payroll.getPayPeriodEnd()));
            ps.setInt(5, payroll.getPayrollId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer payrollId) throws SQLException {
        String sql = SQLConstants.Payroll.DELETE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payrollId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Payroll> findById(Integer payrollId) throws SQLException {
        String sql = SQLConstants.Payroll.FIND_BY_ID;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payrollId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayroll(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Payroll> findAll() throws SQLException {
        String sql = SQLConstants.Payroll.FIND_ALL;
        List<Payroll> payrolls = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                payrolls.add(mapResultSetToPayroll(rs));
            }
        }

        return payrolls;
    }

    @Override
    public List<Payroll> findByEmployeeId(Integer employeeId) throws SQLException {
        String sql = SQLConstants.Payroll.FIND_BY_EMPLOYEE_ID;
        List<Payroll> payrolls = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payrolls.add(mapResultSetToPayroll(rs));
                }
            }
        }

        return payrolls;
    }

    @Override
    public List<Payroll> findByAmountRange(java.math.BigDecimal min, java.math.BigDecimal max) throws SQLException {
        String sql = SQLConstants.Payroll.FIND_BY_AMOUNT_RANGE;
        List<Payroll> payrolls = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payrolls.add(mapResultSetToPayroll(rs));
                }
            }
        }

        return payrolls;
    }

    private Payroll mapResultSetToPayroll(ResultSet rs) throws SQLException {
        Payroll payroll = new Payroll();
        payroll.setPayrollId(rs.getInt("payroll_id"));
        payroll.setEmployeeId(rs.getInt("employee_id"));
        payroll.setAmount(rs.getBigDecimal("amount"));
        
        java.sql.Date startDate = rs.getDate("pay_period_start");
        if (startDate != null) {
            payroll.setPayPeriodStart(startDate.toLocalDate());
        }
        
        java.sql.Date endDate = rs.getDate("pay_period_end");
        if (endDate != null) {
            payroll.setPayPeriodEnd(endDate.toLocalDate());
        }
        
        return payroll;
    }
}

