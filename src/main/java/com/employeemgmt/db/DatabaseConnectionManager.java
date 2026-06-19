package com.employeemgmt.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for managing database connections.
 * Reads connection parameters from .env file in project root.
 */
public class DatabaseConnectionManager {

    private static final String ENV_FILE = ".env";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB_NAME = "emp_mgmt";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private static DatabaseConnectionManager instance;
    private final String jdbcUrl;
    private final String username;
    private final String password;

    private DatabaseConnectionManager() {
        Map<String, String> envVars = loadEnvFile();
        String host = envVars.getOrDefault("DB_HOST", DEFAULT_HOST);
        String port = envVars.getOrDefault("DB_PORT", DEFAULT_PORT);
        String dbName = envVars.getOrDefault("DB_NAME", DEFAULT_DB_NAME);
        this.username = envVars.getOrDefault("DB_USER", DEFAULT_USER);
        this.password = envVars.getOrDefault("DB_PASS", DEFAULT_PASS);

        this.jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true", 
                host, port, dbName);
    }

    /**
     * Gets the singleton instance of DatabaseConnectionManager.
     *
     * @return the singleton instance
     */
    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    /**
     * Gets a new database connection with retry logic.
     *
     * @return a Connection object
     * @throws SQLException if connection cannot be established after retries
     */
    public Connection getConnection() throws SQLException {
        SQLException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                if (isConnectionValid(connection)) {
                    return connection;
                } else {
                    closeConnection(connection);
                    throw new SQLException("Connection validation failed");
                }
            } catch (SQLException e) {
                lastException = e;
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Connection retry interrupted", ie);
                    }
                }
            }
        }

        throw new SQLException("Failed to establish connection after " + MAX_RETRY_ATTEMPTS + " attempts", 
                lastException);
    }

    /**
     * Validates that a connection is still open and usable.
     *
     * @param connection the connection to validate
     * @return true if connection is valid, false otherwise
     */
    public boolean isConnectionValid(Connection connection) {
        if (connection == null) {
            return false;
        }

        try {
            return !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes a connection safely, handling any exceptions.
     *
     * @param connection the connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Log error if logging is available
            }
        }
    }

    /**
     * Gets the JDBC URL being used for connections.
     *
     * @return the JDBC URL string
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    private Map<String, String> loadEnvFile() {
        Map<String, String> envVars = new HashMap<>();
        Path envPath = Paths.get(System.getProperty("user.dir"), ENV_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(envPath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    if (!value.isEmpty()) {
                        envVars.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            // .env file not found or unreadable, use defaults
        }

        return envVars;
    }

    /**
     * Test main method to verify database connection.
     * Queries information_schema.tables to confirm connectivity.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();
        Connection connection = null;

        try {
            System.out.println("Attempting to connect to database...");
            System.out.println("JDBC URL: " + dbManager.getJdbcUrl());
            connection = dbManager.getConnection();
            System.out.println("Connection established successfully.");

            String query = "SELECT COUNT(*) AS table_count FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE()";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                if (rs.next()) {
                    int tableCount = rs.getInt("table_count");
                    System.out.println("Database connection test successful.");
                    System.out.println("Number of tables in current database: " + tableCount);
                }
            }

        } catch (SQLException e) {
            System.err.println("Database connection test failed:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                dbManager.closeConnection(connection);
                System.out.println("Connection closed.");
            }
        }
    }
}

