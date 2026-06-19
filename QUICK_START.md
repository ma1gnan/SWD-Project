# Quick Start Guide - Employee Management System

## Prerequisites

1. **MySQL Server** installed and running
2. **Java 17+** installed
3. **Maven** installed
4. (Optional) Homebrew or equivalent package manager if you want to use the provided automation scripts

## 1. Install Dependencies

### macOS (automated)

```bash
# Install Java (Temurin 21)
./deps/macos/install-java.sh

# Install MySQL Community Server
./deps/macos/install-mysql.sh
```

Scripts are idempotent and install Homebrew if missing. Restart your terminal after running.

### Windows (automated)

Run as Administrator:

```powershell
# Install Java (Temurin 21)
.\deps\windows\install-java.ps1

# Install MySQL Community Server
.\deps\windows\install-mysql.ps1
```

Uses `winget` to install dependencies. Scripts guide you to manual downloads if `winget` is unavailable.

### Manual Installation (if scripts fail)

1. **Java 17+**: [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. **Maven**: [Apache Maven](https://maven.apache.org/download.cgi)
3. **MySQL**: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)

Verify:

```bash
java -version    # Java 17+
mvn -version     # Maven 3.6+
mysql --version  # MySQL 8.0+
```

---

## 2. Configure Database

Copy `.env.example` and set your MySQL password:

```bash
cp .env.example .env
```

Edit `.env`:

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=emp_mgmt
DB_USER=root
DB_PASS=your_mysql_password
```

---

## 3. Start MySQL & Initialize Database

```bash
./src/db/start-mysql.sh --init
```

This script:
- Checks if MySQL is running (starts if needed)
- Verifies connection using `.env` credentials
- Creates `emp_mgmt` database
- Executes schema (tables, indexes, constraints)
- Loads sample data (5 divisions, 15 job titles, 15 employees, payroll records)

### Manual Failsafe

If the script fails, run the following commands:

#### MacOSX
```bash
# Start MySQL
brew services start mysql                    
```

#### Windows
```bash
net start MySQL80                            
```

#### Initialize the database
```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS emp_mgmt;"
mysql -u root -p emp_mgmt < src/db/schema.sql
mysql -u root -p emp_mgmt < src/db/sample-data.sql
```

## 4. Run the Application

```bash
mvn clean javafx:run
```

The JavaFX UI will launch. If the database was not initialized in step 3, the app auto-initializes on first run.

### Manual Failsafe

```bash
# Compile first
mvn clean compile

# Run with explicit JavaFX module path
mvn javafx:run
```

---

## 5. Run Tests

```bash
mvn test
```

### Manual Failsafe

```bash
# Clean and recompile
mvn clean compile

# Run with verbose output
mvn test -X

# Run specific test class
mvn test -Dtest=EmployeeDAOTest
```

---

## What You'll See

**Main Menu:**
- Search Employees (by name, SSN, or ID)
- Add/Edit Employee (create or modify records)
- Reports (payroll by division and job title)
- Salary Adjustment (bulk updates by range)

**Sample Data:**
- 5 divisions (Engineering, Sales, Marketing, HR, Finance)
- 15 job titles (Software Engineer, Manager, Analyst, etc.)
- 15 employees with complete records
- Payroll records spanning 2024-2026

---

## Troubleshooting

### Database Connection Errors

```bash
# Check MySQL status and connection
./src/db/start-mysql.sh

# Test connection manually
mysql -h localhost -P 3306 -u root -p

# Verify .env credentials
cat .env
```

### Empty Dropdowns

```bash
# Check if data exists
mysql -u root -p emp_mgmt -e "SELECT * FROM division;"

# Reload sample data
mysql -u root -p emp_mgmt < src/db/sample-data.sql
```

### JavaFX Runtime Missing

Always use Maven:

```bash
mvn clean javafx:run  # Correct
```

Do not use:

```bash
java com.employeemgmt.ui.App  # Will fail
```

---

## Additional Documentation

- `README.md` - Project overview and goals
- `src/db/README.md` - Complete database setup and management
- `docs/swdd/swdd-docs.md` - Software and System Design Document
- `deps/` - Dependency installation scripts (macOS and Windows)
