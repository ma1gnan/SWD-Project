package com.employeemgmt.ui.fx.controller;

import com.employeemgmt.model.Employee;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavigationManager {

    private static Stage stage;

    // FXML files are in src/main/resources/
    private static final String BASE_FXML_PATH = "/com/employeemgmt/ui/fx/";

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        showMainMenu();
        stage.show();
    }

    private static void show(String fxmlFile, String title) {
        try {
            URL resource = NavigationManager.class.getResource(BASE_FXML_PATH + fxmlFile);
            if (resource == null) {
                throw new IllegalStateException("FXML not found: " + BASE_FXML_PATH + fxmlFile);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
        } catch (IOException e) {
            error("Failed to load FXML: " + fxmlFile, e);
        }
    }


    // --------- Screens ----------

    public static void showMainMenu() {
        show("main_menu.fxml", "Employee Management - Main Menu");
    }

    public static void showSearch() {
        show("employee_search.fxml", "Employee Management - Search");
    }

    public static void showAddEmployee() {
        show("employee_from.fxml", "Employee Management - Add Employee");
    }

    public static void showEditEmployee() {
        try {
            URL resource = NavigationManager.class.getResource(BASE_FXML_PATH + "edit_employee.fxml");
            if (resource == null) {
                throw new IllegalStateException("FXML not found: " + BASE_FXML_PATH + "edit_employee.fxml");
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root, 650, 600);
            stage.setTitle("Employee Management - Edit Employee");
            stage.setScene(scene);
        } catch (IOException e) {
            error("Failed to load edit_employee.fxml", e);
        }
    }

    public static void showEmployeePayHistory() {
        show("employee_pay_history.fxml", "Employee Management - Employee Pay History");
    }

    public static void showReports() {
        show("reports.fxml", "Employee Management - Reports");
    }

    public static void showAdjust() {
        show("salary_adjustment.fxml", "Employee Management - Salary Adjustment");
    }

    public static void showEmployeeDetail(Employee employee) {
        try {
            URL resource = NavigationManager.class.getResource(BASE_FXML_PATH + "employee_detail.fxml");
            if (resource == null) {
                throw new IllegalStateException("FXML not found: " + BASE_FXML_PATH + "employee_detail.fxml");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            EmployeeDetailController controller = loader.getController();
            controller.setEmployee(employee);

            Scene scene = new Scene(root);
            stage.setTitle("Employee Detail");
            stage.setScene(scene);
        } catch (IOException e) {
            error("Failed to load employee_detail.fxml", e);
        }
    }

    public static void showEmployeeSearch() {
        showSearch();
    }

    public static void showEmployeeFormEdit(Employee employee) {
        try {
            URL resource = NavigationManager.class.getResource(BASE_FXML_PATH + "employee_from.fxml");
            if (resource == null) {
                throw new IllegalStateException("FXML not found: " + BASE_FXML_PATH + "employee_from.fxml");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            EmployeeFormController controller = loader.getController();
            controller.editEmployee(employee);

            Scene scene = new Scene(root);
            stage.setTitle("Edit Employee");
            stage.setScene(scene);
        } catch (IOException e) {
            error("Failed to load employee_from.fxml for edit", e);
        }
    }

    // --------- Error helper ----------

    public static void error(String msg, Exception e) {
        System.err.println("\n[Navigation Error] " + msg + "\n");
        if (e != null) {
            e.printStackTrace();
        }
    }
}
