package com.employeemgmt.dao;

import com.employeemgmt.model.Payroll;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PayrollDAO {
    Payroll insert(Payroll payroll) throws SQLException;

    boolean update(Payroll payroll) throws SQLException;

    boolean delete(Integer payrollId) throws SQLException;

    Optional<Payroll> findById(Integer payrollId) throws SQLException;

    List<Payroll> findAll() throws SQLException;

    List<Payroll> findByEmployeeId(Integer employeeId) throws SQLException;

    List<Payroll> findByAmountRange(BigDecimal min, BigDecimal max) throws SQLException;
}

