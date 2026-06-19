package com.employeemgmt.dao;

import com.employeemgmt.model.EmployeeJobTitle;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeJobTitleDAO {
    EmployeeJobTitle insert(EmployeeJobTitle employeeJobTitle) throws SQLException;

    boolean delete(Integer employeeId, Integer jobTitleId) throws SQLException;

    Optional<EmployeeJobTitle> findById(Integer employeeId, Integer jobTitleId) throws SQLException;

    List<EmployeeJobTitle> findAll() throws SQLException;

    List<EmployeeJobTitle> findByEmployeeId(Integer employeeId) throws SQLException;

    List<EmployeeJobTitle> findByJobTitleId(Integer jobTitleId) throws SQLException;
}

