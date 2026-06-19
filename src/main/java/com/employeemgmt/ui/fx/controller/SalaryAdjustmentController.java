package com.employeemgmt.ui.fx.controller;

import java.math.BigDecimal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SalaryAdjustmentController extends BaseController {

    @FXML private TextField txtMin;
    @FXML private TextField txtMax;
    @FXML private TextField txtPercent;

    @FXML
    private void onApply(){
        try{
            int updated = ServiceRegistry.employees().increaseSalaryInRange(
                    new BigDecimal(txtMin.getText()),
                    new BigDecimal(txtMax.getText()),
                    new BigDecimal(txtPercent.getText())
            );

            info(updated + " employees updated.");
        }catch(Exception e){ error("Salary update failed",e);}
    }

    @FXML
    private void onBack(){ NavigationManager.showMainMenu(); }
}