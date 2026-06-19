-- Employee Operations SQL Scripts
-- Centralized SQL queries for employee management operations

-- Employee Operations
-- Insert new employee
INSERT INTO employees (first_name, last_name, SSN, email)
VALUES (?, ?, ?, ?);

-- Update employee
UPDATE employees
SET first_name = ?, last_name = ?, SSN = ?, email = ?
WHERE employee_id = ?;

-- Delete employee
DELETE FROM employees WHERE employee_id = ?;

-- Find employee by ID
SELECT employee_id, first_name, last_name, SSN, email
FROM employees
WHERE employee_id = ?;

-- Find all employees
SELECT employee_id, first_name, last_name, SSN, email
FROM employees
ORDER BY last_name, first_name;

-- Find employee by SSN
SELECT employee_id, first_name, last_name, SSN, email
FROM employees
WHERE SSN = ?;

-- Search employees by name
SELECT employee_id, first_name, last_name, SSN, email
FROM employees
WHERE first_name LIKE ? OR last_name LIKE ?
ORDER BY last_name, first_name;

-- Update salary by percentage (transactional)
UPDATE payroll
SET amount = amount * (1 + ? / 100)
WHERE amount BETWEEN ? AND ?;

-- Payroll Operations
-- Insert payroll record
INSERT INTO payroll (employee_id, amount, pay_period_start, pay_period_end)
VALUES (?, ?, ?, ?);

-- Update payroll record
UPDATE payroll
SET employee_id = ?, amount = ?, pay_period_start = ?, pay_period_end = ?
WHERE payroll_id = ?;

-- Delete payroll record
DELETE FROM payroll WHERE payroll_id = ?;

-- Find payroll by ID
SELECT payroll_id, employee_id, amount, pay_period_start, pay_period_end
FROM payroll
WHERE payroll_id = ?;

-- Find all payrolls
SELECT payroll_id, employee_id, amount, pay_period_start, pay_period_end
FROM payroll
ORDER BY pay_period_start DESC, employee_id;

-- Find payrolls by employee ID
SELECT payroll_id, employee_id, amount, pay_period_start, pay_period_end
FROM payroll
WHERE employee_id = ?
ORDER BY pay_period_start DESC;

-- Division Operations
-- Insert division
INSERT INTO division (name) VALUES (?);

-- Update division
UPDATE division
SET name = ?
WHERE division_id = ?;

-- Delete division
DELETE FROM division WHERE division_id = ?;

-- Find division by ID
SELECT division_id, name
FROM division
WHERE division_id = ?;

-- Find all divisions
SELECT division_id, name
FROM division
ORDER BY name;

-- Job Title Operations
-- Insert job title
INSERT INTO job_titles (title) VALUES (?);

-- Update job title
UPDATE job_titles
SET title = ?
WHERE job_title_id = ?;

-- Delete job title
DELETE FROM job_titles WHERE job_title_id = ?;

-- Find job title by ID
SELECT job_title_id, title
FROM job_titles
WHERE job_title_id = ?;

-- Find all job titles
SELECT job_title_id, title
FROM job_titles
ORDER BY title;

-- Employee-Division Relationship Operations
-- Insert employee-division relationship
INSERT INTO employee_division (employee_id, division_id)
VALUES (?, ?);

-- Delete employee-division relationship
DELETE FROM employee_division
WHERE employee_id = ? AND division_id = ?;

-- Find employee-division by IDs
SELECT employee_id, division_id
FROM employee_division
WHERE employee_id = ? AND division_id = ?;

-- Find all employee-division relationships
SELECT employee_id, division_id
FROM employee_division
ORDER BY employee_id, division_id;

-- Find divisions by employee ID
SELECT employee_id, division_id
FROM employee_division
WHERE employee_id = ?;

-- Find employees by division ID
SELECT employee_id, division_id
FROM employee_division
WHERE division_id = ?;

-- Employee-JobTitle Relationship Operations
-- Insert employee-job title relationship
INSERT INTO employee_job_titles (employee_id, job_title_id)
VALUES (?, ?);

-- Delete employee-job title relationship
DELETE FROM employee_job_titles
WHERE employee_id = ? AND job_title_id = ?;

-- Find employee-job title by IDs
SELECT employee_id, job_title_id
FROM employee_job_titles
WHERE employee_id = ? AND job_title_id = ?;

-- Find all employee-job title relationships
SELECT employee_id, job_title_id
FROM employee_job_titles
ORDER BY employee_id, job_title_id;

-- Find job titles by employee ID
SELECT employee_id, job_title_id
FROM employee_job_titles
WHERE employee_id = ?;

-- Find employees by job title ID
SELECT employee_id, job_title_id
FROM employee_job_titles
WHERE job_title_id = ?;

