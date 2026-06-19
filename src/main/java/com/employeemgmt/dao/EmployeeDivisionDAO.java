package com.employeemgmt.dao;

import com.employeemgmt.model.EmployeeDivision;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeDivisionDAO {
    EmployeeDivision insert(EmployeeDivision employeeDivision) throws SQLException;

    boolean delete(Integer employeeId, Integer divisionId) throws SQLException;

    Optional<EmployeeDivision> findById(Integer employeeId, Integer divisionId) throws SQLException;

    List<EmployeeDivision> findAll() throws SQLException;

    List<EmployeeDivision> findByEmployeeId(Integer employeeId) throws SQLException;

    List<EmployeeDivision> findByDivisionId(Integer divisionId) throws SQLException;
}

