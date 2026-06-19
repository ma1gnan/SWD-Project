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
import com.employeemgmt.model.JobTitle;

public class JobTitleDAOImpl implements JobTitleDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    @Override
    public JobTitle insert(JobTitle jobTitle) throws SQLException {
        String sql = SQLConstants.JobTitle.INSERT;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, jobTitle.getTitle());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    jobTitle.setJobTitleId(rs.getInt(1));
                }
            }
        }

        return jobTitle;
    }

    @Override
    public boolean update(JobTitle jobTitle) throws SQLException {
        String sql = SQLConstants.JobTitle.UPDATE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jobTitle.getTitle());
            ps.setInt(2, jobTitle.getJobTitleId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer jobTitleId) throws SQLException {
        String sql = SQLConstants.JobTitle.DELETE;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobTitleId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<JobTitle> findById(Integer jobTitleId) throws SQLException {
        String sql = SQLConstants.JobTitle.FIND_BY_ID;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobTitleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJobTitle(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<JobTitle> findAll() throws SQLException {
        String sql = SQLConstants.JobTitle.FIND_ALL;
        List<JobTitle> jobTitles = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                jobTitles.add(mapResultSetToJobTitle(rs));
            }
        }

        return jobTitles;
    }

    private JobTitle mapResultSetToJobTitle(ResultSet rs) throws SQLException {
        JobTitle jobTitle = new JobTitle();
        jobTitle.setJobTitleId(rs.getInt("job_title_id"));
        jobTitle.setTitle(rs.getString("title"));
        return jobTitle;
    }
}

