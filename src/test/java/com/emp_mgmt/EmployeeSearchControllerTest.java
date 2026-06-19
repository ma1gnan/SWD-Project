package com.emp_mgmt;

import com.employeemgmt.model.Employee;
import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;
import com.employeemgmt.ui.fx.controller.EmployeeSearchController;
import com.employeemgmt.ui.fx.controller.NavigationManager;
import com.employeemgmt.ui.fx.controller.ServiceRegistry;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class EmployeeSearchControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ReportService reportService;

    private Stage stage;
    private EmployeeSearchController controller;
    private TextField txtSearch;
    private ComboBox<String> cmbMode;
    private TableView<Employee> table;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        
        // Initialize services
        ServiceRegistry.init(employeeService, reportService);
        
        // Load FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/employeemgmt/ui/fx/employee_search.fxml")
        );
        Parent root = loader.load();
        controller = loader.getController();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        // Get references to UI components
        txtSearch = (TextField) root.lookup("#txtSearch");
        cmbMode = (ComboBox<String>) root.lookup("#cmbMode");
        table = (TableView<Employee>) root.lookup("#table");
    }

    @BeforeEach
    void setUp() {
        reset(employeeService, reportService);
    }

    @Test
    void testInitialize() {
        // Verify that search mode combo box is initialized
        assertNotNull(cmbMode);
        List<String> items = cmbMode.getItems();
        assertTrue(items.contains("Name"));
        assertTrue(items.contains("SSN"));
        assertTrue(items.contains("Employee ID"));
        assertEquals("Name", cmbMode.getValue());
    }

    @Test
    void testOnSearchByName() throws SQLException {
        // Setup
        String searchTerm = "John";
        Employee emp1 = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        Employee emp2 = new Employee(2, "Johnny", "Smith", "987654321", "johnny@example.com");
        List<Employee> employees = Arrays.asList(emp1, emp2);
        
        when(employeeService.findByNameFragment(searchTerm)).thenReturn(employees);
        cmbMode.setValue("Name");
        txtSearch.setText(searchTerm);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onSearch");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSearch method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).findByNameFragment(searchTerm);
        assertEquals(2, table.getItems().size());
        assertTrue(table.getItems().contains(emp1));
        assertTrue(table.getItems().contains(emp2));
    }

    @Test
    void testOnSearchBySSN() throws SQLException {
        // Setup
        String ssn = "123456789";
        Employee employee = new Employee(1, "John", "Doe", ssn, "john@example.com");
        
        when(employeeService.findBySSN(ssn)).thenReturn(Optional.of(employee));
        cmbMode.setValue("SSN");
        txtSearch.setText(ssn);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onSearch");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSearch method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).findBySSN(ssn);
        assertEquals(1, table.getItems().size());
        assertEquals(employee, table.getItems().get(0));
    }

    @Test
    void testOnSearchBySSN_NotFound() throws SQLException {
        // Setup
        String ssn = "999999999";
        when(employeeService.findBySSN(ssn)).thenReturn(Optional.empty());
        cmbMode.setValue("SSN");
        txtSearch.setText(ssn);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onSearch");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSearch method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).findBySSN(ssn);
        assertTrue(table.getItems().isEmpty());
    }

    @Test
    void testOnSearchByEmployeeID() throws SQLException {
        // Setup
        int employeeId = 1;
        Employee employee = new Employee(employeeId, "John", "Doe", "123456789", "john@example.com");
        
        when(employeeService.findById(employeeId)).thenReturn(Optional.of(employee));
        cmbMode.setValue("Employee ID");
        txtSearch.setText(String.valueOf(employeeId));
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onSearch");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSearch method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).findById(employeeId);
        assertEquals(1, table.getItems().size());
        assertEquals(employee, table.getItems().get(0));
    }

    @Test
    void testOnSearchByEmployeeID_InvalidNumber() {
        // Setup
        cmbMode.setValue("Employee ID");
        txtSearch.setText("invalid");
        
        // Execute - should handle exception 
        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onSearch");
                method.setAccessible(true);
                method.invoke(controller);
                WaitForAsyncUtils.waitForFxEvents();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testOnSearch_EmptyResults() throws SQLException {
        // Setup
        when(employeeService.findByNameFragment("Nonexistent")).thenReturn(Collections.emptyList());
        cmbMode.setValue("Name");
        txtSearch.setText("Nonexistent");
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onSearch");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSearch method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).findByNameFragment("Nonexistent");
        assertTrue(table.getItems().isEmpty());
    }

    @Test
    void testOnSearch_ExceptionHandling() throws SQLException {
        // Setup
        when(employeeService.findByNameFragment(anyString())).thenThrow(new SQLException("Database error"));
        cmbMode.setValue("Name");
        txtSearch.setText("test");
        
        // Execute - should handle exception 
        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onSearch");
                method.setAccessible(true);
                method.invoke(controller);
                WaitForAsyncUtils.waitForFxEvents();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        verify(employeeService).findByNameFragment("test");
    }

    @Test
    void testOnEdit_NoSelection() {
        // Setup - no employee selected
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onEdit");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onEdit method: " + e.getMessage());
        }
        
        // Verify - should show info message
    }

    @Test
    void testOnEdit_WithSelection() {
        // Setup
        Employee employee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        table.getItems().add(employee);
        table.getSelectionModel().select(employee);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onEdit");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onEdit method: " + e.getMessage());
        }
        
        // Verify - navigation should occur
    }

    @Test
    void testOnBack() {
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onBack");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onBack method: " + e.getMessage());
        }
        
        // Verify - should navigate back to main menu
    }

    @Test
    void testOnSearch_TrimWhitespace() throws SQLException {
        // Setup
        String searchTerm = "  John  ";
        Employee employee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        when(employeeService.findByNameFragment("John")).thenReturn(Arrays.asList(employee));
        cmbMode.setValue("Name");
        txtSearch.setText(searchTerm);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeSearchController.class.getDeclaredMethod("onSearch");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onSearch method: " + e.getMessage());
        }
        
        // Verify - should trim whitespace
        verify(employeeService).findByNameFragment("John");
    }
}

