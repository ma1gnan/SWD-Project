package com.employeemgmt.ui.fx.controller;

import java.sql.SQLException;

import com.employeemgmt.model.Division;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.JobTitle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class EmployeeFormController extends BaseController {

    @FXML private TextField txtFirst;
    @FXML private TextField txtLast;
    @FXML private TextField txtSSN;
    @FXML private TextField txtEmail;

    @FXML private ComboBox<Division> cmbDivision;
    @FXML private ComboBox<JobTitle> cmbJob;

    private Employee editing = null;

    @FXML
    public void initialize(){
        try{
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
        }catch(SQLException e){ error("Failed loading lookups",e); }
    }

    public void setEmployee(Employee emp){
        this.editing = emp;
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
    }

    public void editEmployee(Employee emp) {
        setEmployee(emp);
    }


    @FXML
    private void onSave(){
        try{
            // Validate required dropdown selections
            if (cmbDivision.getValue() == null) {
                info("Please select a Division before saving.");
                return;
            }
            if (cmbJob.getValue() == null) {
                info("Please select a Job Title before saving.");
                return;
            }

            if(editing == null){
                Employee e = new Employee(txtFirst.getText(), txtLast.getText(),
                        txtSSN.getText(), txtEmail.getText());

                ServiceRegistry.employees().addEmployee(e,
                        cmbDivision.getValue().getDivisionId(),
                        cmbJob.getValue().getJobTitleId());

                info("New employee added.");
            } else {
                editing.setFirstName(txtFirst.getText());
                editing.setLastName(txtLast.getText());
                editing.setEmail(txtEmail.getText());
                editing.setSsn(txtSSN.getText());

                ServiceRegistry.employees().updateEmployee(editing,
                        cmbDivision.getValue().getDivisionId(),
                        cmbJob.getValue().getJobTitleId());

                info("Update successful.");
            }
            NavigationManager.showSearch();

        }catch(Exception e){ error("Could not save",e); }
    }

    @FXML
    private void onCancel(){
        NavigationManager.showSearch();
    }
}