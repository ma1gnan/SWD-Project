package com.employeemgmt.ui.fx.controller;

import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;

public class ServiceRegistry {
    private static EmployeeService employeeService;
    private static ReportService reportService;

    public static void init(EmployeeService emp, ReportService rep) {
        employeeService = emp;
        reportService = rep;
    }

    public static EmployeeService employees() { return employeeService; }
    public static ReportService reports() { return reportService; }
}