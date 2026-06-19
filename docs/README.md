# Employee Management System

## Overview

Employee Management System is a Java application for managing employee data and operations. The system provides functionality for employee record management, reporting, and database operations using MySQL. Features a modern JavaFX user interface.

## Quick Start (Recommended)

The easiest way to set up and run the application is using the master setup script:

```bash
./run.sh
```

This script will automatically:
1. Set up your `.env` configuration file with database credentials
2. Start the MySQL server
3. Verify database connection
4. Initialize the database with schema and sample data
5. Build the project with Maven
6. Launch the JavaFX application

**First-time users**: The script will prompt you for your MySQL username and password.

## Manual Setup

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Setup Steps

1. Clone the repository or navigate to the project directory.

2. Create a `.env` file in the project root with your database credentials:
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=emp_mgmt
   DB_USER=root
   DB_PASS=your_password
   ```

3. Start MySQL and initialize the database:
   ```bash
   cd src/db
   ./start-mysql.sh --init
   ```

4. Build and run the project using Maven (see commands below).

## Build Commands

### Run the JavaFX Application

```bash
mvn clean javafx:run
```

### Build the Project

To build the project, run:

```bash
mvn clean package
```

This command will:
- Clean previous build artifacts
- Compile the source code
- Run unit tests
- Package the application into a JAR file with dependencies (using Maven Shade Plugin)

The resulting JAR file will be located in the `target/` directory.

### Additional Maven Commands

- Compile only: `mvn compile`
- Run tests: `mvn test`
- Clean build directory: `mvn clean`
- Install to local repository: `mvn install`
- Run JavaFX app: `mvn javafx:run`

## Features

- **Modern JavaFX UI**: Professional graphical interface with multiple screens
- **Employee Management**: Add, edit, search, and view employee records
- **Division & Job Title Management**: Organize employees by divisions and job titles
- **Payroll Tracking**: Track employee compensation and payment history
- **Reports**: Generate monthly payroll reports and analytics
- **Automatic Database Setup**: Zero-configuration database initialization

## Documentation

For more detailed information, see:
- [QUICK_START.md](../QUICK_START.md) - Comprehensive setup guide
- [SETUP.md](SETUP.md) - Detailed setup instructions
- [DB_INIT.md](DB_INIT.md) - Database initialization guide

