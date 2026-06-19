package com.employeemgmt.ui.fx.controller;

import java.sql.SQLException;
import java.util.Comparator;

import com.employeemgmt.model.Employee;
import com.employeemgmt.model.Payroll;
import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeePayHistoryController extends BaseController {

    @FXML private ComboBox<String> cmbMode;
    @FXML private TextField txtEmpSearch;

    // Employee info table
    @FXML private TableView<Employee> tblEmpInfo;
    @FXML private TableColumn<Employee, Number> colEmpId;
    @FXML private TableColumn<Employee, String> colEmpName;
    @FXML private TableColumn<Employee, String> colEmpSsn;
    @FXML private TableColumn<Employee, String> colEmpEmail;
    @FXML private TableColumn<Employee, String> colEmpDivision;
    @FXML private TableColumn<Employee, String> colEmpJobTitle;

    // Pay history table
    @FXML private TableView<Payroll> tblEmpHistory;
    @FXML private TableColumn<Payroll, String> colHistStart;
    @FXML private TableColumn<Payroll, String> colHistEnd;
    @FXML private TableColumn<Payroll, Number> colHistAmount;

    @FXML
    public void initialize() {
        // Search mode options
        if (cmbMode != null) {
            cmbMode.getItems().addAll("Employee ID", "SSN", "Name");
            cmbMode.getSelectionModel().select("Employee ID");
        }

        // Employee info table columns
        if (colEmpId != null) {
            colEmpId.setCellValueFactory(v ->
                    new SimpleObjectProperty<>(
                            v.getValue().getEmployeeId() != null ? v.getValue().getEmployeeId() : 0));
        }
        if (colEmpName != null) {
            colEmpName.setCellValueFactory(v ->
                    new SimpleStringProperty(
                            nullToEmpty(v.getValue().getFirstName()) + " " +
                            nullToEmpty(v.getValue().getLastName())));
        }
        if (colEmpSsn != null) {
            colEmpSsn.setCellValueFactory(v ->
                    new SimpleStringProperty(nullToEmpty(v.getValue().getSsn())));
        }
        if (colEmpEmail != null) {
            colEmpEmail.setCellValueFactory(v ->
                    new SimpleStringProperty(nullToEmpty(v.getValue().getEmail())));
        }
        if (colEmpDivision != null) {
            colEmpDivision.setCellValueFactory(v ->
                    new SimpleStringProperty(nullToEmpty(v.getValue().getDivisionName())));
        }
        if (colEmpJobTitle != null) {
            colEmpJobTitle.setCellValueFactory(v ->
                    new SimpleStringProperty(nullToEmpty(v.getValue().getJobTitleName())));
        }

        // Pay history table columns
        colHistStart.setCellValueFactory(v -> {
            var d = v.getValue().getPayPeriodStart();
            return new SimpleStringProperty(d != null ? d.toString() : "");
        });

        colHistEnd.setCellValueFactory(v -> {
            var d = v.getValue().getPayPeriodEnd();
            return new SimpleStringProperty(d != null ? d.toString() : "");
        });

        colHistAmount.setCellValueFactory(v ->
            new SimpleObjectProperty<>(v.getValue().getAmount()));
    }

    @FXML
    private void onSearchEmployee() {
        String query = txtEmpSearch.getText();
        if (query == null || query.isBlank()) {
            info("Please enter ID, name, or SSN.");
            clearEmployeeHistory();
            return;
        }

        try {
            EmployeeService empService = ServiceRegistry.employees();
            Employee employee = null;
            String trimmed = query.trim();

            String mode = cmbMode != null ? cmbMode.getValue() : "Employee ID";

            switch (mode) {
                case "SSN" -> {
                    var opt = empService.findBySSN(trimmed);
                    if (opt.isEmpty()) {
                        info("No employee found with SSN: " + trimmed);
                        clearEmployeeHistory();
                        return;
                    }
                    employee = opt.get();
                }
                case "Name" -> {
                    var matches = empService.findByNameFragment(trimmed);
                    if (matches.isEmpty()) {
                        info("No employee found for: " + trimmed);
                        clearEmployeeHistory();
                        return;
                    }
                    if (matches.size() > 1) {
                        info("More than one employee matches '" + trimmed +
                                "'. Please refine your search (include last name or use ID/SSN).");
                        clearEmployeeHistory();
                        return;
                    }
                    employee = matches.get(0);
                }
                default -> { // Employee ID
                    try {
                        int id = Integer.parseInt(trimmed);
                        var opt = empService.findById(id);
                        if (opt.isEmpty()) {
                            info("No employee found with ID: " + id);
                            clearEmployeeHistory();
                            return;
                        }
                        employee = opt.get();
                    } catch (NumberFormatException nfe) {
                        info("Employee ID must be a number.");
                        clearEmployeeHistory();
                        return;
                    }
                }
            }

            ReportService reports = ServiceRegistry.reports();
            var history = reports.getPayHistoryForEmployee(employee.getEmployeeId());
            history.sort(Comparator.comparing(Payroll::getPayPeriodStart).reversed());

            // populate employee info table (single row)
            if (tblEmpInfo != null) {
                tblEmpInfo.setItems(FXCollections.observableArrayList(employee));
            }

            tblEmpHistory.setItems(FXCollections.observableArrayList(history));

        } catch (SQLException ex) {
            error("Search failed", ex);
            clearEmployeeHistory();
        }
    }

    @FXML
    private void onBack() {
        NavigationManager.showMainMenu();
    }

    private void clearEmployeeHistory() {
        if (tblEmpInfo != null) {
            tblEmpInfo.getItems().clear();
        }
        if (tblEmpHistory != null) {
            tblEmpHistory.getItems().clear();
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}


