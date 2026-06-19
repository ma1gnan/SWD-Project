package com.employeemgmt.ui.fx.controller;

import java.util.List;
import java.util.Optional;

import com.employeemgmt.model.Employee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class EmployeeSearchController extends BaseController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbMode;
    @FXML private TableView<Employee> table;
    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colFirst;
    @FXML private TableColumn<Employee, String> colLast;
    @FXML private TableColumn<Employee, String> colSsn;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, String> colDivision;
    @FXML private TableColumn<Employee, String> colJobTitle;

    private final ObservableList<Employee> data = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        cmbMode.getItems().addAll("Name","SSN","Employee ID");
        cmbMode.getSelectionModel().select("Name");

        colId.setCellValueFactory(v-> new javafx.beans.property.SimpleIntegerProperty(v.getValue().getEmployeeId()).asObject());
        colFirst.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(v.getValue().getFirstName()));
        colLast.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(v.getValue().getLastName()));
        colSsn.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(v.getValue().getSsn()));
        colEmail.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(v.getValue().getEmail()));
        colDivision.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(
            v.getValue().getDivisionName() != null ? v.getValue().getDivisionName() : ""));
        colJobTitle.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(
            v.getValue().getJobTitleName() != null ? v.getValue().getJobTitleName() : ""));

        table.setItems(data);
    }

    @FXML
    private void onSearch(){
        String text = txtSearch.getText().trim();
        String mode = cmbMode.getValue();
        data.clear();

        try{
            switch(mode){
                case "Employee ID" -> {
                    int id = Integer.parseInt(text);
                    Optional<Employee> emp = ServiceRegistry.employees().findById(id);
                    emp.ifPresent(data::add);
                }
                case "SSN" -> {
                    Optional<Employee> emp = ServiceRegistry.employees().findBySSN(text);
                    emp.ifPresent(data::add);
                }
                default -> {  // Name search
                    List<Employee> list = ServiceRegistry.employees().findByNameFragment(text);
                    data.addAll(list);
                }
            }
        }catch(Exception e){
            error("Search failed", e);
        }
    }

    @FXML
    private void onEdit(){
        Employee emp = table.getSelectionModel().getSelectedItem();
        if(emp==null){ info("Select a record first"); return; }
        NavigationManager.showAddEmployee();
    }

    @FXML
    private void onBack(){
        NavigationManager.showMainMenu();
    }
}