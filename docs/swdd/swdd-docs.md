# SWDD-EMS-2025-001

## Employee Management System
### Software Design Document

**Course:** CSC 3350 Software Development  
**Version:** 1.0.0  
**Date:** November 30, 2025  
**Status:** Active Development

---

## TABLE OF CONTENTS

1.0 [INTRODUCTION](#10-introduction)  
&nbsp;&nbsp;&nbsp;&nbsp;1.1 [Purpose](#11-purpose)  
&nbsp;&nbsp;&nbsp;&nbsp;1.2 [Scope](#12-scope)  
&nbsp;&nbsp;&nbsp;&nbsp;1.3 [Overview](#13-overview)  
&nbsp;&nbsp;&nbsp;&nbsp;1.4 [Reference Material](#14-reference-material)  
&nbsp;&nbsp;&nbsp;&nbsp;1.5 [Definitions and Acronyms](#15-definitions-and-acronyms)  

2.0 [SYSTEM OVERVIEW](#20-system-overview)  

3.0 [SYSTEM ARCHITECTURE](#30-system-architecture)  
&nbsp;&nbsp;&nbsp;&nbsp;3.1 [Architectural Design](#31-architectural-design)  
&nbsp;&nbsp;&nbsp;&nbsp;3.2 [Decomposition Description](#32-decomposition-description)  
&nbsp;&nbsp;&nbsp;&nbsp;3.3 [Design Rationale](#33-design-rationale)  

4.0 [DATA DESIGN](#40-data-design)  
&nbsp;&nbsp;&nbsp;&nbsp;4.1 [Data Description](#41-data-description)  
&nbsp;&nbsp;&nbsp;&nbsp;4.2 [Data Dictionary](#42-data-dictionary)  

5.0 [COMPONENT DESIGN](#50-component-design)  
&nbsp;&nbsp;&nbsp;&nbsp;5.1 [Database Access Layer](#51-database-access-layer)  
&nbsp;&nbsp;&nbsp;&nbsp;5.2 [Data Access Object Layer](#52-data-access-object-layer)  
&nbsp;&nbsp;&nbsp;&nbsp;5.3 [Model Layer](#53-model-layer)  
&nbsp;&nbsp;&nbsp;&nbsp;5.4 [Service Layer](#54-service-layer)  
&nbsp;&nbsp;&nbsp;&nbsp;5.5 [User Interface Layer](#55-user-interface-layer)  

6.0 [HUMAN INTERFACE DESIGN](#60-human-interface-design)  
&nbsp;&nbsp;&nbsp;&nbsp;6.1 [Overview of User Interface](#61-overview-of-user-interface)  
&nbsp;&nbsp;&nbsp;&nbsp;6.2 [Screen Images](#62-screen-images)  
&nbsp;&nbsp;&nbsp;&nbsp;6.3 [Screen Objects and Actions](#63-screen-objects-and-actions)  
&nbsp;&nbsp;&nbsp;&nbsp;6.4 [Future JavaFX Interface](#64-future-javafx-interface)  

7.0 [REQUIREMENTS MATRIX](#70-requirements-matrix)  

8.0 [APPENDICES](#80-appendices)  

---

## 1.0 INTRODUCTION

### 1.1 Purpose

This Software Design Document (SWDD) describes the architecture, system design, and implementation details of the Employee Management System (EMS). The document is intended for software developers, system maintainers, quality assurance engineers, and technical reviewers who require comprehensive understanding of the system's internal structure and design decisions.

The EMS is a Java-based application that manages employee records, organizational divisions, job titles, and payroll data through a MySQL relational database. This document follows IEEE Std 1016-2009 guidelines for software design descriptions.

### 1.2 Scope

The Employee Management System provides comprehensive functionality for managing organizational personnel data. The system supports:

- **Employee Management:** Create, read, update, and delete employee records with SSN-based identification
- **Organizational Structure:** Manage divisions and job titles with one-to-one employee assignments
- **Payroll Operations:** Track pay periods and amounts with support for bulk percentage-based salary adjustments
- **Search Capabilities:** Multi-criteria employee search by ID, SSN, or name fragments
- **Reporting:** Generate aggregated payroll reports by job title, division, and individual employee history
- **Data Integrity:** Enforce referential integrity through foreign key constraints and transactional operations

The current implementation provides a command-line interface (CLI) for all operations. Future iterations will include a JavaFX-based graphical user interface while maintaining the existing business logic layer.

### 1.3 Overview

This document is organized into eight major sections:

- **Section 1** provides introductory information including purpose, scope, and terminology
- **Section 2** presents a high-level system overview and context
- **Section 3** describes the system architecture, decomposition strategy, and design rationale
- **Section 4** details the data design including database schema and data structures
- **Section 5** provides detailed component designs with algorithms and interfaces
- **Section 6** describes the human interface design for both CLI and future JavaFX implementations
- **Section 7** maps system components to functional requirements
- **Section 8** contains supplementary materials and references

### 1.4 Reference Material

The following documents and resources were consulted during system design:

**Standards and Specifications:**
- IEEE Std 1016-2009: IEEE Standard for Information Technology—Systems Design—Software Design Descriptions
- JDBC 4.3 Specification (JSR 221)
- Java SE 21 Language Specification (JLS)

**Technical Documentation:**
- Oracle Java SE 21 Documentation (https://docs.oracle.com/en/java/javase/21/)
- MySQL 9.0 Reference Manual (https://dev.mysql.com/doc/refman/9.0/en/)
- MySQL Connector/J 9.0 Developer Guide
- Apache Maven 3.9 Documentation (https://maven.apache.org/guides/)
- JUnit 5 User Guide (https://junit.org/junit5/docs/current/user-guide/)

**Build and CI Tools:**
- Maven Shade Plugin 3.5.1 Documentation
- Maven Surefire Plugin 3.2.2 Documentation
- GitHub Actions Workflow Syntax

**Project-Specific Documents:**
- Software Requirements Specification (SWRS-EMS-2025-001)
- Database Schema Documentation (schema.sql)
- Setup and Installation Guide (SETUP.md)

### 1.5 Definitions and Acronyms

**DAO** - Data Access Object. A design pattern that abstracts and encapsulates all access to a data source, providing a clean separation between business logic and data persistence.

**DTO** - Data Transfer Object. A simple object that carries data between processes, typically used for aggregating data from multiple sources for reporting or display.

**JDBC** - Java Database Connectivity. A Java API that defines how a client may access a database, providing methods for querying and updating data.

**CRUD** - Create, Read, Update, Delete. The four basic operations of persistent storage.

**CLI** - Command Line Interface. A text-based interface for interacting with the application through terminal commands.

**GUI** - Graphical User Interface. A visual interface using windows, icons, and menus (planned for future implementation using JavaFX).

**CI/CD** - Continuous Integration/Continuous Deployment. Automated processes for building, testing, and deploying software.

**POJO** - Plain Old Java Object. A simple Java object that doesn't extend or implement specialized frameworks or classes.

**SSN** - Social Security Number. A nine-digit identifier used as a unique employee identifier (stored without dashes).

**FK** - Foreign Key. A database constraint that establishes a link between data in two tables.

**PK** - Primary Key. A unique identifier for a database table record.

**ORM** - Object-Relational Mapping. A technique for converting data between incompatible type systems (not used in this implementation; raw JDBC is used instead).

**PreparedStatement** - A precompiled SQL statement that accepts parameters, providing protection against SQL injection attacks.

**Transaction** - A sequence of database operations that are treated as a single unit of work, ensuring ACID properties.

**Maven** - A build automation and dependency management tool for Java projects.

**InnoDB** - A storage engine for MySQL that provides ACID-compliant transaction support and foreign key constraints.

---

## 2.0 SYSTEM OVERVIEW

The Employee Management System is a Java 21-based application designed to manage organizational personnel data through a MySQL 9.0 relational database. The system implements a layered architecture that separates concerns across five distinct layers: database, data access, business logic, service coordination, and user interface.

**System Context:**

The application operates as a standalone Java process that connects to a MySQL database server. Users interact with the system through a terminal-based command-line interface that presents menu-driven navigation. All data persistence occurs through JDBC connections using prepared statements to ensure security and performance.

**Primary Functionality:**

1. **Employee Lifecycle Management:** Complete CRUD operations for employee records including personal information (name, SSN, email), organizational assignments (division, job title), and temporal tracking (creation and modification timestamps).

2. **Organizational Structure:** Management of divisions and job titles as independent entities with one-to-one relationships to employees, enforcing that each employee belongs to exactly one division and holds exactly one job title.

3. **Payroll Administration:** Recording and management of payroll records with pay period tracking. Supports bulk operations such as percentage-based salary increases within specified amount ranges using transactional guarantees.

4. **Search and Retrieval:** Multi-criteria search capabilities allowing employee lookup by employee ID, SSN (exact match), or name fragments (partial match on first or last name).

5. **Reporting and Analytics:** Generation of aggregated reports including employee pay history, total monthly pay by job title, and total monthly pay by division.

**Technical Stack:**

- **Runtime:** Java 21 (LTS) with modern language features
- **Database:** MySQL 9.0 with InnoDB storage engine
- **Build Tool:** Apache Maven 3.9.11
- **Testing:** JUnit 5.10.1 (Jupiter)
- **Database Connectivity:** MySQL Connector/J 9.0.0
- **Packaging:** Maven Shade Plugin for uber-JAR creation

**Deployment Model:**

The application is packaged as a single executable JAR file containing all dependencies. Database connection parameters are provided through environment variables (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD), allowing deployment across different environments without code modification. The database schema is initialized through SQL scripts that can be executed idempotently.

**Data Flow:**

User input flows from the CLI layer through service coordinators to DAO implementations, which execute parameterized SQL statements against the MySQL database. Results flow back through DTOs (for complex aggregations) or domain models (for entity operations), with the UI layer formatting output for terminal display.

**Concurrency Model:**

The current implementation is single-threaded with synchronous database operations. Each user action completes before the next is accepted. Database connections are obtained from the connection manager on demand and closed immediately after use. Future multi-user scenarios would require connection pooling and transaction isolation level considerations.

---

## 3.0 SYSTEM ARCHITECTURE

### 3.1 Architectural Design

The Employee Management System employs a **five-layer architecture** that provides clear separation of concerns and facilitates maintainability, testability, and future extensibility. Each layer has well-defined responsibilities and communicates with adjacent layers through explicit interfaces.

**Layer Hierarchy (Bottom to Top):**

```
┌─────────────────────────────────────────────────────────────┐
│                    User Interface Layer                      │
│              (ConsoleUI, EmployeeConsole,                    │
│               ReportConsole, DAOConsole)                     │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────────┐
│                      Service Layer                           │
│            (EmployeeService, ReportService)                  │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────────┐
│                  Data Access Object Layer                    │
│     (EmployeeDAO, PayrollDAO, ReportDAO, DivisionDAO,       │
│      JobTitleDAO, EmployeeDivisionDAO, EmployeeJobTitleDAO) │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────────┐
│                 Database Access Layer                        │
│         (DatabaseConnectionManager, DatabaseInit)            │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────────┐
│                      Database Layer                          │
│              (MySQL 9.0 with InnoDB Engine)                  │
└─────────────────────────────────────────────────────────────┘
```

**Cross-Cutting Concerns:**

```
┌─────────────────────────────────────────────────────────────┐
│                       Model Layer                            │
│     (Employee, Division, JobTitle, Payroll, etc.)           │
│            (Used across all layers)                          │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                        DTO Layer                             │
│  (EmployeeWithPayHistory, JobTitlePay, DivisionPay)        │
│            (Used for reporting and aggregation)              │
└─────────────────────────────────────────────────────────────┘
```

**Layer Responsibilities:**

**1. Database Layer**
- Physical data storage using MySQL InnoDB tables
- Enforcement of referential integrity through foreign keys
- Transaction management and ACID guarantees
- Index-based query optimization
- Timestamp-based audit trail (created_at, updated_at)

**2. Database Access Layer**
- JDBC connection lifecycle management
- Environment-based configuration (host, port, credentials)
- Connection validation and retry logic
- Database initialization and schema setup
- Resource cleanup and connection pooling preparation

**3. Data Access Object Layer**
- SQL query construction and execution
- ResultSet to domain object mapping
- Prepared statement parameterization
- Transaction boundary management for multi-statement operations
- Specialized search and query operations

**4. Service Layer**
- Business logic coordination
- Multi-DAO operation orchestration
- Domain-level validation
- Exception translation and handling
- Future location for complex business rules

**5. User Interface Layer**
- User input collection and validation
- Menu navigation and command routing
- Output formatting for terminal display
- Error message presentation
- Future JavaFX GUI integration point

**Data Flow Example (Employee Update Operation):**

1. User selects "Update Employee" from ConsoleUI menu
2. EmployeeConsole prompts for employee ID and new values
3. EmployeeConsole calls EmployeeService.updateEmployee()
4. EmployeeService delegates to EmployeeDAO.update()
5. EmployeeDAO begins transaction, validates division/job title existence
6. EmployeeDAO executes UPDATE statement via PreparedStatement
7. EmployeeDAO updates employee_division and employee_job_titles tables
8. EmployeeDAO commits transaction
9. Success indicator returns through layers
10. ConsoleUI displays confirmation message

### 3.2 Decomposition Description

**Subsystem Decomposition:**

**A. Database Connection Subsystem**

*Components:*
- `DatabaseConnectionManager` (Singleton)
- `DatabaseInit`

*Responsibilities:*
- Read environment variables: DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
- Construct JDBC URL: `jdbc:mysql://{host}:{port}/{database}`
- Establish MySQL connection with retry logic (max 3 attempts, 2-second delay)
- Provide connection factory method: `getConnection()`
- Execute schema initialization scripts
- Handle connection failures with detailed error messages

*Interfaces:*
```java
public Connection getConnection() throws SQLException
public void initializeDatabase() throws SQLException
private boolean testConnection()
```

**B. Model Subsystem**

*Components:*
- `Employee` - Employee entity with validation
- `Division` - Organizational division entity
- `JobTitle` - Job title entity
- `Payroll` - Payroll record entity
- `EmployeeDivision` - Employee-division association
- `EmployeeJobTitle` - Employee-job title association
- `PayrollRecord` - Simplified payroll DTO for display

*Responsibilities:*
- Encapsulate entity state with private fields
- Provide constructors: default, partial, and full
- Implement field validation (SSN format, email regex, non-negative amounts)
- Generate CLI-friendly string representations via toString()
- Maintain JavaBean conventions (getters/setters)

*Validation Rules:*
- SSN: Exactly 9 digits, no dashes, validated via regex `\d{9}`
- Email: RFC 5322 simplified pattern validation
- Numeric fields: Non-negative constraint (amount >= 0)
- Required fields: Non-null, non-empty string validation

**C. Data Access Object Subsystem**

*Components:*
- Interface-implementation pairs for each entity:
  - `EmployeeDAO` / `EmployeeDAOImpl`
  - `DivisionDAO` / `DivisionDAOImpl`
  - `JobTitleDAO` / `JobTitleDAOImpl`
  - `PayrollDAO` / `PayrollDAOImpl`
  - `EmployeeDivisionDAO` / `EmployeeDivisionDAOImpl`
  - `EmployeeJobTitleDAO` / `EmployeeJobTitleDAOImpl`
- `ReportDAO` - Specialized aggregation queries
- `SQLConstants` - Centralized SQL string constants

*Standard DAO Operations:*
```java
T insert(T entity) throws SQLException
Optional<T> findById(int id) throws SQLException
List<T> findAll() throws SQLException
boolean update(T entity) throws SQLException
boolean delete(int id) throws SQLException
```

*Specialized Operations:*
```java
// EmployeeDAO
Optional<Employee> findBySSN(String ssn)
List<Employee> findByNameFragment(String fragment)
Employee insert(Employee emp, int divisionId, int jobTitleId)
boolean update(Employee emp, int divisionId, int jobTitleId)

// PayrollDAO
int increaseAmountInRange(BigDecimal min, BigDecimal max, BigDecimal percent)

// ReportDAO
EmployeeWithPayHistory getEmployeeWithPayHistory(int employeeId)
List<JobTitlePay> getTotalPayByJobTitle(int year, int month)
List<DivisionPay> getTotalPayByDivision(int year, int month)
```

*Implementation Patterns:*
- Try-with-resources for automatic resource management
- PreparedStatement for all parameterized queries
- LIKE wildcard escaping to prevent injection: `escapeLikeWildcards()`
- Transaction management with explicit commit/rollback
- Foreign key existence validation before insert/update
- ResultSet to object mapping in private helper methods

**D. Service Subsystem**

*Components:*
- `EmployeeService` - Employee and payroll coordination
- `ReportService` - Report generation coordination

*Responsibilities:*
- Coordinate multiple DAO operations
- Provide simplified API for UI layer
- Translate DAO exceptions to user-friendly messages
- Future location for business rule enforcement
- Maintain separation between UI and data access concerns

*EmployeeService Operations:*
```java
Employee addEmployee(Employee e, int divisionId, int jobTitleId)
Optional<Employee> findById(int id)
Optional<Employee> findBySSN(String ssn)
List<Employee> findByName(String fragment)
boolean updateEmployee(Employee e, int divisionId, int jobTitleId)
boolean deleteEmployee(int employeeId)
int increaseSalaryInRange(BigDecimal min, BigDecimal max, BigDecimal percentage)
Optional<String> getDivisionNameById(int divisionId)
Optional<String> getJobTitleNameById(int jobTitleId)
```

*ReportService Operations:*
```java
EmployeeWithPayHistory getEmployeeWithPayHistory(int employeeId)
List<JobTitlePay> getTotalPayByJobTitle(int year, int month)
List<DivisionPay> getTotalPayByDivision(int year, int month)
```

**E. User Interface Subsystem**

*Components:*
- `App` - Application entry point and initialization
- `ConsoleUI` - Main menu coordinator
- `EmployeeConsole` - Employee operation handlers
- `ReportConsole` - Report generation handlers
- `DAOConsole` - Direct DAO testing interface (development tool)

*Responsibilities:*
- Display menu options and prompts
- Collect and validate user input
- Route commands to appropriate service methods
- Format and display operation results
- Handle and display error messages
- Provide input retry mechanisms for invalid data

*Input Validation Helpers:*
```java
int readInt(String prompt)           // Retry until valid integer
double readDouble(String prompt)      // Retry until valid double
BigDecimal readBigDecimal(String prompt)
String readNonEmpty(String prompt)    // Retry until non-empty
String readOptional(String prompt)    // Allow empty for "keep current"
String readSSN()                      // Enforce 9-digit format
```

**F. Data Transfer Object Subsystem**

*Components:*
- `EmployeeWithPayHistory` - Employee with list of PayrollRecords
- `JobTitlePay` - Job title name with aggregated total pay
- `DivisionPay` - Division name with aggregated total pay

*Purpose:*
- Aggregate data from multiple tables for reporting
- Avoid exposing complex JOIN logic to UI layer
- Provide read-only data structures for display
- Simplify report formatting logic

### 3.3 Design Rationale

**Layered Architecture Selection:**

A layered architecture was chosen over alternatives (microservices, event-driven, monolithic) for the following reasons:

1. **Appropriate Scale:** The system manages a single domain (employee data) with moderate complexity, making microservices overhead unnecessary.

2. **Clear Separation of Concerns:** Each layer has a single, well-defined responsibility, improving code organization and maintainability.

3. **Testability:** Layers can be tested independently with mock implementations of dependencies.

4. **Educational Value:** The architecture clearly demonstrates software engineering principles appropriate for undergraduate coursework.

5. **Future Extensibility:** The service layer provides a natural integration point for future JavaFX GUI without modifying business logic.

**DAO Pattern Over ORM:**

Raw JDBC with the DAO pattern was selected instead of an ORM framework (Hibernate, JPA) because:

1. **Transparency:** SQL queries are explicit and visible, aiding learning and debugging.

2. **Performance Control:** Direct control over query construction and execution plans.

3. **Simplicity:** No framework configuration, annotation processing, or lazy loading complexity.

4. **Lightweight:** Minimal dependencies reduce JAR size and startup time.

**Interface-Based DAO Design:**

DAO interfaces with concrete implementations provide:

1. **Testability:** Mock implementations can be injected for unit testing.

2. **Flexibility:** Implementation can be swapped (e.g., to NoSQL) without changing service layer.

3. **Documentation:** Interfaces serve as contracts documenting expected behavior.

**Singleton DatabaseConnectionManager:**

The connection manager uses the singleton pattern because:

1. **Resource Management:** Single point of control for database connections.

2. **Configuration Centralization:** Environment variables read once at initialization.

3. **Connection Pooling Preparation:** Future connection pool can be added without changing client code.

**Service Layer Justification:**

Although current business logic is minimal, the service layer provides:

1. **Future-Proofing:** Location for complex business rules as system evolves.

2. **Transaction Coordination:** Multi-DAO operations can be coordinated with shared transactions.

3. **API Simplification:** UI layer has cleaner, more semantic method calls.

**Rejected Alternatives:**

1. **Microservices:** Excessive complexity and network overhead for single-domain system.

2. **Event-Driven Architecture:** No asynchronous processing requirements justify event bus complexity.

3. **Direct UI-to-DAO Communication:** Would violate separation of concerns and complicate future GUI integration.

4. **Stored Procedures:** Would split business logic between Java and SQL, complicating maintenance and version control.

---

## 4.0 DATA DESIGN

### 4.1 Data Description

The Employee Management System stores data in a normalized MySQL relational database using the InnoDB storage engine. The schema consists of five primary entity tables and two association tables that enforce one-to-one relationships between employees and their organizational assignments.

**Normalization Level:**

The database schema is in **Third Normal Form (3NF)**:
- **1NF:** All columns contain atomic values (no repeating groups or arrays)
- **2NF:** All non-key attributes are fully dependent on the primary key
- **3NF:** No transitive dependencies exist (non-key attributes depend only on the primary key)

**Storage Engine:**

InnoDB was selected for all tables to provide:
- ACID-compliant transaction support
- Foreign key constraint enforcement
- Row-level locking for concurrent access
- Crash recovery capabilities
- Referential integrity guarantees

**Character Set:**

All tables use `utf8mb4` character set with `utf8mb4_general_ci` collation to support:
- Full Unicode character range (including emojis, if needed)
- International names and addresses
- Consistent sorting and comparison behavior

**Temporal Tracking:**

All entity tables include audit timestamps:
- `created_at`: Automatically set on INSERT using `DEFAULT CURRENT_TIMESTAMP`
- `updated_at`: Automatically updated on UPDATE using `ON UPDATE CURRENT_TIMESTAMP`

**Referential Integrity:**

Foreign key constraints enforce data consistency:
- `ON DELETE CASCADE`: Deleting an employee automatically removes associated division, job title, and payroll records
- `ON UPDATE CASCADE`: Implicit for primary key changes (though IDs are immutable in practice)

**Constraints and Validation:**

- **Primary Keys:** Auto-incrementing integers for all entity tables
- **Unique Constraints:** SSN must be unique across all employees; payroll records are unique per employee per pay period
- **Check Constraints:** Payroll amounts must be non-negative (`amount >= 0`)
- **NOT NULL Constraints:** Critical fields (names, SSN, email, foreign keys) cannot be null

**Indexes:**

Performance-optimized indexes are created for frequently queried columns:
- `idx_employees_first_name`: Supports name fragment searches
- `idx_employees_last_name`: Supports name fragment searches
- `idx_employees_ssn`: Supports SSN lookups (in addition to UNIQUE constraint index)
- `idx_employees_email`: Supports potential future email-based queries

**Data Persistence Strategy:**

- **Transactional Operations:** Multi-table updates (employee + division + job title) are wrapped in transactions
- **Idempotent Scripts:** Schema and sample data scripts use `IF NOT EXISTS` and `INSERT IGNORE` to allow safe re-execution
- **Prepared Statements:** All queries use parameterized PreparedStatements to prevent SQL injection

### 4.2 Data Dictionary

**Table: employees**

Primary entity table storing employee personal information.

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| employee_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique employee identifier |
| first_name | VARCHAR(50) | NOT NULL | Employee's first name |
| last_name | VARCHAR(50) | NOT NULL | Employee's last name |
| SSN | VARCHAR(9) | UNIQUE, NOT NULL | Social Security Number (9 digits, no dashes) |
| email | VARCHAR(100) | NOT NULL | Employee's email address |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last modification timestamp |

**Indexes:** PRIMARY KEY (employee_id), UNIQUE KEY (SSN), INDEX (first_name), INDEX (last_name), INDEX (email)

---

**Table: division**

Organizational divisions within the company.

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| division_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique division identifier |
| name | VARCHAR(100) | NOT NULL | Division name (e.g., "Engineering", "Sales") |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last modification timestamp |

**Indexes:** PRIMARY KEY (division_id)

---

**Table: job_titles**

Available job titles that can be assigned to employees.

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| job_title_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique job title identifier |
| title | VARCHAR(100) | NOT NULL | Job title name (e.g., "Software Engineer") |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last modification timestamp |

**Indexes:** PRIMARY KEY (job_title_id)

---

**Table: payroll**

Payroll records tracking employee compensation by pay period.

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| payroll_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique payroll record identifier |
| employee_id | INT | NOT NULL, FOREIGN KEY | Reference to employees table |
| amount | DECIMAL(10,2) | NOT NULL, CHECK (amount >= 0) | Payment amount in dollars |
| pay_period_start | DATE | NOT NULL | Pay period start date |
| pay_period_end | DATE | NOT NULL | Pay period end date |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last modification timestamp |

**Foreign Keys:**
- `employee_id` REFERENCES employees(employee_id) ON DELETE CASCADE

**Unique Constraints:**
- UNIQUE KEY uk_payroll_employee_period (employee_id, pay_period_start, pay_period_end)

**Indexes:** PRIMARY KEY (payroll_id), FOREIGN KEY INDEX (employee_id)

---

**Table: employee_division**

Association table enforcing one-to-one relationship between employees and divisions.

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| employee_id | INT | PRIMARY KEY, NOT NULL, FOREIGN KEY | Reference to employees table |
| division_id | INT | NOT NULL, FOREIGN KEY | Reference to division table |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last modification timestamp |

**Foreign Keys:**
- `employee_id` REFERENCES employees(employee_id) ON DELETE CASCADE
- `division_id` REFERENCES division(division_id) ON DELETE CASCADE

**Design Note:** Primary key on employee_id alone enforces one-to-one relationship (each employee has exactly one division).

---

**Table: employee_job_titles**

Association table enforcing one-to-one relationship between employees and job titles.

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| employee_id | INT | PRIMARY KEY, NOT NULL, FOREIGN KEY | Reference to employees table |
| job_title_id | INT | NOT NULL, FOREIGN KEY | Reference to job_titles table |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last modification timestamp |

**Foreign Keys:**
- `employee_id` REFERENCES employees(employee_id) ON DELETE CASCADE
- `job_title_id` REFERENCES job_titles(job_title_id) ON DELETE CASCADE

**Design Note:** Primary key on employee_id alone enforces one-to-one relationship (each employee has exactly one job title).

---

**Java Model Classes:**

**Employee.java**

```java
class Employee {
    private Integer employeeId;
    private String firstName;
    private String lastName;
    private String ssn;           // 9 digits, no dashes
    private String email;
    private Integer divisionId;   // Denormalized for convenience
    private Integer jobTitleId;   // Denormalized for convenience
    
    // Constructors: default, partial (name + ssn + email), full
    // Getters and setters for all fields
    // Validation: validateSSN(), validateEmail()
    // toString() for CLI display
}
```

**Division.java**

```java
class Division {
    private Integer divisionId;
    private String name;
    
    // Constructors, getters, setters, toString()
}
```

**JobTitle.java**

```java
class JobTitle {
    private Integer jobTitleId;
    private String title;
    
    // Constructors, getters, setters, toString()
}
```

**Payroll.java**

```java
class Payroll {
    private Integer payrollId;
    private Integer employeeId;
    private BigDecimal amount;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    
    // Constructors, getters, setters
    // Validation: amount must be non-negative
}
```

**PayrollRecord.java** (Simplified DTO)

```java
class PayrollRecord {
    private Integer payrollId;
    private Integer employeeId;
    private BigDecimal amount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    
    // Used for display in reports
}
```

**EmployeeDivision.java**

```java
class EmployeeDivision {
    private Integer employeeId;
    private Integer divisionId;
    
    // Simple association object
}
```

**EmployeeJobTitle.java**

```java
class EmployeeJobTitle {
    private Integer employeeId;
    private Integer jobTitleId;
    
    // Simple association object
}
```

---

**Data Transfer Objects (DTOs):**

**EmployeeWithPayHistory**

```java
class EmployeeWithPayHistory {
    private Employee employee;
    private String divisionName;
    private String jobTitleName;
    private List<PayrollRecord> payRecords;
    
    // Aggregates employee info with pay history for reporting
}
```

**JobTitlePay**

```java
class JobTitlePay {
    private String jobTitleName;
    private BigDecimal totalPay;
    
    // Aggregates total pay by job title for a given month
}
```

**DivisionPay**

```java
class DivisionPay {
    private String divisionName;
    private BigDecimal totalPay;
    
    // Aggregates total pay by division for a given month
}
```

---

## 5.0 COMPONENT DESIGN

This section provides detailed design specifications for each major component in the system, including algorithms, data structures, and interface contracts.

### 5.1 Database Access Layer

**Component: DatabaseConnectionManager**

*Type:* Singleton class  
*Package:* com.emp_mgmt.db  
*Purpose:* Centralized management of MySQL database connections with environment-based configuration

*Class Structure:*

```java
public class DatabaseConnectionManager {
    // Singleton instance
    private static DatabaseConnectionManager instance;
    
    // Configuration fields
    private final String dbHost;
    private final String dbPort;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;
    private final String jdbcUrl;
    
    // Private constructor
    private DatabaseConnectionManager() { ... }
    
    // Public methods
    public static DatabaseConnectionManager getInstance()
    public Connection getConnection() throws SQLException
    private boolean testConnection()
}
```

*Algorithm: getInstance()*

```
IF instance is null THEN
    CREATE new DatabaseConnectionManager instance
    READ environment variables:
        - DB_HOST (default: "localhost")
        - DB_PORT (default: "3306")
        - DB_NAME (required)
        - DB_USER (required)
        - DB_PASSWORD (required)
    CONSTRUCT jdbcUrl = "jdbc:mysql://{host}:{port}/{name}"
    APPEND connection parameters:
        - useSSL=false
        - allowPublicKeyRetrieval=true
        - serverTimezone=UTC
    TEST connection with testConnection()
    IF test fails THEN
        THROW SQLException with detailed message
    END IF
END IF
RETURN instance
```

*Algorithm: getConnection()*

```
TRY
    RETURN DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)
CATCH SQLException
    LOG error details
    THROW SQLException with user-friendly message
END TRY
```

*Error Handling:*
- Missing environment variables: Throw IllegalStateException with variable name
- Connection failure: Retry up to 3 times with 2-second delay
- Invalid credentials: Throw SQLException with authentication error message

---

**Component: DatabaseInit**

*Type:* Utility class  
*Package:* com.emp_mgmt.db  
*Purpose:* Initialize database schema and load sample data

*Class Structure:*

```java
public class DatabaseInit {
    private final DatabaseConnectionManager dbManager;
    
    public DatabaseInit()
    public void initializeSchema() throws SQLException
    public void loadSampleData() throws SQLException
    private void executeSQLScript(String scriptPath) throws SQLException
    private List<String> parseSQL(String script)
}
```

*Algorithm: initializeSchema()*

```
READ schema.sql file from classpath
PARSE SQL into individual statements
FOR EACH statement
    EXECUTE statement via PreparedStatement
    IF statement is CREATE INDEX THEN
        CHECK if index already exists
        SKIP if exists (handle MySQL duplicate index error)
    END IF
END FOR
LOG success message
```

*Algorithm: loadSampleData()*

```
READ sample-data.sql file from classpath
PARSE SQL into individual statements
FOR EACH statement
    EXECUTE statement via PreparedStatement
    HANDLE duplicate key errors (INSERT IGNORE, ON DUPLICATE KEY UPDATE)
END FOR
LOG success message with row counts
```

*SQL Parsing Logic:*

```
SPLIT script by semicolons
FOR EACH potential statement
    TRIM whitespace
    SKIP if empty or comment-only
    HANDLE multi-line statements (preserve until complete)
    ADD to statement list
END FOR
RETURN statement list
```

### 5.2 Data Access Object Layer

**Component: EmployeeDAO**

*Type:* Data Access Object  
*Package:* com.emp_mgmt.dao  
*Purpose:* CRUD and search operations for employee records with transactional division/job title updates

*Interface Definition:*

```java
public interface EmployeeDAO {
    Employee insert(Employee emp, int divisionId, int jobTitleId) throws SQLException;
    Optional<Employee> findById(int id) throws SQLException;
    Optional<Employee> findBySSN(String ssn) throws SQLException;
    List<Employee> findByNameFragment(String fragment) throws SQLException;
    boolean update(Employee emp, int divisionId, int jobTitleId) throws SQLException;
    boolean delete(int employeeId) throws SQLException;
}
```

*Algorithm: insert(Employee, divisionId, jobTitleId)*

```
VALIDATE divisionId > 0 AND jobTitleId > 0
GET database connection
BEGIN TRANSACTION (setAutoCommit(false))
TRY
    CHECK divisionExists(divisionId)
    IF NOT exists THEN THROW SQLException
    
    CHECK jobTitleExists(jobTitleId)
    IF NOT exists THEN THROW SQLException
    
    PREPARE statement: INSERT INTO employees (first_name, last_name, SSN, email) VALUES (?, ?, ?, ?)
    SET parameters from employee object
    EXECUTE insert
    GET generated employee_id from ResultSet
    SET employee.employeeId = generated_id
    
    CALL upsertEmployeeDivision(employee_id, divisionId)
    CALL upsertEmployeeJobTitle(employee_id, jobTitleId)
    
    COMMIT TRANSACTION
    RETURN employee with populated ID
CATCH SQLException
    ROLLBACK TRANSACTION
    THROW SQLException
FINALLY
    RESTORE autoCommit = true
    CLOSE resources
END TRY
```

*Algorithm: findByNameFragment(fragment)*

```
ESCAPE LIKE wildcards in fragment:
    REPLACE '%' with '\%'
    REPLACE '_' with '\_'
CONSTRUCT pattern = "%" + escaped_fragment + "%"

PREPARE statement:
    SELECT e.employee_id, e.first_name, e.last_name, e.SSN, e.email,
           ed.division_id, ej.job_title_id
    FROM employees e
    LEFT JOIN employee_division ed ON e.employee_id = ed.employee_id
    LEFT JOIN employee_job_titles ej ON e.employee_id = ej.employee_id
    WHERE e.first_name LIKE ? OR e.last_name LIKE ?
    ORDER BY e.last_name, e.first_name

SET parameter 1 = pattern
SET parameter 2 = pattern
EXECUTE query

INITIALIZE empty list
WHILE ResultSet has next row
    MAP row to Employee object
    ADD to list
END WHILE
RETURN list
```

*Algorithm: update(Employee, divisionId, jobTitleId)*

```
VALIDATE divisionId > 0 AND jobTitleId > 0
GET database connection
BEGIN TRANSACTION (setAutoCommit(false))
TRY
    PREPARE statement: UPDATE employees SET first_name=?, last_name=?, SSN=?, email=? WHERE employee_id=?
    SET parameters from employee object
    EXECUTE update
    GET rows affected
    
    IF rows > 0 THEN
        CHECK divisionExists(divisionId)
        IF NOT exists THEN THROW SQLException
        
        CHECK jobTitleExists(jobTitleId)
        IF NOT exists THEN THROW SQLException
        
        CALL clearEmployeeDivision(employee_id)
        CALL clearEmployeeJobTitle(employee_id)
        CALL upsertEmployeeDivision(employee_id, divisionId)
        CALL upsertEmployeeJobTitle(employee_id, jobTitleId)
    END IF
    
    COMMIT TRANSACTION
    RETURN (rows > 0)
CATCH SQLException
    ROLLBACK TRANSACTION
    THROW SQLException
FINALLY
    RESTORE autoCommit = true
    CLOSE resources
END TRY
```

*Helper Methods:*

```
divisionExists(Connection, divisionId):
    EXECUTE: SELECT COUNT(*) FROM division WHERE division_id = ?
    RETURN count > 0

jobTitleExists(Connection, jobTitleId):
    EXECUTE: SELECT COUNT(*) FROM job_titles WHERE job_title_id = ?
    RETURN count > 0

upsertEmployeeDivision(Connection, employeeId, divisionId):
    EXECUTE: DELETE FROM employee_division WHERE employee_id = ?
    EXECUTE: INSERT INTO employee_division (employee_id, division_id) VALUES (?, ?)

escapeLikeWildcards(text):
    REPLACE '%' with '\%'
    REPLACE '_' with '\_'
    RETURN escaped text
```

---

**Component: PayrollDAO**

*Type:* Data Access Object  
*Package:* com.emp_mgmt.dao  
*Purpose:* Payroll record operations including bulk percentage-based updates

*Key Method: increaseAmountInRange(min, max, percent)*

```
VALIDATE percent is not null
VALIDATE min is not null
VALIDATE max is not null

CALCULATE factor = (percent / 100) + 1
    Example: 3.2% → factor = 1.032

PREPARE statement:
    UPDATE payroll
    SET amount = amount * ?
    WHERE amount BETWEEN ? AND ?

SET parameter 1 = factor
SET parameter 2 = min
SET parameter 3 = max
EXECUTE update
RETURN rows affected
```

*Transaction Handling:*

This method does not explicitly manage transactions; the caller (EmployeeService) may wrap it in a transaction if needed for coordination with other operations.

---

**Component: ReportDAO**

*Type:* Data Access Object  
*Package:* com.emp_mgmt.dao  
*Purpose:* Execute complex JOIN queries for reporting and aggregation

*Algorithm: getEmployeeWithPayHistory(employeeId)*

```
PREPARE statement:
    SELECT e.employee_id, e.first_name, e.last_name, e.SSN, e.email,
           d.name AS division_name,
           jt.title AS job_title_name,
           p.payroll_id, p.amount, p.pay_period_start, p.pay_period_end
    FROM employees e
    LEFT JOIN employee_division ed ON e.employee_id = ed.employee_id
    LEFT JOIN division d ON ed.division_id = d.division_id
    LEFT JOIN employee_job_titles ej ON e.employee_id = ej.employee_id
    LEFT JOIN job_titles jt ON ej.job_title_id = jt.job_title_id
    LEFT JOIN payroll p ON e.employee_id = p.employee_id
    WHERE e.employee_id = ?
    ORDER BY p.pay_period_start

SET parameter = employeeId
EXECUTE query

INITIALIZE EmployeeWithPayHistory result
INITIALIZE empty payRecords list
SET firstRow = true

WHILE ResultSet has next row
    IF firstRow THEN
        MAP employee fields to Employee object
        SET result.employee = mapped employee
        SET result.divisionName = division_name
        SET result.jobTitleName = job_title_name
        SET firstRow = false
    END IF
    
    IF payroll_id is not null THEN
        MAP payroll fields to PayrollRecord object
        ADD to payRecords list
    END IF
END WHILE

SET result.payRecords = payRecords
RETURN result
```

*Algorithm: getTotalPayByJobTitle(year, month)*

```
PREPARE statement:
    SELECT jt.title, SUM(p.amount) AS total_pay
    FROM payroll p
    JOIN employees e ON p.employee_id = e.employee_id
    JOIN employee_job_titles ej ON e.employee_id = ej.employee_id
    JOIN job_titles jt ON ej.job_title_id = jt.job_title_id
    WHERE YEAR(p.pay_period_start) = ? AND MONTH(p.pay_period_start) = ?
    GROUP BY jt.title
    ORDER BY jt.title

SET parameter 1 = year
SET parameter 2 = month
EXECUTE query

INITIALIZE empty list
WHILE ResultSet has next row
    CREATE JobTitlePay(title, total_pay)
    ADD to list
END WHILE
RETURN list
```

*Algorithm: getTotalPayByDivision(year, month)*

```
PREPARE statement:
    SELECT d.name AS division_name, SUM(p.amount) AS total_pay
    FROM payroll p
    JOIN employees e ON p.employee_id = e.employee_id
    JOIN employee_division ed ON e.employee_id = ed.employee_id
    JOIN division d ON ed.division_id = d.division_id
    WHERE YEAR(p.pay_period_start) = ? AND MONTH(p.pay_period_start) = ?
    GROUP BY d.name
    ORDER BY d.name

SET parameter 1 = year
SET parameter 2 = month
EXECUTE query

INITIALIZE empty list
WHILE ResultSet has next row
    CREATE DivisionPay(division_name, total_pay)
    ADD to list
END WHILE
RETURN list
```

### 5.3 Model Layer

**Component: Employee**

*Type:* Domain Model  
*Package:* com.emp_mgmt.model  
*Purpose:* Encapsulate employee entity with validation

*Validation Methods:*

```
validateSSN(ssn):
    IF ssn is null OR ssn is empty THEN
        THROW IllegalArgumentException("SSN is required")
    END IF
    IF ssn does not match pattern "\d{9}" THEN
        THROW IllegalArgumentException("SSN must be exactly 9 digits")
    END IF

validateEmail(email):
    IF email is null OR email is empty THEN
        THROW IllegalArgumentException("Email is required")
    END IF
    DEFINE pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    IF email does not match pattern THEN
        THROW IllegalArgumentException("Invalid email format")
    END IF
```

*toString() Format:*

```
RETURN formatted string:
    "Employee{id=%d, name='%s %s', ssn='%s', email='%s', division=%d, jobTitle=%d}"
```

### 5.4 Service Layer

**Component: EmployeeService**

*Type:* Service Coordinator  
*Package:* com.emp_mgmt.service  
*Purpose:* Coordinate employee and payroll operations across multiple DAOs

*Dependencies:*
- EmployeeDAO
- PayrollDAO
- DivisionDAO
- JobTitleDAO

*Algorithm: increaseSalaryInRange(min, max, percentage)*

```
VALIDATE min < max
VALIDATE percentage > 0

CALL payrollDAO.increaseAmountInRange(min, max, percentage)
RETURN rows affected

CATCH SQLException
    LOG error
    TRANSLATE to user-friendly message
    RETHROW or return error indicator
END CATCH
```

*Future Enhancements:*
- Add transaction coordination for multi-DAO operations
- Implement business rule validation (e.g., salary caps, approval workflows)
- Add audit logging for sensitive operations

---

**Component: ReportService**

*Type:* Service Coordinator  
*Package:* com.emp_mgmt.service  
*Purpose:* Coordinate report generation with DAO layer

*Dependencies:*
- ReportDAO

*Methods:*

All methods are simple pass-through delegates to ReportDAO, providing a consistent service layer API and future extension point for report caching, formatting, or export functionality.

### 5.5 User Interface Layer

**Component: ConsoleUI**

*Type:* UI Coordinator  
*Package:* com.emp_mgmt.ui  
*Purpose:* Main menu navigation and command routing

*Algorithm: start()*

```
SET running = true
WHILE running
    CALL printMainMenu()
    READ user choice
    SWITCH choice
        CASE 1: CALL employeeConsole.searchEmployee()
        CASE 2: CALL employeeConsole.updateEmployee()
        CASE 3: CALL employeeConsole.updateSalaryByPercentage()
        CASE 4: CALL reportConsole.showMenu()
        CASE 0: SET running = false
        DEFAULT: PRINT "Invalid choice"
    END SWITCH
END WHILE
```

---

**Component: EmployeeConsole**

*Type:* UI Handler  
*Package:* com.emp_mgmt.ui  
*Purpose:* Handle employee-specific operations

*Algorithm: searchEmployee()*

```
PRINT search options:
    1. By Employee ID
    2. By SSN
    3. By Name
READ choice

SWITCH choice
    CASE 1:
        READ employee ID
        CALL employeeService.findById(id)
        IF found THEN PRINT employee details
        ELSE PRINT "Not found"
        
    CASE 2:
        READ SSN (validate 9 digits)
        CALL employeeService.findBySSN(ssn)
        IF found THEN PRINT employee details
        ELSE PRINT "Not found"
        
    CASE 3:
        READ name fragment
        CALL employeeService.findByName(fragment)
        IF list is empty THEN
            PRINT "No employees found"
        ELSE IF list size == 1 THEN
            PRINT single employee details
        ELSE
            PRINT numbered list of employees
            READ selection
            PRINT selected employee details
        END IF
END SWITCH
```

*Algorithm: updateEmployee()*

```
PRINT search options (ID or SSN)
READ search choice
FIND employee using selected method
IF not found THEN
    PRINT "Employee not found"
    RETURN
END IF

PRINT current employee details

PROMPT for new values (allow empty to keep current):
    - First name
    - Last name
    - SSN
    - Email
    - Division ID (required)
    - Job Title ID (required)

UPDATE employee object with non-empty values
VALIDATE division ID and job title ID are provided

CALL employeeService.updateEmployee(employee, divisionId, jobTitleId)
IF success THEN
    PRINT "Employee updated successfully"
    FETCH and PRINT updated employee details
ELSE
    PRINT "Update failed"
END IF

CATCH SQLException
    PRINT "Database error: " + error message
CATCH Exception
    PRINT "Error: " + error message
END CATCH
```

*Algorithm: updateSalaryByPercentage()*

```
PRINT operation description
READ percentage (e.g., 3.2 for 3.2%)
READ minimum amount (inclusive)
READ maximum amount (exclusive)

VALIDATE min < max
IF invalid THEN
    PRINT "Error: Minimum must be less than maximum"
    RETURN
END IF

PRINT confirmation message with parameters
CALL employeeService.increaseSalaryInRange(min, max, percentage)
PRINT "Updated {rows} payroll record(s)"

CATCH SQLException
    PRINT "Database error: " + error message
CATCH Exception
    PRINT "Error: " + error message
END CATCH
```

---

**Component: ReportConsole**

*Type:* UI Handler  
*Package:* com.emp_mgmt.ui  
*Purpose:* Handle report generation and display

*Algorithm: showMenu()*

```
SET back = false
WHILE NOT back
    PRINT report options:
        1. Employee Pay History
        2. Total Pay by Job Title
        3. Total Pay by Division
        0. Back to Main Menu
    READ choice
    
    SWITCH choice
        CASE 1: CALL showEmployeePayHistory()
        CASE 2: CALL showTotalPayByJobTitle()
        CASE 3: CALL showTotalPayByDivision()
        CASE 0: SET back = true
        DEFAULT: PRINT "Invalid choice"
    END SWITCH
END WHILE
```

*Algorithm: showEmployeePayHistory()*

```
PRINT search options (ID or SSN)
READ search choice
DETERMINE employee ID from search

CALL reportService.getEmployeeWithPayHistory(employeeId)
IF employee is null THEN
    PRINT "No data found"
    RETURN
END IF

PRINT formatted employee information:
    - Employee ID
    - Full name
    - SSN
    - Email
    - Division name
    - Job Title name

PRINT pay history header
IF payRecords is empty THEN
    PRINT "No pay records found"
ELSE
    PRINT table header: Period Start | Period End | Amount
    FOR EACH record in payRecords
        PRINT formatted row
    END FOR
END IF
```

*Algorithm: showTotalPayByJobTitle()*

```
READ year (e.g., 2025)
READ month (1-12)
VALIDATE month in range [1, 12]

CALL reportService.getTotalPayByJobTitle(year, month)
IF list is empty THEN
    PRINT "No data found for {year}-{month}"
    RETURN
END IF

PRINT report header with year-month
PRINT table header: Job Title | Total Pay
INITIALIZE total = 0

FOR EACH row in list
    PRINT formatted row: job title name | $amount
    ADD row.totalPay to total
END FOR

PRINT separator line
PRINT "TOTAL | $" + total
```

*Algorithm: showTotalPayByDivision()*

```
READ year (e.g., 2025)
READ month (1-12)
VALIDATE month in range [1, 12]

CALL reportService.getTotalPayByDivision(year, month)
IF list is empty THEN
    PRINT "No data found for {year}-{month}"
    RETURN
END IF

PRINT report header with year-month
PRINT table header: Division | Total Pay
INITIALIZE total = 0

FOR EACH row in list
    PRINT formatted row: division name | $amount
    ADD row.totalPay to total
END FOR

PRINT separator line
PRINT "TOTAL | $" + total
```

---

## 6.0 HUMAN INTERFACE DESIGN

### 6.1 Overview of User Interface

The Employee Management System provides two user interface modalities: a command-line interface (CLI) for the current implementation and a planned JavaFX graphical user interface (GUI) for future releases.

**Current CLI Implementation:**

The CLI uses a hierarchical menu system with numeric selection. Users navigate through menus by entering numbers corresponding to desired operations. Input validation ensures that invalid entries prompt for re-entry rather than causing errors. All output is formatted as plain text tables with clear headers and separators.

**User Workflow:**

1. **Application Launch:** User executes JAR file; system displays welcome banner and main menu
2. **Menu Navigation:** User enters numeric choice; system validates and routes to appropriate handler
3. **Data Entry:** System prompts for required fields; user enters values with format validation
4. **Operation Execution:** System processes request and displays results or error messages
5. **Return to Menu:** User automatically returns to menu after operation completion

**Input Validation Strategy:**

- **Numeric Fields:** Retry loop until valid integer/decimal entered
- **SSN:** Enforce exactly 9 digits with retry
- **Email:** Regex validation with retry
- **Optional Fields:** Allow empty input to keep current value (update operations)
- **Required Fields:** Retry loop until non-empty value provided

**Output Formatting:**

- **Employee Details:** Labeled fields with clear section headers
- **Lists:** Numbered rows for selection
- **Reports:** ASCII table format with column alignment
- **Errors:** Prefixed with "Error:" or "Database error:" for clarity

**Error Handling:**

- **Database Errors:** Display user-friendly message with operation context
- **Validation Errors:** Display specific field requirement (e.g., "SSN must be exactly 9 digits")
- **Not Found:** Clear message indicating no results for search criteria
- **Transaction Failures:** Rollback indication with error details

### 6.2 Screen Images

**CLI Main Menu:**

```
========================================
  EMPLOYEE MANAGEMENT SYSTEM
========================================

===== MAIN MENU =====
1. Search Employee
2. Update Employee
3. Update Salary by Percentage
4. Reports
0. Exit
Choose an option: _
```

**Employee Search by Name:**

```
--- SEARCH EMPLOYEE ---
Search by:
1. Employee ID
2. SSN
3. Name
0. Back
Choose: 3
Enter name (first or last): Smith

Found 2 employee(s):
1. John Smith (ID: 1)
2. Jane Smith (ID: 17)

Select employee number: 1

========================================
EMPLOYEE INFORMATION
========================================
Employee ID: 1
Name: John Smith
SSN: 123456789
Email: john.smith@company.com
Division: Engineering
Job Title: Software Engineer
========================================
```

**Update Employee:**

```
--- UPDATE EMPLOYEE ---
Find employee by:
1. Employee ID
2. SSN
0. Back
Choose: 1
Enter Employee ID: 1

Current employee information:
========================================
EMPLOYEE INFORMATION
========================================
Employee ID: 1
Name: John Smith
SSN: 123456789
Email: john.smith@company.com
Division: Engineering
Job Title: Software Engineer
========================================

Enter new values (press Enter to keep current value):
First Name [John]: 
Last Name [Smith]: 
SSN [123456789]: 
Email [john.smith@company.com]: john.smith@newcompany.com
Division ID [1]: 
Job Title ID [1]: 2

Employee updated successfully.
Updated information:
========================================
EMPLOYEE INFORMATION
========================================
Employee ID: 1
Name: John Smith
SSN: 123456789
Email: john.smith@newcompany.com
Division: Engineering
Job Title: Senior Software Engineer
========================================
```

**Salary Update by Percentage:**

```
--- UPDATE SALARY BY PERCENTAGE ---
This will increase payroll amounts within a specified range.
Example: 3.2% increase for salaries >= $58,000 and < $105,000

Enter percentage increase (e.g., 3.2 for 3.2%): 3.2
Minimum salary amount (inclusive): $58000
Maximum salary amount (exclusive): $105000

Applying 3.2% increase to payroll amounts
between $58000.00 (inclusive) and $105000.00 (exclusive)...

Updated 47 payroll record(s).
```

**Employee Pay History Report:**

```
--- FULL-TIME EMPLOYEE INFO + PAY HISTORY ---
Find employee by:
1. Employee ID
2. SSN
0. Back
Choose: 1
Enter Employee ID: 1

========================================
EMPLOYEE INFORMATION
========================================
Employee ID: 1
Name: John Smith
SSN: 123456789
Email: john.smith@company.com
Division: Engineering
Job Title: Senior Software Engineer
========================================

PAY HISTORY
========================================
Period Start    Period End      Amount
-----------------------------------------------
2024-06-01      2024-06-15      $  4800.00
2024-12-01      2024-12-15      $  4900.00
2025-01-01      2025-01-15      $  5000.00
2025-01-16      2025-01-31      $  5000.00
2025-07-01      2025-07-15      $  5100.00
2026-01-01      2026-01-15      $  5200.00
2026-09-01      2026-09-15      $  5300.00
========================================
```

**Total Pay by Job Title Report:**

```
--- TOTAL PAY BY JOB TITLE ---
Enter year (e.g., 2025): 2025
Enter month (1-12): 1

========================================
TOTAL PAY BY JOB TITLE - 2025-01
========================================
Job Title                                Total Pay
-----------------------------------------------
Finance Manager                          $ 13600.00
Financial Analyst                        $ 10400.00
HR Coordinator                           $  8400.00
HR Manager                               $ 11600.00
Lead Software Engineer                   $ 16000.00
Marketing Manager                        $ 14000.00
Marketing Specialist                     $  9600.00
Product Manager                          $ 15000.00
Quality Assurance Engineer               $ 11000.00
Sales Manager                            $ 12000.00
Sales Representative                     $ 13500.00
Senior Software Engineer                 $ 13000.00
Software Engineer                        $ 10000.00
-----------------------------------------------
TOTAL                                    $158100.00
========================================
```

**Total Pay by Division Report:**

```
--- TOTAL PAY BY DIVISION ---
Enter year (e.g., 2025): 2025
Enter month (1-12): 1

========================================
TOTAL PAY BY DIVISION - 2025-01
========================================
Division                                 Total Pay
-----------------------------------------------
Engineering                              $ 55000.00
Finance                                  $ 24000.00
Human Resources                          $ 20000.00
Marketing                                $ 23600.00
Sales                                    $ 35500.00
-----------------------------------------------
TOTAL                                    $158100.00
========================================
```

### 6.3 Screen Objects and Actions

**Main Menu (ConsoleUI)**

| Object | Type | Action | Result |
|--------|------|--------|--------|
| Option 1 | Menu Item | Select "Search Employee" | Navigate to EmployeeConsole.searchEmployee() |
| Option 2 | Menu Item | Select "Update Employee" | Navigate to EmployeeConsole.updateEmployee() |
| Option 3 | Menu Item | Select "Update Salary" | Navigate to EmployeeConsole.updateSalaryByPercentage() |
| Option 4 | Menu Item | Select "Reports" | Navigate to ReportConsole.showMenu() |
| Option 0 | Menu Item | Select "Exit" | Terminate application |

**Search Employee Screen (EmployeeConsole)**

| Object | Type | Action | Result |
|--------|------|--------|--------|
| Search Method Selector | Radio Menu | Choose search criteria (ID/SSN/Name) | Prompt for appropriate input |
| Employee ID Input | Integer Field | Enter numeric ID | Query database by ID |
| SSN Input | Text Field (9 digits) | Enter SSN | Query database by SSN |
| Name Input | Text Field | Enter name fragment | Query database with LIKE match |
| Employee List | Selection List | Choose from multiple results | Display selected employee details |
| Employee Details | Display Panel | View employee information | Read-only formatted output |

**Update Employee Screen (EmployeeConsole)**

| Object | Type | Action | Result |
|--------|------|--------|--------|
| Find Method Selector | Radio Menu | Choose search method (ID/SSN) | Locate employee to update |
| Current Values Display | Information Panel | View current employee data | Reference for updates |
| First Name Input | Text Field (Optional) | Enter new first name | Update if non-empty |
| Last Name Input | Text Field (Optional) | Enter new last name | Update if non-empty |
| SSN Input | Text Field (Optional) | Enter new SSN | Validate and update if non-empty |
| Email Input | Text Field (Optional) | Enter new email | Validate and update if non-empty |
| Division ID Input | Integer Field (Required) | Enter division ID | Validate existence and update |
| Job Title ID Input | Integer Field (Required) | Enter job title ID | Validate existence and update |
| Update Confirmation | Display Panel | View updated employee data | Confirmation of changes |

**Salary Update Screen (EmployeeConsole)**

| Object | Type | Action | Result |
|--------|------|--------|--------|
| Percentage Input | Decimal Field | Enter percentage (e.g., 3.2) | Store for calculation |
| Minimum Amount Input | Decimal Field | Enter minimum salary | Lower bound for range |
| Maximum Amount Input | Decimal Field | Enter maximum salary | Upper bound for range |
| Confirmation Message | Display Panel | View parameters | Confirm before execution |
| Result Message | Display Panel | View rows affected | Confirmation of bulk update |

**Reports Menu (ReportConsole)**

| Object | Type | Action | Result |
|--------|------|--------|--------|
| Option 1 | Menu Item | Select "Employee Pay History" | Navigate to showEmployeePayHistory() |
| Option 2 | Menu Item | Select "Total Pay by Job Title" | Navigate to showTotalPayByJobTitle() |
| Option 3 | Menu Item | Select "Total Pay by Division" | Navigate to showTotalPayByDivision() |
| Option 0 | Menu Item | Select "Back" | Return to main menu |

**Employee Pay History Report (ReportConsole)**

| Object | Type | Action | Result |
|--------|------|--------|--------|
| Employee Selector | Search Interface | Find employee by ID/SSN | Identify employee for report |
| Employee Info Panel | Display Panel | View employee details | Context for pay history |
| Pay History Table | Data Grid | View payroll records | Chronological pay period list |

**Aggregated Pay Reports (ReportConsole)**

| Object | Type | Action | Result |
|--------|------|--------|--------|
| Year Input | Integer Field | Enter year (e.g., 2025) | Filter payroll by year |
| Month Input | Integer Field | Enter month (1-12) | Filter payroll by month |
| Report Table | Data Grid | View aggregated totals | Grouped pay amounts |
| Total Row | Summary Row | View grand total | Sum of all groups |

### 6.4 Future JavaFX Interface

**Planned GUI Architecture:**

The future JavaFX implementation will maintain the existing service and DAO layers while replacing the CLI UI layer with a graphical interface. The architecture will follow the Model-View-Controller (MVC) pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                    JavaFX View Layer                         │
│              (FXML files, CSS stylesheets)                   │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────────┐
│                  JavaFX Controller Layer                     │
│    (EmployeeController, ReportController, etc.)             │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────────┐
│                      Service Layer                           │
│            (EmployeeService, ReportService)                  │
│                  (NO CHANGES REQUIRED)                       │
└─────────────────────────────────────────────────────────────┘
```

**Planned GUI Components:**

1. **Main Window (Stage)**
   - Menu bar: File, Edit, Reports, Help
   - Toolbar: Quick access buttons for common operations
   - Status bar: Connection status, operation feedback
   - Content area: Tabbed interface or navigation panel

2. **Employee Management Tab**
   - Search panel: Combo box for search type, text field for input
   - Results table: Sortable, filterable employee list
   - Detail panel: Form with labeled fields for viewing/editing
   - Action buttons: Add, Update, Delete, Refresh

3. **Payroll Management Tab**
   - Employee selector: Dropdown or search field
   - Payroll records table: Chronological list with period and amount
   - Bulk update panel: Percentage, min/max range inputs
   - Action buttons: Add Record, Apply Increase

4. **Reports Tab**
   - Report type selector: Radio buttons or dropdown
   - Parameter panel: Date pickers for year/month selection
   - Results area: TableView with formatted columns
   - Export buttons: PDF, CSV, Print

5. **Dialogs**
   - Add/Edit Employee: Modal dialog with form validation
   - Confirmation dialogs: For delete and bulk update operations
   - Error dialogs: User-friendly error messages with details
   - About dialog: Application version and credits

**JavaFX Technology Stack:**

- **UI Framework:** JavaFX 21 (bundled with JDK 21)
- **Layout:** FXML for declarative UI definition
- **Styling:** CSS for consistent theming
- **Data Binding:** ObservableList for table data
- **Validation:** ControlsFX for enhanced form validation
- **Charts:** JavaFX Charts API for potential pay trend visualization

**Migration Strategy:**

1. **Phase 1:** Create JavaFX controllers that delegate to existing service layer
2. **Phase 2:** Design FXML layouts for main window and tabs
3. **Phase 3:** Implement data binding between UI and models
4. **Phase 4:** Add CSS styling for professional appearance
5. **Phase 5:** Implement advanced features (charts, export, printing)

**Advantages of Planned GUI:**

- **Improved Usability:** Point-and-click navigation, visual feedback
- **Better Data Presentation:** Sortable tables, color-coded status indicators
- **Enhanced Validation:** Real-time field validation with visual cues
- **Multi-Window Support:** View multiple reports or employees simultaneously
- **Export Capabilities:** Generate PDF reports, export to CSV
- **Accessibility:** Keyboard shortcuts, screen reader support

**Design Mockup Concepts:**

*Main Window Layout:*
```
┌────────────────────────────────────────────────────────────┐
│ File  Edit  Reports  Help                                  │
├────────────────────────────────────────────────────────────┤
│ [+] [✎] [🗑] [↻]                                          │
├──────────────┬─────────────────────────────────────────────┤
│ Employees    │  Search: [ID ▼] [_____________] [Search]   │
│ Payroll      │                                             │
│ Reports      │  ┌─────────────────────────────────────┐   │
│ Settings     │  │ ID  Name         SSN       Division │   │
│              │  ├─────────────────────────────────────┤   │
│              │  │ 1   John Smith   123456789 Engineer │   │
│              │  │ 2   Jane Doe     234567890 Sales    │   │
│              │  │ ... (sortable, selectable table)    │   │
│              │  └─────────────────────────────────────┘   │
│              │                                             │
│              │  Employee Details:                          │
│              │  Name: [John Smith____________]             │
│              │  SSN:  [123456789_____________]             │
│              │  Email:[john.smith@company.com]             │
│              │  Div:  [Engineering ▼]                      │
│              │  Title:[Software Engineer ▼]                │
│              │                                             │
│              │  [Update] [Cancel]                          │
├──────────────┴─────────────────────────────────────────────┤
│ Connected to: employee_db@localhost | Last updated: 10:45 │
└────────────────────────────────────────────────────────────┘
```

**No Changes to Business Logic:**

The existing service and DAO layers will remain unchanged. The JavaFX controllers will instantiate service objects and call their methods exactly as the CLI layer does currently. This demonstrates the value of proper layered architecture and separation of concerns.

---

## 7.0 REQUIREMENTS MATRIX

This section traces system components to functional requirements from the Software Requirements Specification (SWRS-EMS-2025-001).

| Requirement ID | Requirement Description | Implementing Components | Verification Method |
|----------------|-------------------------|-------------------------|---------------------|
| **FR-1** | System shall store employee records with SSN, name, email | `employees` table, `Employee` model, `EmployeeDAO` | Database inspection, unit tests |
| **FR-2** | System shall enforce unique SSN per employee | `employees.SSN UNIQUE` constraint, `Employee.validateSSN()` | Constraint violation test, duplicate insert attempt |
| **FR-3** | System shall validate SSN as exactly 9 digits | `Employee.validateSSN()`, `EmployeeConsole.readSSN()` | Unit test with invalid SSNs, UI input test |
| **FR-4** | System shall validate email format | `Employee.validateEmail()` | Unit test with invalid emails |
| **FR-5** | System shall assign each employee to exactly one division | `employee_division` table with PK on `employee_id`, `EmployeeDAO.insert/update` | Foreign key test, cardinality test |
| **FR-6** | System shall assign each employee to exactly one job title | `employee_job_titles` table with PK on `employee_id`, `EmployeeDAO.insert/update` | Foreign key test, cardinality test |
| **FR-7** | System shall support employee search by ID | `EmployeeDAO.findById()`, `EmployeeConsole.searchEmployee()` | Integration test, UI test |
| **FR-8** | System shall support employee search by SSN | `EmployeeDAO.findBySSN()`, `EmployeeConsole.searchEmployee()` | Integration test, UI test |
| **FR-9** | System shall support employee search by name fragment | `EmployeeDAO.findByNameFragment()`, `EmployeeConsole.searchEmployee()` | Integration test with partial names |
| **FR-10** | System shall support updating employee information | `EmployeeDAO.update()`, `EmployeeService.updateEmployee()`, `EmployeeConsole.updateEmployee()` | Integration test, transaction test |
| **FR-11** | System shall validate division existence before assignment | `EmployeeDAO.divisionExists()` | Test with invalid division ID |
| **FR-12** | System shall validate job title existence before assignment | `EmployeeDAO.jobTitleExists()` | Test with invalid job title ID |
| **FR-13** | System shall maintain referential integrity on delete | Foreign key `ON DELETE CASCADE` constraints | Delete employee, verify cascade |
| **FR-14** | System shall record payroll amounts by pay period | `payroll` table, `Payroll` model, `PayrollDAO` | Database inspection, insert test |
| **FR-15** | System shall enforce non-negative payroll amounts | `payroll.amount CHECK (amount >= 0)`, `Payroll` validation | Test with negative amount |
| **FR-16** | System shall prevent duplicate payroll records per period | `uk_payroll_employee_period` unique constraint | Duplicate insert test |
| **FR-17** | System shall support bulk salary increase by percentage | `PayrollDAO.increaseAmountInRange()`, `EmployeeService.increaseSalaryInRange()` | Integration test with sample data |
| **FR-18** | System shall apply salary increase only within specified range | `PayrollDAO` WHERE clause with BETWEEN | Test with amounts inside/outside range |
| **FR-19** | System shall execute bulk updates transactionally | `EmployeeDAO` transaction management with commit/rollback | Test with forced failure, verify rollback |
| **FR-20** | System shall generate employee pay history report | `ReportDAO.getEmployeeWithPayHistory()`, `ReportService`, `ReportConsole` | Integration test, verify JOIN accuracy |
| **FR-21** | System shall aggregate total pay by job title for a month | `ReportDAO.getTotalPayByJobTitle()`, `ReportService`, `ReportConsole` | Integration test, verify SUM and GROUP BY |
| **FR-22** | System shall aggregate total pay by division for a month | `ReportDAO.getTotalPayByDivision()`, `ReportService`, `ReportConsole` | Integration test, verify SUM and GROUP BY |
| **FR-23** | System shall filter reports by year and month | `ReportDAO` WHERE clauses with YEAR() and MONTH() | Test with multiple years/months |
| **FR-24** | System shall display employee details with division and job title names | `ReportDAO` JOIN queries, `EmployeeConsole.printEmployeeDetails()` | Integration test, verify name resolution |
| **FR-25** | System shall provide CLI menu navigation | `ConsoleUI`, `EmployeeConsole`, `ReportConsole` | Manual UI test, navigation flow |
| **FR-26** | System shall validate user input with retry on error | `ConsoleUI.readInt()`, `readDouble()`, `readSSN()`, etc. | UI test with invalid inputs |
| **FR-27** | System shall display user-friendly error messages | Exception handling in all UI classes | Test with various error conditions |
| **FR-28** | System shall connect to MySQL database via environment variables | `DatabaseConnectionManager`, environment variable reading | Test with missing/invalid variables |
| **FR-29** | System shall retry database connection on failure | `DatabaseConnectionManager.testConnection()` | Test with database temporarily down |
| **FR-30** | System shall initialize database schema from SQL scripts | `DatabaseInit.initializeSchema()` | Test with empty database |
| **FR-31** | System shall load sample data from SQL scripts | `DatabaseInit.loadSampleData()` | Test with empty tables |
| **FR-32** | System shall handle idempotent schema/data scripts | `CREATE TABLE IF NOT EXISTS`, `INSERT IGNORE`, `ON DUPLICATE KEY UPDATE` | Run scripts multiple times |
| **FR-33** | System shall use prepared statements for all queries | All DAO implementations | Code review, SQL injection test |
| **FR-34** | System shall escape LIKE wildcards in search queries | `EmployeeDAO.escapeLikeWildcards()` | Test with '%' and '_' in input |
| **FR-35** | System shall close database resources properly | Try-with-resources in all DAOs | Resource leak test, connection pool monitoring |
| **FR-36** | System shall track record creation and modification timestamps | `created_at`, `updated_at` columns in all tables | Database inspection after insert/update |
| **FR-37** | System shall build as executable JAR with dependencies | Maven Shade Plugin configuration | Build and execute JAR |
| **FR-38** | System shall run unit tests during build | Maven Surefire Plugin, JUnit 5 tests | Execute `mvn test` |

**Non-Functional Requirements:**

| Requirement ID | Requirement Description | Implementing Components | Verification Method |
|----------------|-------------------------|-------------------------|---------------------|
| **NFR-1** | System shall use Java 21 language features | Project configuration, source code | Compiler version check |
| **NFR-2** | System shall use MySQL 9.0 with InnoDB | Database schema, connection string | Database version query |
| **NFR-3** | System shall follow layered architecture | Package structure, class dependencies | Architecture review, dependency analysis |
| **NFR-4** | System shall separate UI from business logic | Service layer abstraction | Code review, layer violation test |
| **NFR-5** | System shall use DAO pattern for data access | DAO interfaces and implementations | Code review, pattern compliance |
| **NFR-6** | System shall validate data in model classes | Model validation methods | Unit tests for validation |
| **NFR-7** | System shall use Maven for build automation | `pom.xml`, Maven commands | Build execution, dependency resolution |
| **NFR-8** | System shall maintain code style consistency | `.cursorrules` configuration | Code review, style checker |
| **NFR-9** | System shall document public APIs with JavaDoc | JavaDoc comments on public methods | JavaDoc generation, completeness check |
| **NFR-10** | System shall handle exceptions gracefully | Try-catch blocks, exception translation | Error condition tests |

**Coverage Summary:**

- **Total Functional Requirements:** 38
- **Total Non-Functional Requirements:** 10
- **Total Components:** 25+ (classes and database objects)
- **Coverage:** 100% of requirements mapped to implementing components

---

## 8.0 APPENDICES

### Appendix A: Database Schema Diagram

```
┌─────────────────────┐
│     employees       │
├─────────────────────┤
│ PK employee_id      │
│    first_name       │
│    last_name        │
│ UK SSN              │
│    email            │
│    created_at       │
│    updated_at       │
└──────────┬──────────┘
           │
           │ 1
           │
           ├──────────────────────────────────────┐
           │                                      │
           │ 1                                    │ 1
           │                                      │
┌──────────┴──────────┐              ┌───────────┴─────────┐
│ employee_division   │              │ employee_job_titles │
├─────────────────────┤              ├─────────────────────┤
│ PK,FK employee_id   │              │ PK,FK employee_id   │
│ FK    division_id   │              │ FK    job_title_id  │
│       created_at    │              │       created_at    │
│       updated_at    │              │       updated_at    │
└──────────┬──────────┘              └───────────┬─────────┘
           │                                     │
           │ 1                                   │ 1
           │                                     │
           │ *                                   │ *
           │                                     │
┌──────────┴──────────┐              ┌───────────┴─────────┐
│     division        │              │     job_titles      │
├─────────────────────┤              ├─────────────────────┤
│ PK division_id      │              │ PK job_title_id     │
│    name             │              │    title            │
│    created_at       │              │    created_at       │
│    updated_at       │              │    updated_at       │
└─────────────────────┘              └─────────────────────┘

┌─────────────────────┐
│      payroll        │
├─────────────────────┤
│ PK payroll_id       │
│ FK employee_id      │
│    amount           │
│    pay_period_start │
│    pay_period_end   │
│    created_at       │
│    updated_at       │
└──────────┬──────────┘
           │
           │ *
           │
           │ 1
           │
      (employees)
```

### Appendix B: Class Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         App                                  │
│  + main(String[] args): void                                │
└──────────────────────────┬──────────────────────────────────┘
                           │ creates
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                       ConsoleUI                              │
│  - scanner: Scanner                                          │
│  - employeeConsole: EmployeeConsole                          │
│  - reportConsole: ReportConsole                              │
│  + start(): void                                             │
│  - printMainMenu(): void                                     │
│  - readInt(String): int                                      │
└──────────────────────────┬──────────────────────────────────┘
                           │ uses
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    EmployeeService                           │
│  - employeeDAO: EmployeeDAO                                  │
│  - payrollDAO: PayrollDAO                                    │
│  + addEmployee(Employee, int, int): Employee                 │
│  + findById(int): Optional<Employee>                         │
│  + findBySSN(String): Optional<Employee>                     │
│  + findByName(String): List<Employee>                        │
│  + updateEmployee(Employee, int, int): boolean               │
│  + increaseSalaryInRange(BigDecimal, BigDecimal, ...): int   │
└──────────────────────────┬──────────────────────────────────┘
                           │ uses
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      EmployeeDAO                             │
│  - dbManager: DatabaseConnectionManager                      │
│  + insert(Employee, int, int): Employee                      │
│  + findById(int): Optional<Employee>                         │
│  + findBySSN(String): Optional<Employee>                     │
│  + findByNameFragment(String): List<Employee>                │
│  + update(Employee, int, int): boolean                       │
│  + delete(int): boolean                                      │
│  - divisionExists(Connection, int): boolean                  │
│  - jobTitleExists(Connection, int): boolean                  │
│  - escapeLikeWildcards(String): String                       │
└──────────────────────────┬──────────────────────────────────┘
                           │ uses
                           ▼
┌─────────────────────────────────────────────────────────────┐
│               DatabaseConnectionManager                      │
│  - instance: DatabaseConnectionManager (static)              │
│  - dbHost: String                                            │
│  - dbPort: String                                            │
│  - dbName: String                                            │
│  - dbUser: String                                            │
│  - dbPassword: String                                        │
│  - jdbcUrl: String                                           │
│  + getInstance(): DatabaseConnectionManager (static)         │
│  + getConnection(): Connection                               │
│  - testConnection(): boolean                                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                        Employee                              │
│  - employeeId: Integer                                       │
│  - firstName: String                                         │
│  - lastName: String                                          │
│  - ssn: String                                               │
│  - email: String                                             │
│  - divisionId: Integer                                       │
│  - jobTitleId: Integer                                       │
│  + Employee()                                                │
│  + Employee(String, String, String, String)                  │
│  + getters/setters                                           │
│  + validateSSN(): void                                       │
│  + validateEmail(): void                                     │
│  + toString(): String                                        │
└─────────────────────────────────────────────────────────────┘

(Similar structure for Division, JobTitle, Payroll, DTOs)
```

### Appendix C: Sequence Diagram - Update Employee

```
User -> ConsoleUI: Select "Update Employee"
ConsoleUI -> EmployeeConsole: updateEmployee()
EmployeeConsole -> User: Prompt for search method
User -> EmployeeConsole: Enter employee ID
EmployeeConsole -> EmployeeService: findById(id)
EmployeeService -> EmployeeDAO: findById(id)
EmployeeDAO -> Database: SELECT with JOINs
Database -> EmployeeDAO: ResultSet
EmployeeDAO -> EmployeeService: Optional<Employee>
EmployeeService -> EmployeeConsole: Optional<Employee>
EmployeeConsole -> User: Display current employee details
EmployeeConsole -> User: Prompt for new values
User -> EmployeeConsole: Enter new email, division ID, job title ID
EmployeeConsole -> EmployeeService: updateEmployee(emp, divId, jobId)
EmployeeService -> EmployeeDAO: update(emp, divId, jobId)
EmployeeDAO -> Database: BEGIN TRANSACTION
EmployeeDAO -> Database: UPDATE employees
EmployeeDAO -> Database: DELETE FROM employee_division
EmployeeDAO -> Database: INSERT INTO employee_division
EmployeeDAO -> Database: DELETE FROM employee_job_titles
EmployeeDAO -> Database: INSERT INTO employee_job_titles
EmployeeDAO -> Database: COMMIT
Database -> EmployeeDAO: Success
EmployeeDAO -> EmployeeService: true
EmployeeService -> EmployeeConsole: true
EmployeeConsole -> EmployeeService: findById(id)
EmployeeService -> EmployeeDAO: findById(id)
EmployeeDAO -> Database: SELECT with JOINs
Database -> EmployeeDAO: ResultSet
EmployeeDAO -> EmployeeService: Optional<Employee>
EmployeeService -> EmployeeConsole: Optional<Employee>
EmployeeConsole -> User: Display updated employee details
```

### Appendix D: Technology Versions

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 (LTS) | Runtime and language |
| MySQL | 9.0 | Database server |
| MySQL Connector/J | 9.0.0 | JDBC driver |
| Maven | 3.9.11 | Build automation |
| JUnit Jupiter | 5.10.1 | Unit testing |
| Maven Compiler Plugin | 3.11.0 | Java compilation |
| Maven Shade Plugin | 3.5.1 | Uber-JAR packaging |
| Maven Surefire Plugin | 3.2.2 | Test execution |

### Appendix E: Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| DB_HOST | Yes | localhost | MySQL server hostname or IP |
| DB_PORT | Yes | 3306 | MySQL server port |
| DB_NAME | Yes |  | Database name (e.g., employee_db) |
| DB_USER | Yes | - | Database username |
| DB_PASSWORD | Yes | - | Database password |

**Example Configuration:**

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=employee_db
export DB_USER=emp_user
export DB_PASSWORD=secure_password
```

### Appendix F: Setup and Build Commands

**Prerequisites:**
- Java 21 or higher
- Maven 3.6 or higher
- MySQL 9.0 or higher
- Git

**Step 1: Clone Repository**
```bash
git clone https://github.com/1DeepakSrinivas/SWD-Project.git
cd SWD-Project
```

**Step 2: Make Scripts Executable**
```bash
chmod +x ./src/db/start-mysql.sh
```

**Step 3: Start MySQL Server**
```bash
./src/db/start-mysql.sh
```

The script reads configuration from `.env` file. If MySQL is already running, it will detect and skip startup.

**Alternative (macOS Homebrew):**
```bash
brew services start mysql
```

**Step 4: Create Database**
```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS emp_mgmt;"
```

**Step 5: Configure Environment**
Copy the `.env.example` file and update the details (preferred approach):

```bash
cp .env.example .env
```

or manually create `.env` file in project root directory:
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=emp_mgmt
DB_USER=root
DB_PASS=your_password
```

If password is empty, set `DB_PASS=`. Replace `your_password` with your MySQL root password.

**Step 6: Initialize Database Schema and Data**
```bash
mvn compile exec:java -Dexec.mainClass="com.emp_mgmt.db.DatabaseInit"
```

This executes `DatabaseInit.java`, which:
- Creates all tables with proper constraints
- Loads sample data
- Handles idempotent execution (safe to run multiple times)

**Alternative (Manual SQL Execution):**
```bash
mysql -u root -p emp_mgmt < src/db/schema.sql
mysql -u root -p emp_mgmt < src/db/sample-data.sql
```

**Step 7: Verify Database Connection (Optional)**
```bash
mvn compile exec:java -Dexec.mainClass="com.emp_mgmt.db.DatabaseConnectionManager"
```

This tests the connection and displays database information.

**Step 8: Build Project**
```bash
mvn clean compile
```

**Package JAR:**
```bash
mvn clean package
```

**Run Tests:**
```bash
mvn test
```

**Run Application:**
```bash
java -jar target/employee-management-1.0.0.jar
```

**Setup Order Verification:**

The setup order is critical:
1. **MySQL must be running** before `DatabaseInit` can execute (Step 3 before Step 6)
2. **Database must exist** before `DatabaseInit` can create tables (Step 4 before Step 6)
3. **`.env` file should be configured** before `DatabaseInit` runs, though defaults are available (Step 5 before Step 6)
4. **Database initialization must complete** before building/running the application (Step 6 before Step 8)
5. **Connection verification** is optional but recommended after initialization (Step 7)

**Troubleshooting:**

If `DatabaseInit` fails:
- Verify MySQL is running: `mysqladmin ping -h localhost -P 3306 -u root`
- Check database exists: `mysql -u root -p -e "SHOW DATABASES;"`
- Verify `.env` file has correct credentials
- Check file paths: `DatabaseInit` looks for `src/db/schema.sql` and `src/db/sample-data.sql` relative to project root

If connection test fails:
- Verify MySQL server is accessible on configured host/port
- Check credentials in `.env` file
- Ensure database `emp_mgmt` exists

### Appendix G: Future Enhancements

**Planned Features:**

1. **JavaFX GUI:** Graphical user interface with forms, tables, and charts
2. **Connection Pooling:** HikariCP for improved database performance
3. **Logging Framework:** SLF4J with Logback for structured logging
4. **Configuration Management:** Properties files or YAML for environment-specific settings
5. **Authentication:** User login with role-based access control
6. **Audit Trail:** Detailed logging of all data modifications
7. **Export Functionality:** PDF and CSV export for reports
8. **Advanced Reporting:** Trend analysis, charts, and custom report builder
9. **REST API:** Expose functionality as RESTful web services
10. **Docker Deployment:** Containerized application with Docker Compose

**Technical Debt:**

1. Add comprehensive unit test coverage (target: 80%+)
2. Implement integration tests for multi-DAO operations
3. Add performance benchmarks for large datasets
4. Implement connection pooling for concurrent access
5. Add input sanitization for all user-provided strings
6. Implement proper logging instead of System.out.println
7. Add configuration validation on startup
8. Implement graceful shutdown with resource cleanup

### Appendix H: References

1. IEEE Computer Society. (2009). *IEEE Std 1016-2009: IEEE Standard for Information Technology—Systems Design—Software Design Descriptions*. IEEE.

2. Oracle Corporation. (2023). *Java SE 21 Documentation*. Retrieved from https://docs.oracle.com/en/java/javase/21/

3. Oracle Corporation. (2024). *MySQL 9.0 Reference Manual*. Retrieved from https://dev.mysql.com/doc/refman/9.0/en/

4. Apache Software Foundation. (2024). *Maven – Welcome to Apache Maven*. Retrieved from https://maven.apache.org/

5. JUnit Team. (2024). *JUnit 5 User Guide*. Retrieved from https://junit.org/junit5/docs/current/user-guide/

6. Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.

7. Fowler, M. (2002). *Patterns of Enterprise Application Architecture*. Addison-Wesley.

8. Bloch, J. (2018). *Effective Java* (3rd ed.). Addison-Wesley.

---

**Document Control:**

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0.0 | 2025-11-30 | Development Team | Initial release |

**Approval:**

This document has been reviewed and approved for use in the Employee Management System project.

---

*End of Software Design Document*
