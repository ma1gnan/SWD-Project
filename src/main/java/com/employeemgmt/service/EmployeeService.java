package com.employeemgmt.service;

import com.employeemgmt.dao.DivisionDAO;
import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.dao.EmployeeDivisionDAO;
import com.employeemgmt.dao.EmployeeJobTitleDAO;
import com.employeemgmt.dao.JobTitleDAO;
import com.employeemgmt.dao.PayrollDAO;
import com.employeemgmt.model.Division;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.EmployeeDivision;
import com.employeemgmt.model.EmployeeJobTitle;
import com.employeemgmt.model.JobTitle;
import com.employeemgmt.model.Payroll;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final DivisionDAO divisionDAO;
    private final JobTitleDAO jobTitleDAO;
    private final EmployeeDivisionDAO employeeDivisionDAO;
    private final EmployeeJobTitleDAO employeeJobTitleDAO;
    private final PayrollDAO payrollDAO;

    public EmployeeService(EmployeeDAO employeeDAO,
                           DivisionDAO divisionDAO,
                           JobTitleDAO jobTitleDAO,
                           EmployeeDivisionDAO employeeDivisionDAO,
                           EmployeeJobTitleDAO employeeJobTitleDAO,
                           PayrollDAO payrollDAO) {
        this.employeeDAO = employeeDAO;
        this.divisionDAO = divisionDAO;
        this.jobTitleDAO = jobTitleDAO;
        this.employeeDivisionDAO = employeeDivisionDAO;
        this.employeeJobTitleDAO = employeeJobTitleDAO;
        this.payrollDAO = payrollDAO;
    }

    // --- Create employee ---
    public Employee addEmployee(Employee employee, int divisionId, int jobTitleId) throws SQLException {
        // Insert the employee first to get the generated employee ID
        Employee inserted = employeeDAO.insert(employee);

        if (inserted.getEmployeeId() == null) {
            throw new SQLException("Failed to obtain generated employee ID after insert");
        }

        // Create division relationship
        EmployeeDivision newDivision = new EmployeeDivision(inserted.getEmployeeId(), divisionId);
        employeeDivisionDAO.insert(newDivision);

        // Create job title relationship
        EmployeeJobTitle newJobTitle = new EmployeeJobTitle(inserted.getEmployeeId(), jobTitleId);
        employeeJobTitleDAO.insert(newJobTitle);

        // Enrich employee with division and job title names for immediate display
        enrichEmployeeWithDivisionAndJobTitle(inserted);

        return inserted;
    }

    // --- Lookups ---

    public Optional<Employee> findById(int id) throws SQLException {
        Optional<Employee> employee = employeeDAO.findById(id);
        if (employee.isPresent()) {
            enrichEmployeeWithDivisionAndJobTitle(employee.get());
        }
        return employee;
    }

    public Optional<Employee> findBySSN(String ssn) throws SQLException {
        Optional<Employee> employee = employeeDAO.findBySSN(ssn);
        if (employee.isPresent()) {
            enrichEmployeeWithDivisionAndJobTitle(employee.get());
        }
        return employee;
    }

    public List<Employee> findByNameFragment(String fragment) throws SQLException {
        List<Employee> employees = employeeDAO.searchByName(fragment);
        for (Employee employee : employees) {
            enrichEmployeeWithDivisionAndJobTitle(employee);
        }
        return employees;
    }

    private void enrichEmployeeWithDivisionAndJobTitle(Employee employee) throws SQLException {
        if (employee.getEmployeeId() == null) {
            return;
        }

        List<EmployeeDivision> employeeDivisions = employeeDivisionDAO.findByEmployeeId(employee.getEmployeeId());
        if (!employeeDivisions.isEmpty()) {
            EmployeeDivision empDiv = employeeDivisions.get(0);
            Optional<Division> division = divisionDAO.findById(empDiv.getDivisionId());
            division.ifPresent(d -> employee.setDivisionName(d.getName()));
        }

        List<EmployeeJobTitle> employeeJobTitles = employeeJobTitleDAO.findByEmployeeId(employee.getEmployeeId());
        if (!employeeJobTitles.isEmpty()) {
            EmployeeJobTitle empJob = employeeJobTitles.get(0);
            Optional<JobTitle> jobTitle = jobTitleDAO.findById(empJob.getJobTitleId());
            jobTitle.ifPresent(jt -> employee.setJobTitleName(jt.getTitle()));
        }
    }

    // --- Update / delete ---

    public boolean updateEmployee(Employee employee, int divisionId, int jobTitleId) throws SQLException {
        if (employee.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID is required for update");
        }

        // Update employee basic info
        boolean updated = employeeDAO.update(employee);

        if (updated) {
            // Update division relationship
            List<EmployeeDivision> existingDivisions = employeeDivisionDAO.findByEmployeeId(employee.getEmployeeId());
            if (!existingDivisions.isEmpty()) {
                // Delete existing division relationship
                employeeDivisionDAO.delete(employee.getEmployeeId(), existingDivisions.get(0).getDivisionId());
            }
            // Insert new division relationship
            EmployeeDivision newDivision = new EmployeeDivision(employee.getEmployeeId(), divisionId);
            employeeDivisionDAO.insert(newDivision);

            // Update job title relationship
            List<EmployeeJobTitle> existingJobTitles = employeeJobTitleDAO.findByEmployeeId(employee.getEmployeeId());
            if (!existingJobTitles.isEmpty()) {
                // Delete existing job title relationship
                employeeJobTitleDAO.delete(employee.getEmployeeId(), existingJobTitles.get(0).getJobTitleId());
            }
            // Insert new job title relationship
            EmployeeJobTitle newJobTitle = new EmployeeJobTitle(employee.getEmployeeId(), jobTitleId);
            employeeJobTitleDAO.insert(newJobTitle);
        }

        return updated;
    }

    public boolean deleteEmployee(int employeeId) throws SQLException {
        return employeeDAO.delete(employeeId);
    }

    // --- Salary update in range ---

    public int increaseSalaryInRange(BigDecimal min, BigDecimal max, BigDecimal percentage) throws SQLException {
        // Find all payroll records in the specified salary range
        List<Payroll> payrollsInRange = payrollDAO.findByAmountRange(min, max);
        
        if (payrollsInRange.isEmpty()) {
            return 0;
        }
        
        // Group by employee and find the latest payroll entry for each employee
        Map<Integer, Payroll> latestPayrollByEmployee = new HashMap<>();
        for (Payroll payroll : payrollsInRange) {
            Integer employeeId = payroll.getEmployeeId();
            if (employeeId == null || payroll.getPayPeriodStart() == null) {
                continue;
            }
            
            Payroll existing = latestPayrollByEmployee.get(employeeId);
            if (existing == null || 
                (existing.getPayPeriodStart() != null &&
                 payroll.getPayPeriodStart().isAfter(existing.getPayPeriodStart()))) {
                latestPayrollByEmployee.put(employeeId, payroll);
            }
        }
        
        // Calculate the increase factor
        BigDecimal increaseFactor = BigDecimal.ONE.add(percentage.divide(new BigDecimal("100")));
        int createdCount = 0;
        
        // Create new payroll entries for each affected employee
        for (Map.Entry<Integer, Payroll> entry : latestPayrollByEmployee.entrySet()) {
            Payroll latestPayroll = entry.getValue();
            
            if (latestPayroll.getPayPeriodStart() == null || latestPayroll.getPayPeriodEnd() == null) {
                continue;
            }
            
            // Calculate next pay period
            // Start date is the day after the last period ends (to avoid overlap)
            LocalDate nextPeriodStart = latestPayroll.getPayPeriodEnd().plusDays(1);
            
            // Calculate the duration of the last period (inclusive of both endpoints)
            // ChronoUnit.DAYS.between() returns exclusive count, so add 1 for inclusive duration
            long daysBetween = ChronoUnit.DAYS.between(latestPayroll.getPayPeriodStart(), latestPayroll.getPayPeriodEnd()) + 1;
            
            // End date is start + same duration (inclusive) - subtract 1 because both start and end are inclusive
            LocalDate nextPeriodEnd = nextPeriodStart.plusDays(daysBetween - 1);
            
            // Calculate new amount with increase
            BigDecimal newAmount = latestPayroll.getAmount().multiply(increaseFactor);
            
            // Create new payroll entry
            Payroll newPayroll = new Payroll(
                entry.getKey(),  // employeeId
                newAmount,
                nextPeriodStart,
                nextPeriodEnd
            );
            
            payrollDAO.insert(newPayroll);
            createdCount++;
        }
        
        return createdCount;
    }

    // --- For UI dropdowns ---

    public List<Division> getAllDivisions() throws SQLException {
        return divisionDAO.findAll();
    }

    public List<JobTitle> getAllJobTitles() throws SQLException {
        return jobTitleDAO.findAll();
    }
}
