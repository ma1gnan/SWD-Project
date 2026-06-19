package com.employeemgmt.service;

import com.employeemgmt.dao.*;
import com.employeemgmt.model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.employeemgmt.ui.ReportRow;

public class ReportService {

    private final EmployeeDAO employeeDAO;
    private final DivisionDAO divisionDAO;
    private final JobTitleDAO jobTitleDAO;
    private final PayrollDAO payrollDAO;
    private final EmployeeDivisionDAO employeeDivisionDAO;
    private final EmployeeJobTitleDAO employeeJobTitleDAO;

    public ReportService(EmployeeDAO employeeDAO,
                         DivisionDAO divisionDAO,
                         JobTitleDAO jobTitleDAO,
                         PayrollDAO payrollDAO,
                         EmployeeDivisionDAO employeeDivisionDAO,
                         EmployeeJobTitleDAO employeeJobTitleDAO) {
        this.employeeDAO = employeeDAO;
        this.divisionDAO = divisionDAO;
        this.jobTitleDAO = jobTitleDAO;
        this.payrollDAO = payrollDAO;
        this.employeeDivisionDAO = employeeDivisionDAO;
        this.employeeJobTitleDAO = employeeJobTitleDAO;
    }

    // --------------------------------------------------------------------
    // 1) Full-time employee info + pay history
    // --------------------------------------------------------------------

    public Optional<Employee> getEmployee(int employeeId) throws SQLException {
        return employeeDAO.findById(employeeId);
    }

    public List<Payroll> getPayHistoryForEmployee(int employeeId) throws SQLException {
        return payrollDAO.findByEmployeeId(employeeId);
    }

    // --------------------------------------------------------------------
    // Helpers: filter payrolls for a specific month
    // --------------------------------------------------------------------

    private List<Payroll> getPayrollsForMonth(int year, int month) throws SQLException {
        List<Payroll> all = payrollDAO.findAll();

        return all.stream()
                .filter(p -> {
                    LocalDate date = p.getPayPeriodEnd(); // or getPayPeriodStart()
                    return date != null
                            && date.getYear() == year
                            && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // 2) Total pay by Job Title (for a month)
    // --------------------------------------------------------------------

    public Map<String, BigDecimal> getTotalPayByJobTitle(int year, int month) throws SQLException {
        List<Payroll> payrolls = getPayrollsForMonth(year, month);

        // empId -> jobTitleId from EmployeeJobTitle
        Map<Integer, Integer> empToJobTitleId = employeeJobTitleDAO.findAll()
                .stream()
                .collect(Collectors.toMap(
                        EmployeeJobTitle::getEmployeeId,
                        EmployeeJobTitle::getJobTitleId
                ));

        // jobTitleId -> JobTitle
        Map<Integer, JobTitle> jobTitleMap = jobTitleDAO.findAll()
                .stream()
                .collect(Collectors.toMap(JobTitle::getJobTitleId, jt -> jt));

        Map<String, BigDecimal> totals = new HashMap<>();

        for (Payroll p : payrolls) {
            int empId = p.getEmployeeId();
            Integer jobTitleId = empToJobTitleId.get(empId);
            if (jobTitleId == null) {
                continue;
            }

            JobTitle jobTitle = jobTitleMap.get(jobTitleId);
            if (jobTitle == null) {
                continue;
            }

            String titleName = jobTitle.getTitle();
            totals.merge(titleName, p.getAmount(), BigDecimal::add);
        }

        return totals;
    }

    /**
     * Employee FTE info + pay history for a given month.
     * One row per payroll entry for that month, with employee/division/job metadata.
     */
    public List<ReportRow> getEmployeePayForMonth(int year, int month) throws SQLException {
        List<Payroll> payrolls = getPayrollsForMonth(year, month);

        Map<Integer, Employee> employeeMap = employeeDAO.findAll()
                .stream()
                .collect(Collectors.toMap(Employee::getEmployeeId, e -> e));

        Map<Integer, Integer> empToDivisionId = employeeDivisionDAO.findAll()
                .stream()
                .collect(Collectors.toMap(
                        EmployeeDivision::getEmployeeId,
                        EmployeeDivision::getDivisionId
                ));

        Map<Integer, Division> divisionMap = divisionDAO.findAll()
                .stream()
                .collect(Collectors.toMap(Division::getDivisionId, d -> d));

        Map<Integer, Integer> empToJobTitleId = employeeJobTitleDAO.findAll()
                .stream()
                .collect(Collectors.toMap(
                        EmployeeJobTitle::getEmployeeId,
                        EmployeeJobTitle::getJobTitleId
                ));

        Map<Integer, JobTitle> jobTitleMap = jobTitleDAO.findAll()
                .stream()
                .collect(Collectors.toMap(JobTitle::getJobTitleId, jt -> jt));

        List<ReportRow> rows = new ArrayList<>();

        for (Payroll p : payrolls) {
            Integer empId = p.getEmployeeId();
            if (empId == null) {
                continue;
            }

            Employee emp = employeeMap.get(empId);
            String employeeName;
            if (emp != null) {
                employeeName = String.format("%s %s",
                        emp.getFirstName() != null ? emp.getFirstName() : "",
                        emp.getLastName() != null ? emp.getLastName() : "").trim();
            } else {
                employeeName = "Employee #" + empId;
            }

            String divisionName = "";
            Integer divId = empToDivisionId.get(empId);
            if (divId != null) {
                Division d = divisionMap.get(divId);
                if (d != null && d.getName() != null) {
                    divisionName = d.getName();
                }
            }

            String jobTitleName = "";
            Integer jobId = empToJobTitleId.get(empId);
            if (jobId != null) {
                JobTitle jt = jobTitleMap.get(jobId);
                if (jt != null && jt.getTitle() != null) {
                    jobTitleName = jt.getTitle();
                }
            }

            ReportRow row = new ReportRow();
            row.setEmployeeId(empId);
            row.setEmployeeName(employeeName);
            row.setDivisionName(divisionName);
            row.setJobTitle(jobTitleName);
            row.setPayPeriodStart(p.getPayPeriodStart());
            row.setPayPeriodEnd(p.getPayPeriodEnd());
            row.setAmount(p.getAmount());

            rows.add(row);
        }
        return rows;
    }


    // --------------------------------------------------------------------
    // 3) Total pay by Division (for a month)
    // --------------------------------------------------------------------

    public Map<String, BigDecimal> getTotalPayByDivision(int year, int month) throws SQLException {
        List<Payroll> payrolls = getPayrollsForMonth(year, month);

        // empId -> divisionId from EmployeeDivision
        Map<Integer, Integer> empToDivisionId = employeeDivisionDAO.findAll()
                .stream()
                .collect(Collectors.toMap(
                        EmployeeDivision::getEmployeeId,
                        EmployeeDivision::getDivisionId
                ));

        // divisionId -> Division
        Map<Integer, Division> divisionMap = divisionDAO.findAll()
                .stream()
                .collect(Collectors.toMap(Division::getDivisionId, d -> d));

        Map<String, BigDecimal> totals = new HashMap<>();

        for (Payroll p : payrolls) {
            int empId = p.getEmployeeId();
            Integer divisionId = empToDivisionId.get(empId);
            if (divisionId == null) {
                continue;
            }

            Division division = divisionMap.get(divisionId);
            if (division == null) {
                continue;
            }

            String divisionName = division.getName();
            totals.merge(divisionName, p.getAmount(), BigDecimal::add);
        }

        return totals;
    }
}
