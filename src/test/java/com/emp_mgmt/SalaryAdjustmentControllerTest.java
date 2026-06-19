package com.emp_mgmt;

import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;
import com.employeemgmt.ui.fx.controller.SalaryAdjustmentController;
import com.employeemgmt.ui.fx.controller.ServiceRegistry;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class SalaryAdjustmentControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ReportService reportService;

    private Stage stage;
    private SalaryAdjustmentController controller;
    private TextField txtMin;
    private TextField txtMax;
    private TextField txtPercent;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        
        // Initialize services
        ServiceRegistry.init(employeeService, reportService);
        
        // Load FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/employeemgmt/ui/fx/salary_adjustment.fxml")
        );
        Parent root = loader.load();
        controller = loader.getController();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        // Get references to UI components
        txtMin = (TextField) root.lookup("#txtMin");
        txtMax = (TextField) root.lookup("#txtMax");
        txtPercent = (TextField) root.lookup("#txtPercent");
    }

    @BeforeEach
    void setUp() {
        reset(employeeService, reportService);
    }

    @Test
    void testOnApply() throws SQLException {
        // Setup
        BigDecimal min = new BigDecimal("50000.00");
        BigDecimal max = new BigDecimal("100000.00");
        BigDecimal percent = new BigDecimal("5.0");
        int updatedCount = 10;
        
        when(employeeService.increaseSalaryInRange(min, max, percent))
            .thenReturn(updatedCount);
        
        txtMin.setText("50000.00");
        txtMax.setText("100000.00");
        txtPercent.setText("5.0");
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = SalaryAdjustmentController.class.getDeclaredMethod("onApply");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onApply method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).increaseSalaryInRange(min, max, percent);
    }

    @Test
    void testOnApply_ZeroEmployeesUpdated() throws SQLException {
        // Setup
        BigDecimal min = new BigDecimal("200000.00");
        BigDecimal max = new BigDecimal("300000.00");
        BigDecimal percent = new BigDecimal("3.0");
        
        when(employeeService.increaseSalaryInRange(min, max, percent))
            .thenReturn(0);
        
        txtMin.setText("200000.00");
        txtMax.setText("300000.00");
        txtPercent.setText("3.0");
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = SalaryAdjustmentController.class.getDeclaredMethod("onApply");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onApply method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).increaseSalaryInRange(min, max, percent);
    }

    @Test
    void testOnApply_InvalidInput() throws SQLException {
        // Setup - invalid number format
        txtMin.setText("invalid");
        txtMax.setText("100000.00");
        txtPercent.setText("5.0");
        
        // Execute - should handle exception gracefully
        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = SalaryAdjustmentController.class.getDeclaredMethod("onApply");
                method.setAccessible(true);
                method.invoke(controller);
                WaitForAsyncUtils.waitForFxEvents();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        // Verify - service should not be called with invalid input
        verify(employeeService, never()).increaseSalaryInRange(any(), any(), any());
    }

    @Test
    void testOnApply_EmptyFields() throws SQLException {
        // Setup - empty fields
        txtMin.setText("");
        txtMax.setText("");
        txtPercent.setText("");
        
        // Execute - should handle exception gracefully
        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = SalaryAdjustmentController.class.getDeclaredMethod("onApply");
                method.setAccessible(true);
                method.invoke(controller);
                WaitForAsyncUtils.waitForFxEvents();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        // Verify - service should not be called
        verify(employeeService, never()).increaseSalaryInRange(any(), any(), any());
    }

    @Test
    void testOnApply_ExceptionHandling() throws SQLException {
        // Setup
        BigDecimal min = new BigDecimal("50000.00");
        BigDecimal max = new BigDecimal("100000.00");
        BigDecimal percent = new BigDecimal("5.0");
        
        when(employeeService.increaseSalaryInRange(min, max, percent))
            .thenThrow(new SQLException("Database error"));
        
        txtMin.setText("50000.00");
        txtMax.setText("100000.00");
        txtPercent.setText("5.0");
        
        // Execute - should handle exception gracefully
        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = SalaryAdjustmentController.class.getDeclaredMethod("onApply");
                method.setAccessible(true);
                method.invoke(controller);
                WaitForAsyncUtils.waitForFxEvents();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        verify(employeeService).increaseSalaryInRange(min, max, percent);
    }

    @Test
    void testOnApply_DecimalValues() throws SQLException {
        // Setup
        BigDecimal min = new BigDecimal("50000.50");
        BigDecimal max = new BigDecimal("100000.75");
        BigDecimal percent = new BigDecimal("2.5");
        int updatedCount = 5;
        
        when(employeeService.increaseSalaryInRange(min, max, percent))
            .thenReturn(updatedCount);
        
        txtMin.setText("50000.50");
        txtMax.setText("100000.75");
        txtPercent.setText("2.5");
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = SalaryAdjustmentController.class.getDeclaredMethod("onApply");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onApply method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).increaseSalaryInRange(min, max, percent);
    }

    @Test
    void testOnBack() {
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = SalaryAdjustmentController.class.getDeclaredMethod("onBack");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onBack method: " + e.getMessage());
        }
        
        // Verify - should navigate back to main menu
    }

    @Test
    void testOnApply_LargeValues() throws SQLException {
        // Setup
        BigDecimal min = new BigDecimal("0.01");
        BigDecimal max = new BigDecimal("999999.99");
        BigDecimal percent = new BigDecimal("10.0");
        int updatedCount = 100;
        
        when(employeeService.increaseSalaryInRange(min, max, percent))
            .thenReturn(updatedCount);
        
        txtMin.setText("0.01");
        txtMax.setText("999999.99");
        txtPercent.setText("10.0");
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = SalaryAdjustmentController.class.getDeclaredMethod("onApply");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onApply method: " + e.getMessage());
        }
        
        // Verify
        verify(employeeService).increaseSalaryInRange(min, max, percent);
    }
}

