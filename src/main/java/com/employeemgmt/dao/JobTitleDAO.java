package com.employeemgmt.dao;

import com.employeemgmt.model.JobTitle;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface JobTitleDAO {
    JobTitle insert(JobTitle jobTitle) throws SQLException;

    boolean update(JobTitle jobTitle) throws SQLException;

    boolean delete(Integer jobTitleId) throws SQLException;

    Optional<JobTitle> findById(Integer jobTitleId) throws SQLException;

    List<JobTitle> findAll() throws SQLException;
}

