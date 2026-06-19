package com.employeemgmt.dao;

import com.employeemgmt.model.Division;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DivisionDAO {
    Division insert(Division division) throws SQLException;

    boolean update(Division division) throws SQLException;

    boolean delete(Integer divisionId) throws SQLException;

    Optional<Division> findById(Integer divisionId) throws SQLException;

    List<Division> findAll() throws SQLException;
}

