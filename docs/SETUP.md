# Setup Guide

Complete setup instructions for the Employee Management System.

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

## Step 1: Clone the Repository

Clone the repository:

```bash
git clone https://github.com/1DeepakSrinivas/SWD-Project.git
cd SWD-Project
```

### Step 1.1: Add Execute Permissions to the script:

```bash
chmod +x ./src/db/start-mysql.sh
```

Or extract the project to a directory of your choice.

## Step 2: Start MySQL Server

Use the provided script to start MySQL:

```bash
./src/db/start-mysql.sh
```

The script reads configuration from `.env` file. If MySQL is already running, it will detect and skip startup.

Alternative manual methods:

**macOS (Homebrew):**
```bash
brew services start mysql
```

**Windows:**
Start MySQL from Services or MySQL Workbench.

## Step 3: Create Database

Connect to MySQL and create the database:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS emp_mgmt;"
```

## Step 4: Configure Environment

Create `.env` file in project root:

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=emp_mgmt
DB_USER=root
DB_PASS=your_password
```

If password is empty, set `DB_PASS=`. Replace `your_password` with your MySQL root password.

## Step 5: Initialize Database

Run the database initialization class:

```bash
mvn compile exec:java -Dexec.mainClass="com.emp_mgmt.db.DatabaseInit"
```

This creates tables and loads sample data. Safe to run multiple times.

Alternative: Run SQL scripts manually:

```bash
mysql -u root -p emp_mgmt < src/db/schema.sql
mysql -u root -p emp_mgmt < src/db/sample-data.sql
```

## Step 6: Build Project

Compile the project:

```bash
mvn clean compile
```

Package the application:

```bash
mvn clean package
```

Output JAR: `target/employee-management-1.0.0.jar`

## Step 7: Test Connection

Verify database connection:

```bash
mvn exec:java -Dexec.mainClass="com.emp_mgmt.db.DatabaseConnectionManager"
```

Expected output:
```
Attempting to connect to database...
Connection established successfully.
Database connection test successful.
Number of tables in current database: 6
Connection closed.
```

## Step 8: Verify Database Setup

Check tables:

```bash
mysql -u root -p emp_mgmt -e "SHOW TABLES;"
```

Expected tables:
- division
- employees
- employee_division
- employee_job_titles
- job_titles
- payroll

Check sample data:

```bash
mysql -u root -p emp_mgmt -e "SELECT COUNT(*) AS employee_count FROM employees;"
```

Should show 15 employees if sample data was loaded.

## Database Configuration

Connection parameters are read from `.env` file:

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | MySQL server hostname |
| `DB_PORT` | `3306` | MySQL server port |
| `DB_NAME` | `emp_mgmt` | Database name |
| `DB_USER` | `root` | Database username |
| `DB_PASS` | (empty) | Database password |

If `.env` is missing or variables are unset, defaults are used.

## Database Initialization

The `DatabaseInit` class executes:
- `src/db/schema.sql` - Creates tables with fail-safe logic
- `src/db/sample-data.sql` - Loads sample data

Features:
- Checks if tables exist before creating
- Handles duplicate entries gracefully
- Uses transactions with rollback on failure

## Troubleshooting

**MySQL connection fails:**
- Verify MySQL is running: `./src/db/start-mysql.sh`
- Check `.env` credentials
- Test connection: `mysql -u root -p`

**Error: "Access denied for user 'root'@'localhost'"**
- Verify MySQL password in `.env` file
- Test MySQL connection: `mysql -u root -p`
- Ensure `DB_USER` and `DB_PASS` are correct

**Error: "Unknown database 'emp_mgmt'"**
- Create database: `mysql -u root -p -e "CREATE DATABASE emp_mgmt;"`
- Run initialization: `mvn exec:java -Dexec.mainClass="com.emp_mgmt.db.DatabaseInit"`

**Error: "No suitable driver found"**
- Ensure MySQL Connector/J dependency is in `pom.xml`
- Run `mvn clean compile` to download dependencies
- Check Maven dependencies: `mvn dependency:tree`

**Build errors:**
- Verify Java version: `java -version`
- Check Maven: `mvn -version`
- Set JAVA_HOME if needed: `export JAVA_HOME=$(/usr/libexec/java_home)`
- Clean and rebuild: `mvn clean compile`

**Database script errors:**
- "Table already exists" - Normal if re-running scripts, safe to ignore
- "Duplicate entry" - Sample data uses `INSERT IGNORE`, safe to ignore

