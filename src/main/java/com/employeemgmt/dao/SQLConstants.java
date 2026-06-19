package com.employeemgmt.dao;

final class SQLConstants {

    private SQLConstants() {
    }

    static final class Employee {
        static final String INSERT = """
            INSERT INTO employees (first_name, last_name, SSN, email)
            VALUES (?, ?, ?, ?)
            """;

        static final String UPDATE = """
            UPDATE employees
            SET first_name = ?, last_name = ?, SSN = ?, email = ?
            WHERE employee_id = ?
            """;

        static final String DELETE = "DELETE FROM employees WHERE employee_id = ?";

        static final String FIND_BY_ID = """
            SELECT employee_id, first_name, last_name, SSN, email
            FROM employees
            WHERE employee_id = ?
            """;

        static final String FIND_ALL = """
            SELECT employee_id, first_name, last_name, SSN, email
            FROM employees
            ORDER BY last_name, first_name
            """;

        static final String FIND_BY_SSN = """
            SELECT employee_id, first_name, last_name, SSN, email
            FROM employees
            WHERE SSN = ?
            """;

        static final String SEARCH_BY_NAME = """
            SELECT employee_id, first_name, last_name, SSN, email
            FROM employees
            WHERE first_name LIKE ? OR last_name LIKE ?
            ORDER BY last_name, first_name
            """;

        static final String UPDATE_SALARY_BY_PERCENTAGE = """
            UPDATE payroll
            SET amount = amount * (1 + ? / 100)
            WHERE amount BETWEEN ? AND ?
            """;
    }

    static final class Payroll {
        static final String INSERT = """
            INSERT INTO payroll (employee_id, amount, pay_period_start, pay_period_end)
            VALUES (?, ?, ?, ?)
            """;

        static final String UPDATE = """
            UPDATE payroll
            SET employee_id = ?, amount = ?, pay_period_start = ?, pay_period_end = ?
            WHERE payroll_id = ?
            """;

        static final String DELETE = "DELETE FROM payroll WHERE payroll_id = ?";

        static final String FIND_BY_ID = """
            SELECT payroll_id, employee_id, amount, pay_period_start, pay_period_end
            FROM payroll
            WHERE payroll_id = ?
            """;

        static final String FIND_ALL = """
            SELECT payroll_id, employee_id, amount, pay_period_start, pay_period_end
            FROM payroll
            ORDER BY pay_period_start DESC, employee_id
            """;

        static final String FIND_BY_EMPLOYEE_ID = """
            SELECT payroll_id, employee_id, amount, pay_period_start, pay_period_end
            FROM payroll
            WHERE employee_id = ?
            ORDER BY pay_period_start DESC
            """;

        static final String FIND_BY_AMOUNT_RANGE = """
            SELECT payroll_id, employee_id, amount, pay_period_start, pay_period_end
            FROM payroll
            WHERE amount BETWEEN ? AND ?
            ORDER BY employee_id, pay_period_start DESC
            """;
    }

    static final class Division {
        static final String INSERT = "INSERT INTO division (name) VALUES (?)";

        static final String UPDATE = """
            UPDATE division
            SET name = ?
            WHERE division_id = ?
            """;

        static final String DELETE = "DELETE FROM division WHERE division_id = ?";

        static final String FIND_BY_ID = """
            SELECT division_id, name
            FROM division
            WHERE division_id = ?
            """;

        static final String FIND_ALL = """
            SELECT division_id, name
            FROM division
            ORDER BY name
            """;
    }

    static final class JobTitle {
        static final String INSERT = "INSERT INTO job_titles (title) VALUES (?)";

        static final String UPDATE = """
            UPDATE job_titles
            SET title = ?
            WHERE job_title_id = ?
            """;

        static final String DELETE = "DELETE FROM job_titles WHERE job_title_id = ?";

        static final String FIND_BY_ID = """
            SELECT job_title_id, title
            FROM job_titles
            WHERE job_title_id = ?
            """;

        static final String FIND_ALL = """
            SELECT job_title_id, title
            FROM job_titles
            ORDER BY title
            """;
    }

    static final class EmployeeDivision {
        static final String INSERT = """
            INSERT INTO employee_division (employee_id, division_id)
            VALUES (?, ?)
            """;

        static final String DELETE = """
            DELETE FROM employee_division
            WHERE employee_id = ? AND division_id = ?
            """;

        static final String DELETE_BY_EMPLOYEE = """
            DELETE FROM employee_division
            WHERE employee_id = ?
            """;

        static final String DELETE_BY_DIVISION = """
            DELETE FROM employee_division
            WHERE division_id = ?
            """;

        static final String FIND_BY_ID = """
            SELECT employee_id, division_id
            FROM employee_division
            WHERE employee_id = ? AND division_id = ?
            """;

        static final String FIND_ALL = """
            SELECT employee_id, division_id
            FROM employee_division
            ORDER BY employee_id, division_id
            """;

        static final String FIND_BY_EMPLOYEE_ID = """
            SELECT employee_id, division_id
            FROM employee_division
            WHERE employee_id = ?
            """;

        static final String FIND_BY_DIVISION_ID = """
            SELECT employee_id, division_id
            FROM employee_division
            WHERE division_id = ?
            """;
    }

    static final class EmployeeJobTitle {
        static final String INSERT = """
            INSERT INTO employee_job_titles (employee_id, job_title_id)
            VALUES (?, ?)
            """;

        static final String DELETE = """
            DELETE FROM employee_job_titles
            WHERE employee_id = ? AND job_title_id = ?
            """;

        static final String DELETE_BY_EMPLOYEE = """
            DELETE FROM employee_job_titles
            WHERE employee_id = ?
            """;

        static final String DELETE_BY_JOB_TITLE = """
            DELETE FROM employee_job_titles
            WHERE job_title_id = ?
            """;

        static final String FIND_BY_ID = """
            SELECT employee_id, job_title_id
            FROM employee_job_titles
            WHERE employee_id = ? AND job_title_id = ?
            """;

        static final String FIND_ALL = """
            SELECT employee_id, job_title_id
            FROM employee_job_titles
            ORDER BY employee_id, job_title_id
            """;

        static final String FIND_BY_EMPLOYEE_ID = """
            SELECT employee_id, job_title_id
            FROM employee_job_titles
            WHERE employee_id = ?
            """;

        static final String FIND_BY_JOB_TITLE_ID = """
            SELECT employee_id, job_title_id
            FROM employee_job_titles
            WHERE job_title_id = ?
            """;
    }
}

