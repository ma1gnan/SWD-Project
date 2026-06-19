package com.employeemgmt.ui;

import com.employeemgmt.dao.*;
import com.employeemgmt.db.DatabaseInit;
import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;
import com.employeemgmt.ui.fx.controller.NavigationManager;
import com.employeemgmt.ui.fx.controller.ServiceRegistry;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Initialize database (creates tables and loads sample data if needed)
        System.out.println("Checking database initialization...");
        DatabaseInit dbInit = new DatabaseInit();
        if (dbInit.initializeIfNeeded()) {
            System.out.println("Database ready.");
        } else {
            System.err.println("Warning: Database initialization had issues, but continuing...");
        }

        // Build DAO layer (your existing backend)
        EmployeeDAO employeeDAO = new EmployeeDAOImpl();
        DivisionDAO divisionDAO = new DivisionDAOImpl();
        JobTitleDAO jobTitleDAO = new JobTitleDAOImpl();
        PayrollDAO payrollDAO = new PayrollDAOImpl();
        EmployeeDivisionDAO employeeDivisionDAO = new EmployeeDivisionDAOImpl();
        EmployeeJobTitleDAO employeeJobTitleDAO = new EmployeeJobTitleDAOImpl();

        // Build services
        EmployeeService employeeService = new EmployeeService(
                employeeDAO,
                divisionDAO,
                jobTitleDAO,
                employeeDivisionDAO,
                employeeJobTitleDAO,
                payrollDAO
        );

        ReportService reportService = new ReportService(
                employeeDAO,
                divisionDAO,
                jobTitleDAO,
                payrollDAO,
                employeeDivisionDAO,
                employeeJobTitleDAO
        );

        // Register them for controllers to use
        ServiceRegistry.init(employeeService, reportService);

        // Start UI
        NavigationManager.init(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
