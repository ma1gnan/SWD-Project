package com.emp_mgmt;

import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;
import com.employeemgmt.ui.fx.controller.ReportsController;
import com.employeemgmt.ui.fx.controller.ServiceRegistry;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
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
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class ReportsControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ReportService reportService;

    private Stage stage;
    private ReportsController controller;
    private ComboBox<Integer> cmbYear;
    private ComboBox<Integer> cmbMonth;
    private TableView<Map.Entry<String, BigDecimal>> tblJob;
    private TableView<Map.Entry<String, BigDecimal>> tblDiv;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        
        // Initialize services
        ServiceRegistry.init(employeeService, reportService);
        
        // Load FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/employeemgmt/ui/fx/reports.fxml")
        );
        Parent root = loader.load();
        controller = loader.getController();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        // Get references to UI components
        cmbYear = (ComboBox<Integer>) root.lookup("#cmbYear");
        cmbMonth = (ComboBox<Integer>) root.lookup("#cmbMonth");
        tblJob = (TableView<Map.Entry<String, BigDecimal>>) root.lookup("#tblJob");
        tblDiv = (TableView<Map.Entry<String, BigDecimal>>) root.lookup("#tblDiv");
    }

    @BeforeEach
    void setUp() {
        reset(employeeService, reportService);
    }

    @Test
    void testInitialize() {
        // Verify that year and month combo boxes are initialized
        assertNotNull(cmbYear);
        assertNotNull(cmbMonth);
        
        int currentYear = YearMonth.now().getYear();
        int currentMonth = YearMonth.now().getMonthValue();
        
        // Verify default values
        assertEquals(currentYear, cmbYear.getValue());
        assertEquals(currentMonth, cmbMonth.getValue());
        
        // Verify year range (current year - 2 to current year + 1)
        assertTrue(cmbYear.getItems().contains(currentYear - 2));
        assertTrue(cmbYear.getItems().contains(currentYear - 1));
        assertTrue(cmbYear.getItems().contains(currentYear));
        assertTrue(cmbYear.getItems().contains(currentYear + 1));
        
        // Verify months (1-12)
        assertEquals(12, cmbMonth.getItems().size());
        for (int i = 1; i <= 12; i++) {
            assertTrue(cmbMonth.getItems().contains(i));
        }
    }

    @Test
    void testOnRun() throws SQLException {
        // Setup
        int year = 2024;
        int month = 1;
        
        Map<String, BigDecimal> jobTotals = new HashMap<>();
        jobTotals.put("Software Engineer", new BigDecimal("50000.00"));
        jobTotals.put("Manager", new BigDecimal("75000.00"));
        
        Map<String, BigDecimal> divTotals = new HashMap<>();
        divTotals.put("Engineering", new BigDecimal("100000.00"));
        divTotals.put("Sales", new BigDecimal("80000.00"));
        
        when(reportService.getTotalPayByJobTitle(year, month)).thenReturn(jobTotals);
        when(reportService.getTotalPayByDivision(year, month)).thenReturn(divTotals);
        
        cmbYear.setValue(year);
        cmbMonth.setValue(month);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = ReportsController.class.getDeclaredMethod("onRun");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onRun method: " + e.getMessage());
        }
        
        // Verify
        verify(reportService).getTotalPayByJobTitle(year, month);
        verify(reportService).getTotalPayByDivision(year, month);
        
        // Verify tables are populated
        assertNotNull(tblJob.getItems());
        assertNotNull(tblDiv.getItems());
        assertEquals(2, tblJob.getItems().size());
        assertEquals(2, tblDiv.getItems().size());
    }

    @Test
    void testOnRun_EmptyResults() throws SQLException {
        // Setup
        int year = 2024;
        int month = 1;
        
        Map<String, BigDecimal> emptyJobTotals = new HashMap<>();
        Map<String, BigDecimal> emptyDivTotals = new HashMap<>();
        
        when(reportService.getTotalPayByJobTitle(year, month)).thenReturn(emptyJobTotals);
        when(reportService.getTotalPayByDivision(year, month)).thenReturn(emptyDivTotals);
        
        cmbYear.setValue(year);
        cmbMonth.setValue(month);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = ReportsController.class.getDeclaredMethod("onRun");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onRun method: " + e.getMessage());
        }
        
        // Verify
        verify(reportService).getTotalPayByJobTitle(year, month);
        verify(reportService).getTotalPayByDivision(year, month);
        
        // Verify tables are empty
        assertTrue(tblJob.getItems().isEmpty());
        assertTrue(tblDiv.getItems().isEmpty());
    }

    @Test
    void testOnRun_ExceptionHandling() throws SQLException {
        // Setup
        int year = 2024;
        int month = 1;
        
        when(reportService.getTotalPayByJobTitle(year, month))
            .thenThrow(new SQLException("Database error"));
        
        cmbYear.setValue(year);
        cmbMonth.setValue(month);
        
        // Execute - should handle exception gracefully
        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = ReportsController.class.getDeclaredMethod("onRun");
                method.setAccessible(true);
                method.invoke(controller);
                WaitForAsyncUtils.waitForFxEvents();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        verify(reportService).getTotalPayByJobTitle(year, month);
    }

    @Test
    void testOnBack() {
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = ReportsController.class.getDeclaredMethod("onBack");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onBack method: " + e.getMessage());
        }
        
        // Verify - should navigate back to main menu
        // This is tested through NavigationManager
    }

    @Test
    void testOnRun_DifferentYearMonth() throws SQLException {
        // Setup
        int year = 2023;
        int month = 6;
        
        Map<String, BigDecimal> jobTotals = new HashMap<>();
        jobTotals.put("Software Engineer", new BigDecimal("45000.00"));
        
        Map<String, BigDecimal> divTotals = new HashMap<>();
        divTotals.put("Engineering", new BigDecimal("90000.00"));
        
        when(reportService.getTotalPayByJobTitle(year, month)).thenReturn(jobTotals);
        when(reportService.getTotalPayByDivision(year, month)).thenReturn(divTotals);
        
        cmbYear.setValue(year);
        cmbMonth.setValue(month);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = ReportsController.class.getDeclaredMethod("onRun");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onRun method: " + e.getMessage());
        }
        
        // Verify
        verify(reportService).getTotalPayByJobTitle(year, month);
        verify(reportService).getTotalPayByDivision(year, month);
    }
}

