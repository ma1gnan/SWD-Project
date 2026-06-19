package com.employeemgmt.ui.fx.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
public class ReportsController extends BaseController {

    @FXML private ComboBox<Integer> cmbYear;
    @FXML private ComboBox<Integer> cmbMonth;
    @FXML private TableView<Map.Entry<String, BigDecimal>> tblJob;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,String> jobNameColumn;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,BigDecimal> colJobTotal;

    @FXML private TableView<Map.Entry<String, BigDecimal>> tblDiv;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,String> colDivName;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,BigDecimal> colDivTotal;
    @FXML
    public void initialize(){
        int y = YearMonth.now().getYear();
        cmbYear.getItems().addAll(y-2,y-1,y,y+1);
        cmbMonth.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        cmbYear.setValue(y);
        cmbMonth.setValue(YearMonth.now().getMonthValue());

        jobNameColumn.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(v.getValue().getKey()));
        colJobTotal.setCellValueFactory(v-> new javafx.beans.property.SimpleObjectProperty<>(v.getValue().getValue()));

        colDivName.setCellValueFactory(v-> new SimpleStringProperty(v.getValue().getKey()));
        colDivTotal.setCellValueFactory(v-> new SimpleObjectProperty<>(v.getValue().getValue()));
    }

    @FXML
    private void onRun(){
        int y = cmbYear.getValue();
        int m = cmbMonth.getValue();

        try{
            var job = ServiceRegistry.reports().getTotalPayByJobTitle(y, m);
            var div = ServiceRegistry.reports().getTotalPayByDivision(y, m);

            tblJob.setItems(FXCollections.observableArrayList(job.entrySet()));
            tblDiv.setItems(FXCollections.observableArrayList(div.entrySet()));


        } catch(SQLException e){
            error("Report failed", e);
        }
    }

    @FXML
    private void onBack(){ NavigationManager.showMainMenu(); }

    // All employee pay history functionality has been moved to EmployeePayHistoryController
}