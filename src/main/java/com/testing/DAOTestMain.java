package com.testing;

import com.employeemgmt.dao.DivisionDAO;
import com.employeemgmt.dao.DivisionDAOImpl;
import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.dao.EmployeeDAOImpl;
import com.employeemgmt.dao.JobTitleDAO;
import com.employeemgmt.dao.JobTitleDAOImpl;
import com.employeemgmt.dao.PayrollDAO;
import com.employeemgmt.dao.PayrollDAOImpl;
import com.employeemgmt.model.Division;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.JobTitle;
import com.employeemgmt.model.Payroll;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class DAOTestMain {

    public static void main(String[] args) {
        try {
            testEmployeeDAO();
            testDivisionDAO();
            testJobTitleDAO();
            testPayrollDAO();
            System.out.println("\nAll DAO tests completed successfully!");
        } catch (SQLException e) {
            System.err.println("DAO test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testEmployeeDAO() throws SQLException {
        System.out.println("\n=== Testing EmployeeDAO ===");
        EmployeeDAO employeeDAO = new EmployeeDAOImpl();

        Employee newEmployee = new Employee("John", "Doe", "123456789", "john.doe@test.com");
        System.out.println("Inserting employee: " + newEmployee);
        Employee inserted = employeeDAO.insert(newEmployee);
        System.out.println("Inserted employee with ID: " + inserted.getEmployeeId());

        Employee found = employeeDAO.findById(inserted.getEmployeeId()).orElse(null);
        System.out.println("Found employee: " + found);

        found.setEmail("john.doe.updated@test.com");
        boolean updated = employeeDAO.update(found);
        System.out.println("Update result: " + updated);

        Employee updatedEmployee = employeeDAO.findById(found.getEmployeeId()).orElse(null);
        System.out.println("Updated employee: " + updatedEmployee);

        Employee foundBySSN = employeeDAO.findBySSN("123456789").orElse(null);
        System.out.println("Found by SSN: " + foundBySSN);

        System.out.println("Search by name 'John':");
        employeeDAO.searchByName("John").forEach(System.out::println);
    }

    private static void testDivisionDAO() throws SQLException {
        System.out.println("\n=== Testing DivisionDAO ===");
        DivisionDAO divisionDAO = new DivisionDAOImpl();

        Division newDivision = new Division("Engineering");
        System.out.println("Inserting division: " + newDivision);
        Division inserted = divisionDAO.insert(newDivision);
        System.out.println("Inserted division with ID: " + inserted.getDivisionId());

        Division found = divisionDAO.findById(inserted.getDivisionId()).orElse(null);
        System.out.println("Found division: " + found);

        found.setName("Engineering - Updated");
        boolean updated = divisionDAO.update(found);
        System.out.println("Update result: " + updated);

        Division updatedDivision = divisionDAO.findById(found.getDivisionId()).orElse(null);
        System.out.println("Updated division: " + updatedDivision);
    }

    private static void testJobTitleDAO() throws SQLException {
        System.out.println("\n=== Testing JobTitleDAO ===");
        JobTitleDAO jobTitleDAO = new JobTitleDAOImpl();

        JobTitle newJobTitle = new JobTitle("Software Engineer");
        System.out.println("Inserting job title: " + newJobTitle);
        JobTitle inserted = jobTitleDAO.insert(newJobTitle);
        System.out.println("Inserted job title with ID: " + inserted.getJobTitleId());

        JobTitle found = jobTitleDAO.findById(inserted.getJobTitleId()).orElse(null);
        System.out.println("Found job title: " + found);

        found.setTitle("Senior Software Engineer");
        boolean updated = jobTitleDAO.update(found);
        System.out.println("Update result: " + updated);

        JobTitle updatedJobTitle = jobTitleDAO.findById(found.getJobTitleId()).orElse(null);
        System.out.println("Updated job title: " + updatedJobTitle);
    }

    private static void testPayrollDAO() throws SQLException {
        System.out.println("\n=== Testing PayrollDAO ===");
        PayrollDAO payrollDAO = new PayrollDAOImpl();
        EmployeeDAO employeeDAO = new EmployeeDAOImpl();

        Employee employee = employeeDAO.findAll().stream().findFirst().orElse(null);
        if (employee == null) {
            System.out.println("No employees found, creating one...");
            employee = employeeDAO.insert(new Employee("Test", "Employee", "999999999", "test@test.com"));
        }

        Payroll newPayroll = new Payroll(
            employee.getEmployeeId(),
            new BigDecimal("5000.00"),
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 15)
        );
        System.out.println("Inserting payroll: " + newPayroll);
        Payroll inserted = payrollDAO.insert(newPayroll);
        System.out.println("Inserted payroll with ID: " + inserted.getPayrollId());

        Payroll found = payrollDAO.findById(inserted.getPayrollId()).orElse(null);
        System.out.println("Found payroll: " + found);

        found.setAmount(new BigDecimal("5500.00"));
        boolean updated = payrollDAO.update(found);
        System.out.println("Update result: " + updated);

        Payroll updatedPayroll = payrollDAO.findById(found.getPayrollId()).orElse(null);
        System.out.println("Updated payroll: " + updatedPayroll);

        System.out.println("\nTesting updateSalaryByPercentage:");
        int rowsAffected = employeeDAO.updateSalaryByPercentage(5.0, new BigDecimal("4000"), new BigDecimal("6000"));
        System.out.println("Rows affected by salary update: " + rowsAffected);
    }
}

