package com.employeemgmt.ui.fx.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public abstract class BaseController {

    protected void info(String msg){
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(msg);
            a.show();
        });
    }

    protected void error(String msg, Exception e){
        e.printStackTrace();
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(msg + "\n" + e.getMessage());
            a.show();
        });
    }
}