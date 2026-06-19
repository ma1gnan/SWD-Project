package com.employeemgmt.ui.fx.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.employeemgmt.model.Division;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.JobTitle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class EditEmployeeController extends BaseController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbMode;
    @FXML private GridPane formPane;
    @FXML private TextField txtFirst;
    @FXML private TextField txtLast;
    @FXML private TextField txtSSN;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<Division> cmbDivision;
    @FXML private ComboBox<JobTitle> cmbJob;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;

    private Employee currentEmployee;

    @FXML
    public void initialize() {
        // Initialize search mode combo box
        cmbMode.getItems().addAll("Name", "SSN", "Employee ID");
        cmbMode.getSelectionModel().select("Name");

        // Initialize division and job title combo boxes
        try {
            cmbDivision.setItems(FXCollections.observableList(ServiceRegistry.employees().getAllDivisions()));
            cmbJob.setItems(FXCollections.observableList(ServiceRegistry.employees().getAllJobTitles()));

            // Set custom cell factories to display only the name/title
            cmbDivision.setButtonCell(new javafx.scene.control.ListCell<Division>() {
                @Override
                protected void updateItem(Division item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            cmbDivision.setCellFactory(lv -> new javafx.scene.control.ListCell<Division>() {
                @Override
                protected void updateItem(Division item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

            cmbJob.setButtonCell(new javafx.scene.control.ListCell<JobTitle>() {
                @Override
                protected void updateItem(JobTitle item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            });
            cmbJob.setCellFactory(lv -> new javafx.scene.control.ListCell<JobTitle>() {
                @Override
                protected void updateItem(JobTitle item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            });
        } catch (SQLException e) {
            error("Failed loading lookups", e);
        }

        // Initially hide the form
        formPane.setVisible(false);
        formPane.setManaged(false);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
    }

    @FXML
    private void onSearch() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            info("Please enter a search term");
            return;
        }

        String mode = cmbMode.getValue();
        if (mode == null) {
            info("Please select a search mode");
            return;
        }

        try {
            Optional<Employee> employee = Optional.empty();

            switch (mode) {
                case "Employee ID" -> {
                    try {
                        int id = Integer.parseInt(text);
                        employee = ServiceRegistry.employees().findById(id);
                    } catch (NumberFormatException e) {
                        error("Invalid Employee ID format", e);
                        return;
                    }
                }
                case "SSN" -> {
                    employee = ServiceRegistry.employees().findBySSN(text);
                }
                default -> {  // Name search
                    List<Employee> list = ServiceRegistry.employees().findByNameFragment(text);
                    if (list.size() == 1) {
                        employee = Optional.of(list.get(0));
                    } else if (list.size() > 1) {
                        info("Multiple employees found. Please search by Employee ID or SSN for a unique match.");
                        return;
                    }
                }
            }

            if (employee.isPresent()) {
                loadEmployee(employee.get());
            } else {
                info("No employee found matching the search criteria.");
                formPane.setVisible(false);
                formPane.setManaged(false);
                btnSave.setDisable(true);
                btnDelete.setDisable(true);
            }
        } catch (Exception e) {
            error("Search failed", e);
        }
    }

    private void loadEmployee(Employee emp) {
        this.currentEmployee = emp;
        txtFirst.setText(emp.getFirstName());
        txtLast.setText(emp.getLastName());
        txtEmail.setText(emp.getEmail());
        txtSSN.setText(emp.getSsn());

        // Set division if available
        if (emp.getDivisionName() != null) {
            for (Division div : cmbDivision.getItems()) {
                if (div.getName().equals(emp.getDivisionName())) {
                    cmbDivision.setValue(div);
                    break;
                }
            }
        }

        // Set job title if available
        if (emp.getJobTitleName() != null) {
            for (JobTitle job : cmbJob.getItems()) {
                if (job.getTitle().equals(emp.getJobTitleName())) {
                    cmbJob.setValue(job);
                    break;
                }
            }
        }

        // Show the form
        formPane.setVisible(true);
        formPane.setManaged(true);
        btnSave.setDisable(false);
        btnDelete.setDisable(false);
    }

    @FXML
    private void onSave() {
        if (currentEmployee == null) {
            info("No employee loaded. Please search for an employee first.");
            return;
        }

        try {
            // Validate required dropdown selections
            if (cmbDivision.getValue() == null) {
                info("Please select a Division before saving.");
                return;
            }
            if (cmbJob.getValue() == null) {
                info("Please select a Job Title before saving.");
                return;
            }

            // Update employee fields
            currentEmployee.setFirstName(txtFirst.getText());
            currentEmployee.setLastName(txtLast.getText());
            currentEmployee.setEmail(txtEmail.getText());
            currentEmployee.setSsn(txtSSN.getText());

            // Save changes
            boolean success = ServiceRegistry.employees().updateEmployee(
                    currentEmployee,
                    cmbDivision.getValue().getDivisionId(),
                    cmbJob.getValue().getJobTitleId()
            );

            if (success) {
                info("Employee updated successfully.");
                // Clear form
                clearForm();
            } else {
                error("Failed to update employee", new Exception("Update returned false"));
            }
        } catch (Exception e) {
            error("Could not save employee", e);
        }
    }

    @FXML
    private void onDelete() {
        if (currentEmployee == null) {
            info("No employee loaded. Please search for an employee first.");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Employee");
        confirmAlert.setContentText(
                String.format("Are you sure you want to delete employee %s %s (ID: %d)?\n\n" +
                        "This will permanently remove all records for this employee.",
                        currentEmployee.getFirstName(),
                        currentEmployee.getLastName(),
                        currentEmployee.getEmployeeId())
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = ServiceRegistry.employees().deleteEmployee(currentEmployee.getEmployeeId());
                if (success) {
                    info("Employee deleted successfully.");
                    clearForm();
                } else {
                    error("Failed to delete employee", new Exception("Delete returned false"));
                }
            } catch (Exception e) {
                error("Could not delete employee", e);
            }
        }
    }

    @FXML
    private void onBack() {
        NavigationManager.showMainMenu();
    }

    private void clearForm() {
        currentEmployee = null;
        txtFirst.clear();
        txtLast.clear();
        txtSSN.clear();
        txtEmail.clear();
        cmbDivision.setValue(null);
        cmbJob.setValue(null);
        txtSearch.clear();
        formPane.setVisible(false);
        formPane.setManaged(false);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
    }
}

