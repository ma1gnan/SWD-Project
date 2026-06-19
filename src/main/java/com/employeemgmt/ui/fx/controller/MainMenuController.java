package com.employeemgmt.ui.fx.controller;

import javafx.fxml.FXML;

public class MainMenuController extends BaseController {

    @FXML
    private void onSearchEmployees(){
        NavigationManager.showSearch();
    }

    @FXML
    private void onAddEmployee(){
        NavigationManager.showAddEmployee();
    }

    @FXML
    private void onEmployeePayHistory(){
        NavigationManager.showEmployeePayHistory();
    }

    @FXML
    private void onEditEmployee(){
        NavigationManager.showEditEmployee();
    }

    @FXML
    private void onReports(){
        NavigationManager.showReports();
    }

    @FXML
    private void onSalaryAdjust(){
        NavigationManager.showAdjust();
    }

    @FXML
    private void onExit(){
        System.exit(0);
    }
}