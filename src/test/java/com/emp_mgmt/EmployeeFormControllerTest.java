package com.emp_mgmt;

import com.employeemgmt.model.Division;
import com.employeemgmt.model.Employee;
import com.employeemgmt.model.JobTitle;
import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;
import com.employeemgmt.ui.fx.controller.EmployeeFormController;
import com.employeemgmt.ui.fx.controller.ServiceRegistry;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class EmployeeFormControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ReportService reportService;

    private Stage stage;
    private EmployeeFormController controller;
    private TextField txtFirst;
    private TextField txtLast;
    private TextField txtSSN;
    private TextField txtEmail;
    private ComboBox<Division> cmbDivision;
    private ComboBox<JobTitle> cmbJob;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        
        // Initialize services
        ServiceRegistry.init(employeeService, reportService);
        
        // Load FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/employeemgmt/ui/fx/employee_from.fxml")
        );
        Parent root = loader.load();
        controller = loader.getController();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        // Get references to UI components
        txtFirst = (TextField) root.lookup("#txtFirst");
        txtLast = (TextField) root.lookup("#txtLast");
        txtSSN = (TextField) root.lookup("#txtSSN");
        txtEmail = (TextField) root.lookup("#txtEmail");
        cmbDivision = (ComboBox<Division>) root.lookup("#cmbDivision");
        cmbJob = (ComboBox<JobTitle>) root.lookup("#cmbJob");
    }

    @BeforeEach
    void setUp() throws SQLException {
        reset(employeeService, reportService);
        
        // Setup mock data for dropdowns
        Division div1 = new Division(1, "Engineering");
        Division div2 = new Division(2, "Sales");
        List<Division> divisions = Arrays.asList(div1, div2);
        
        JobTitle job1 = new JobTitle(1, "Software Engineer");
        JobTitle job2 = new JobTitle(2, "Manager");
        List<JobTitle> jobTitles = Arrays.asList(job1, job2);
        
        when(employeeService.getAllDivisions()).thenReturn(divisions);
        when(employeeService.getAllJobTitles()).thenReturn(jobTitles);
    }

    @Test
    void testInitialize() throws SQLException {
        // Execute
        controller.initialize();
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify
        verify(employeeService).getAllDivisions();
        verify(employeeService).getAllJobTitles();
        assertNotNull(cmbDivision.getItems());
        assertNotNull(cmbJob.getItems());
    }

    @Test
    void testInitialize_ExceptionHandling() throws SQLException {
        // Setup
        when(employeeService.getAllDivisions()).thenThrow(new SQLException("Database error"));
        
        // Execute - should handle exception gracefully
        assertDoesNotThrow(() -> {
            controller.initialize();
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testOnSave_NewEmployee() throws SQLException {
        // Setup
        Division division = new Division(1, "Engineering");
        JobTitle jobTitle = new JobTitle(1, "Software Engineer");
        Employee newEmployee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        
        when(employeeService.addEmployee(any(Employee.class), eq(1), eq(1)))
            .thenReturn(newEmployee);
        
        txtFirst.setText("John");
        txtLast.setText("Doe");
        txtSSN.setText("123456789");
        txtEmail.setText("john@example.com");
        cmbDivision.setValue(division);
        cmbJob.setValue(jobTitle);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeFormController.class.getDeclaredMethod("onSave");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSave method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).addEmployee(any(Employee.class), eq(1), eq(1));
    }

    @Test
    void testOnSave_UpdateEmployee() throws SQLException {
        // Setup
        Employee existingEmployee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        controller.setEmployee(existingEmployee);
        
        Division division = new Division(1, "Engineering");
        JobTitle jobTitle = new JobTitle(1, "Software Engineer");
        
        when(employeeService.updateEmployee(any(Employee.class), eq(1), eq(1)))
            .thenReturn(true);
        
        txtFirst.setText("Jane");
        txtLast.setText("Smith");
        txtSSN.setText("987654321");
        txtEmail.setText("jane@example.com");
        cmbDivision.setValue(division);
        cmbJob.setValue(jobTitle);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeFormController.class.getDeclaredMethod("onSave");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSave method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).updateEmployee(any(Employee.class), eq(1), eq(1));
        assertEquals("Jane", existingEmployee.getFirstName());
        assertEquals("Smith", existingEmployee.getLastName());
    }

    @Test
    void testOnSave_NoDivisionSelected() throws SQLException {
        // Setup
        txtFirst.setText("John");
        txtLast.setText("Doe");
        txtSSN.setText("123456789");
        txtEmail.setText("john@example.com");
        cmbDivision.setValue(null);
        cmbJob.setValue(new JobTitle(1, "Software Engineer"));
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeFormController.class.getDeclaredMethod("onSave");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSave method: " + e.getMessage());
        }
        
        // Verify - should not save, should show info message
        verify(employeeService, never()).addEmployee(any(), anyInt(), anyInt());
        verify(employeeService, never()).updateEmployee(any(), anyInt(), anyInt());
    }

    @Test
    void testOnSave_NoJobTitleSelected() throws SQLException {
        // Setup
        txtFirst.setText("John");
        txtLast.setText("Doe");
        txtSSN.setText("123456789");
        txtEmail.setText("john@example.com");
        cmbDivision.setValue(new Division(1, "Engineering"));
        cmbJob.setValue(null);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeFormController.class.getDeclaredMethod("onSave");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSave method: " + e.getMessage());
        }
        
        // Verify - should not save, should show info message
        verify(employeeService, never()).addEmployee(any(), anyInt(), anyInt());
        verify(employeeService, never()).updateEmployee(any(), anyInt(), anyInt());
    }

    @Test
    void testOnSave_ExceptionHandling() throws SQLException {
        // Setup
        Division division = new Division(1, "Engineering");
        JobTitle jobTitle = new JobTitle(1, "Software Engineer");
        
        when(employeeService.addEmployee(any(Employee.class), anyInt(), anyInt()))
            .thenThrow(new SQLException("Database error"));
        
        txtFirst.setText("John");
        txtLast.setText("Doe");
        txtSSN.setText("123456789");
        txtEmail.setText("john@example.com");
        cmbDivision.setValue(division);
        cmbJob.setValue(jobTitle);
        
        // Execute - should handle exception gracefully
        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = EmployeeFormController.class.getDeclaredMethod("onSave");
                method.setAccessible(true);
                method.invoke(controller);
                WaitForAsyncUtils.waitForFxEvents();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testOnCancel() {
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeFormController.class.getDeclaredMethod("onCancel");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onCancel method: " + e.getMessage());
        }
        
        // Verify - should navigate back to search
        // This is tested through NavigationManager
    }

    @Test
    void testSetEmployee() {
        // Setup
        Employee employee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        
        // Execute
        controller.setEmployee(employee);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify
        assertEquals("John", txtFirst.getText());
        assertEquals("Doe", txtLast.getText());
        assertEquals("123456789", txtSSN.getText());
        assertEquals("john@example.com", txtEmail.getText());
    }

    @Test
    void testEditEmployee() {
        // Setup
        Employee employee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        
        // Execute
        controller.editEmployee(employee);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify
        assertEquals("John", txtFirst.getText());
        assertEquals("Doe", txtLast.getText());
        assertEquals("123456789", txtSSN.getText());
        assertEquals("john@example.com", txtEmail.getText());
    }
}

