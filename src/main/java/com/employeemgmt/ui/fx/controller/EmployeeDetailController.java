package com.employeemgmt.ui.fx.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.employeemgmt.dao.DivisionDAOImpl;
import com.employeemgmt.dao.EmployeeDAOImpl;
import com.employeemgmt.dao.EmployeeDivisionDAOImpl;
import com.employeemgmt.dao.EmployeeJobTitleDAOImpl;
import com.employeemgmt.dao.JobTitleDAOImpl;
import com.employeemgmt.dao.PayrollDAOImpl;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.Payroll;
import com.employeemgmt.service.ReportService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EmployeeDetailController extends BaseController {

    @FXML
    private Label lblId;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSsn;
    @FXML
    private Label lblEmail;

    @FXML
    private TableView<Payroll> tblPayroll;
    @FXML
    private TableColumn<Payroll, Number> colPayrollId;
    @FXML
    private TableColumn<Payroll, String> colPeriod;
    @FXML
    private TableColumn<Payroll, Number> colAmount;

    private final ObservableList<Payroll> payrollData =
            FXCollections.observableArrayList();

    private Employee employee;
    private final ReportService reportService = new ReportService(
        new EmployeeDAOImpl(),
        new DivisionDAOImpl(),
        new JobTitleDAOImpl(),
        new PayrollDAOImpl(),
        new EmployeeDivisionDAOImpl(),
        new EmployeeJobTitleDAOImpl()
);

    @FXML
    private void initialize() {
        colPayrollId.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getPayrollId() != null ? cell.getValue().getPayrollId() : 0));

        colAmount.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<Number>(
                        cell.getValue().getAmount() != null ? cell.getValue().getAmount() : BigDecimal.ZERO));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        colPeriod.setCellValueFactory(cell -> {
            Payroll p = cell.getValue();
            String start = p.getPayPeriodStart() != null ? p.getPayPeriodStart().format(fmt) : "";
            String end = p.getPayPeriodEnd() != null ? p.getPayPeriodEnd().format(fmt) : "";
            return new javafx.beans.property.SimpleStringProperty(start + " to " + end);
        });

        tblPayroll.setItems(payrollData);
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        lblId.setText(employee.getEmployeeId() != null ? employee.getEmployeeId().toString() : "");
        lblName.setText(employee.getFirstName() + " " + employee.getLastName());
        lblSsn.setText(employee.getSsn());
        lblEmail.setText(employee.getEmail());

        loadPayHistory();
    }

    private void loadPayHistory() {
    if (employee.getEmployeeId() == null) {
        payrollData.clear();
        return;
    }

    try {
        List<Payroll> history = reportService.getPayHistoryForEmployee(employee.getEmployeeId());
        payrollData.setAll(history);
    } catch (Exception e) {  // <== FIXED CATCH
        showError("Failed to load pay history.", e);
    }
}

    @FXML
    private void onEdit() {
        NavigationManager.showEmployeeFormEdit(employee);
    }

    @FXML
    private void onBack() {
        NavigationManager.showEmployeeSearch();

    }

    private void showError(String msg, Exception e) {
        NavigationManager.error(msg, e);
    }

}