package com.employeemgmt.dao;

import com.employeemgmt.model.Employee;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {
    Employee insert(Employee employee) throws SQLException;

    boolean update(Employee employee) throws SQLException;

    boolean delete(Integer employeeId) throws SQLException;

    Optional<Employee> findById(Integer employeeId) throws SQLException;

    List<Employee> findAll() throws SQLException;

    Optional<Employee> findBySSN(String ssn) throws SQLException;

    List<Employee> searchByName(String nameFragment) throws SQLException;

    int updateSalaryByPercentage(double percentage, BigDecimal min, BigDecimal max) throws SQLException;
}

