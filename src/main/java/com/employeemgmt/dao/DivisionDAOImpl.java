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
import com.employeemgmt.model.Division;

public class DivisionDAOImpl implements DivisionDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    @Override
    public Division insert(Division division) throws SQLException {
        String sql = SQLConstants.Division.INSERT;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, division.getName());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    division.setDivisionId(rs.getInt(1));
                }
            }
        }

        return division;
    }

    @Override
    public boolean update(Division division) throws SQLException {
        String sql = SQLConstants.Division.UPDATE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, division.getName());
            ps.setInt(2, division.getDivisionId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer divisionId) throws SQLException {
        String sql = SQLConstants.Division.DELETE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, divisionId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Division> findById(Integer divisionId) throws SQLException {
        String sql = SQLConstants.Division.FIND_BY_ID;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, divisionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDivision(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Division> findAll() throws SQLException {
        String sql = SQLConstants.Division.FIND_ALL;
        List<Division> divisions = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                divisions.add(mapResultSetToDivision(rs));
            }
        }

        return divisions;
    }

    private Division mapResultSetToDivision(ResultSet rs) throws SQLException {
        Division division = new Division();
        division.setDivisionId(rs.getInt("division_id"));
        division.setName(rs.getString("name"));
        return division;
    }
}

