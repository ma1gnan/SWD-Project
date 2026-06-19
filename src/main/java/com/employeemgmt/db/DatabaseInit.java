package com.employeemgmt.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes the database by executing schema and sample data SQL files.
 * Includes fail-safe logic to prevent errors if tables already exist.
 */
public class DatabaseInit {

    private static final String SCHEMA_FILE = "src/db/schema.sql";
    private static final String SAMPLE_DATA_FILE = "src/db/sample-data.sql";
    private final DatabaseConnectionManager dbManager;

    public DatabaseInit() {
        this.dbManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Initializes the database only if needed (tables don't exist or are empty).
     * This is safe to call on every application startup.
     * 
     * @return true if initialization was successful or not needed, false if there was an error
     */
    public boolean initializeIfNeeded() {
        Connection connection = null;
        try {
            connection = dbManager.getConnection();
            
            // Check if division table exists and has data
            if (tableExists(connection, "division")) {
                // Check if it has data
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM division")) {
                    if (rs.next() && rs.getInt("count") > 0) {
                        System.out.println("Database already initialized with data. Skipping initialization.");
                        return true;
                    }
                }
            }
            
            // If we get here, we need to initialize
            System.out.println("Database needs initialization. Starting...");
            return initialize();
            
        } catch (SQLException e) {
            System.err.println("Error checking database state: " + e.getMessage());
            e.printStackTrace();
            // Try to initialize anyway
            return initialize();
        } finally {
            if (connection != null) {
                dbManager.closeConnection(connection);
            }
        }
    }

    /**
     * Initializes the database by executing schema and sample data files.
     * 
     * @return true if initialization was successful, false otherwise
     */
    public boolean initialize() {
        Connection connection = null;
        try {
            connection = dbManager.getConnection();
            connection.setAutoCommit(false);

            System.out.println("Starting database initialization...");

            // Initialize schema
            if (!executeSchema(connection)) {
                connection.rollback();
                return false;
            }

            // Initialize sample data
            if (!executeSampleData(connection)) {
                connection.rollback();
                return false;
            }

            connection.commit();
            System.out.println("Database initialization completed successfully.");
            return true;

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Failed to rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (connection != null) {
                dbManager.closeConnection(connection);
            }
        }
    }

    /**
     * Executes the schema SQL file with fail-safe logic.
     * Checks if tables exist before creating them.
     * 
     * @param connection the database connection
     * @return true if successful, false otherwise
     */
    private boolean executeSchema(Connection connection) {
        try {
            System.out.println("Executing schema file: " + SCHEMA_FILE);
            Path schemaPath = Paths.get(System.getProperty("user.dir"), SCHEMA_FILE);

            if (!schemaPath.toFile().exists()) {
                System.err.println("Schema file not found: " + schemaPath);
                return false;
            }

            String sqlContent = readSqlFile(schemaPath);
            if (sqlContent == null || sqlContent.trim().isEmpty()) {
                System.err.println("Schema file is empty or could not be read.");
                return false;
            }

            // Split SQL statements by semicolon and execute each one
            String[] statements = sqlContent.split(";");
            
            try (Statement stmt = connection.createStatement()) {
                for (String statement : statements) {
                    statement = statement.trim();
                    if (statement.isEmpty() || statement.startsWith("--")) {
                        continue;
                    }

                    // Handle CREATE TABLE statements with fail-safe logic
                    if (statement.toUpperCase().startsWith("CREATE TABLE")) {
                        String tableName = extractTableName(statement);
                        if (tableName != null && tableExists(connection, tableName)) {
                            System.out.println("Table '" + tableName + "' already exists. Skipping creation.");
                            continue;
                        }
                    }

                    // Handle CREATE INDEX statements with fail-safe logic
                    if (statement.toUpperCase().startsWith("CREATE INDEX")) {
                        String[] indexInfo = extractIndexInfo(statement);
                        if (indexInfo != null && indexInfo.length == 2 && 
                            indexExists(connection, indexInfo[0], indexInfo[1])) {
                            System.out.println("Index '" + indexInfo[0] + "' on table '" + indexInfo[1] + "' already exists. Skipping creation.");
                            continue;
                        }
                    }

                    try {
                        stmt.execute(statement);
                    } catch (SQLException e) {
                        // If table/index already exists, log and continue
                        if (e.getSQLState().equals("42S01") || e.getMessage().contains("already exists")) {
                            System.out.println("Warning: " + e.getMessage() + " - Continuing...");
                            continue;
                        }
                        throw e;
                    }
                }
            }

            System.out.println("Schema execution completed.");
            return true;

        } catch (SQLException | IOException e) {
            System.err.println("Error executing schema: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Executes the sample data SQL file with fail-safe logic.
     * 
     * @param connection the database connection
     * @return true if successful, false otherwise
     */
    private boolean executeSampleData(Connection connection) {
        try {
            System.out.println("Executing sample data file: " + SAMPLE_DATA_FILE);
            Path dataPath = Paths.get(System.getProperty("user.dir"), SAMPLE_DATA_FILE);

            if (!dataPath.toFile().exists()) {
                System.err.println("Sample data file not found: " + dataPath);
                return false;
            }

            String sqlContent = readSqlFile(dataPath);
            if (sqlContent == null || sqlContent.trim().isEmpty()) {
                System.err.println("Sample data file is empty or could not be read.");
                return false;
            }

            // Split SQL statements by semicolon and execute each one
            String[] statements = sqlContent.split(";");
            
            try (Statement stmt = connection.createStatement()) {
                for (String statement : statements) {
                    statement = statement.trim();
                    if (statement.isEmpty() || statement.startsWith("--")) {
                        continue;
                    }

                    try {
                        stmt.execute(statement);
                    } catch (SQLException e) {
                        // Log warning but continue for duplicate key errors (expected with INSERT IGNORE)
                        if (e.getSQLState().equals("23000") || e.getMessage().contains("Duplicate entry")) {
                            System.out.println("Warning: Duplicate entry (expected with INSERT IGNORE) - Continuing...");
                            continue;
                        }
                        // For other errors, log and continue (fail-safe)
                        System.out.println("Warning: " + e.getMessage() + " - Continuing...");
                        continue;
                    }
                }
            }

            System.out.println("Sample data execution completed.");
            return true;

        } catch (SQLException | IOException e) {
            System.err.println("Error executing sample data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads the content of a SQL file.
     * 
     * @param filePath the path to the SQL file
     * @return the file content as a string, or null if an error occurs
     */
    private String readSqlFile(Path filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Extracts the table name from a CREATE TABLE statement.
     * 
     * @param createStatement the CREATE TABLE SQL statement
     * @return the table name, or null if not found
     */
    private String extractTableName(String createStatement) {
        try {
            // Pattern: CREATE TABLE table_name or CREATE TABLE IF NOT EXISTS table_name
            String upper = createStatement.toUpperCase().trim();
            int tableIndex = upper.indexOf("TABLE");
            if (tableIndex == -1) {
                return null;
            }

            String afterTable = createStatement.substring(tableIndex + 5).trim();
            // Skip "IF NOT EXISTS" if present
            if (afterTable.toUpperCase().startsWith("IF NOT EXISTS")) {
                afterTable = afterTable.substring(13).trim();
            }

            // Extract table name (until space, parenthesis, or end)
            int endIndex = afterTable.length();
            for (int i = 0; i < afterTable.length(); i++) {
                char c = afterTable.charAt(i);
                if (c == ' ' || c == '(') {
                    endIndex = i;
                    break;
                }
            }
            return afterTable.substring(0, endIndex).trim();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts the index name and table name from a CREATE INDEX statement.
     * 
     * @param createStatement the CREATE INDEX SQL statement
     * @return an array with [indexName, tableName], or null if not found
     */
    private String[] extractIndexInfo(String createStatement) {
        try {
            // Pattern: CREATE INDEX index_name ON table_name
            String upper = createStatement.toUpperCase().trim();
            int indexIndex = upper.indexOf("INDEX");
            if (indexIndex == -1) {
                return null;
            }

            String afterIndex = createStatement.substring(indexIndex + 5).trim();
            // Extract index name (until space or ON keyword)
            int onIndex = -1;
            for (int i = 0; i <= afterIndex.length() - 2; i++) {
                if (afterIndex.substring(i, i + 2).toUpperCase().equals("ON")) {
                    onIndex = i;
                    break;
                }
            }
            
            if (onIndex == -1) {
                return null;
            }

            String indexName = afterIndex.substring(0, onIndex).trim();
            String afterOn = afterIndex.substring(onIndex + 2).trim();
            
            // Extract table name (until space, parenthesis, or end)
            int endIndex = afterOn.length();
            for (int i = 0; i < afterOn.length(); i++) {
                char c = afterOn.charAt(i);
                if (c == ' ' || c == '(') {
                    endIndex = i;
                    break;
                }
            }
            String tableName = afterOn.substring(0, endIndex).trim();
            
            return new String[]{indexName, tableName};
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Checks if a table exists in the database.
     * 
     * @param connection the database connection
     * @param tableName the name of the table to check
     * @return true if the table exists, false otherwise
     */
    private boolean tableExists(Connection connection, String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Checks if an index exists on a specific table in the database.
     * 
     * @param connection the database connection
     * @param indexName the name of the index to check
     * @param tableName the name of the table
     * @return true if the index exists, false otherwise
     */
    private boolean indexExists(Connection connection, String indexName, String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getIndexInfo(null, null, tableName, false, false)) {
                while (rs.next()) {
                    String rsIndexName = rs.getString("INDEX_NAME");
                    if (indexName.equalsIgnoreCase(rsIndexName)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Main method for testing database initialization.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        DatabaseInit initializer = new DatabaseInit();
        boolean success = initializer.initialize();
        System.exit(success ? 0 : 1);
    }
}

