# Database Setup and Initialization

This directory contains all database-related files for the Employee Management System.

## Files

- **`schema.sql`** - Database schema with table definitions
- **`sample-data.sql`** - Sample data for testing and development
- **`start-mysql.sh`** - Script to start MySQL server and optionally initialize the database

## Quick Start

### Option 1: Automatic Initialization (Recommended)

The application automatically initializes the database on first run. Simply start the application:

```bash
mvn clean javafx:run
```

The app will:
1. Check if the database is initialized
2. Create tables if they don't exist
3. Load sample data if tables are empty
4. Start the UI

### Option 2: Manual Initialization via Script

Start MySQL and initialize the database in one command:

```bash
./src/db/start-mysql.sh --init
```

This will:
1. Start the MySQL server (if not already running)
2. Create the `emp_mgmt` database if it doesn't exist
3. Execute `schema.sql` to create tables
4. Execute `sample-data.sql` to load sample data

### Option 3: Manual SQL Execution

If you prefer to run SQL files manually:

```bash
# Start MySQL server
./src/db/start-mysql.sh

# Load schema
mysql -h localhost -P 3306 -u root -p emp_mgmt < src/db/schema.sql

# Load sample data
mysql -h localhost -P 3306 -u root -p emp_mgmt < src/db/sample-data.sql
```

## Database Configuration

Database connection settings are stored in `.env` file in the project root:

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=emp_mgmt
DB_USER=root
DB_PASS=your_password
```

Copy `.env.example` to `.env` and update with your MySQL credentials.

## Sample Data

The `sample-data.sql` file includes:

- **5 Divisions**: Engineering, Sales, Marketing, Human Resources, Finance
- **15 Job Titles**: Various roles across departments
- **15 Employees**: Sample employee records
- **Employee-Division Assignments**: Each employee assigned to one division
- **Employee-Job Title Assignments**: Each employee assigned to one job title
- **Payroll Records**: Multiple pay periods for all employees (2024-2026)

All INSERT statements use `INSERT IGNORE` or `ON DUPLICATE KEY UPDATE` to safely re-run the file.

## Schema Overview

### Tables

1. **`division`** - Company divisions/departments
2. **`job_titles`** - Available job titles
3. **`employees`** - Employee master data
4. **`employee_division`** - Links employees to divisions (one-to-one)
5. **`employee_job_titles`** - Links employees to job titles (one-to-one)
6. **`payroll`** - Payroll records for employees

### Key Constraints

- Each employee must have exactly one division (enforced by UNIQUE constraint)
- Each employee must have exactly one job title (enforced by UNIQUE constraint)
- SSN must be unique across all employees
- Foreign keys enforce referential integrity with CASCADE delete

## Troubleshooting

### Database Not Initializing

If the application doesn't initialize the database automatically:

1. Check MySQL is running: `./src/db/start-mysql.sh`
2. Verify connection settings in `.env`
3. Manually initialize: `./src/db/start-mysql.sh --init`

### Connection Errors

If you see connection errors:

1. Verify MySQL is running: `mysqladmin ping -h localhost -P 3306 -u root -p`
2. Check credentials in `.env` file
3. Ensure database exists: `mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS emp_mgmt;"`

### Empty Dropdowns in UI

If Division or Job Title dropdowns are empty:

1. Check if data exists:
   ```bash
   mysql -u root -p emp_mgmt -e "SELECT * FROM division;"
   mysql -u root -p emp_mgmt -e "SELECT * FROM job_titles;"
   ```

2. If empty, reload sample data:
   ```bash
   mysql -u root -p emp_mgmt < src/db/sample-data.sql
   ```

3. Restart the application

## Development Notes

- The schema uses `CREATE TABLE IF NOT EXISTS` for safe re-execution
- Sample data uses `INSERT IGNORE` and `ON DUPLICATE KEY UPDATE` for idempotency
- All tables include `created_at` and `updated_at` timestamps
- Foreign keys use `ON DELETE CASCADE` to maintain referential integrity
