package com.emp_mgmt;

import com.employeemgmt.model.Employee;
import com.employeemgmt.model.Payroll;
import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;
import com.employeemgmt.ui.fx.controller.EmployeeDetailController;
import com.employeemgmt.ui.fx.controller.ServiceRegistry;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class EmployeeDetailControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ReportService reportService;

    private Stage stage;
    private EmployeeDetailController controller;
    private Label lblId;
    private Label lblEmail;
    private TableView<Payroll> tblPayroll;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        
        // Initialize services
        ServiceRegistry.init(employeeService, reportService);
        
        // Load FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/employeemgmt/ui/fx/employee_detail.fxml")
        );
        Parent root = loader.load();
        controller = loader.getController();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        // Get references to UI components that exist in FXML
        lblId = (Label) root.lookup("#lblId");
        lblEmail = (Label) root.lookup("#lblEmail");
        // tblPayroll may be added programmatically or may not be in FXML
        try {
            tblPayroll = (TableView<Payroll>) root.lookup("#tblPayroll");
        } catch (Exception e) {
            // Table may not be in FXML, controller handles it
            tblPayroll = null;
        }
    }

    @BeforeEach
    void setUp() {
        reset(employeeService, reportService);
    }

    @Test
    void testInitialize() {
        // Verify that the controller initializes properly
        assertNotNull(controller);
        assertNotNull(tblPayroll);
    }

    @Test
    void testSetEmployee() throws SQLException {
        // Setup
        Employee employee = new Employee(1, "John", "Doe", "123456789", "john@example.com");

        
        // Execute
        controller.setEmployee(employee);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify labels are set (checking what's available in FXML)
        assertNotNull(lblId);
        assertEquals("1", lblId.getText());
        assertNotNull(lblEmail);
        assertEquals("john@example.com", lblEmail.getText());
    }

    @Test
    void testSetEmployee_NullEmployeeId() {
        // Setup
        Employee employee = new Employee(null, "John", "Doe", "123456789", "john@example.com");
        
        // Execute
        controller.setEmployee(employee);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify
        assertNotNull(lblId);
        assertEquals("", lblId.getText());
        assertNotNull(lblEmail);
        assertEquals("john@example.com", lblEmail.getText());
    }

    @Test
    void testOnEdit() {
        // Setup
        Employee employee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        controller.setEmployee(employee);
        
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeDetailController.class.getDeclaredMethod("onEdit");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onEdit method: " + e.getMessage());
        }
        
        // Verify - should navigate to edit form
    }

    @Test
    void testOnBack() {
        // Execute - use reflection to call private method for testing
        try {
            java.lang.reflect.Method method = EmployeeDetailController.class.getDeclaredMethod("onBack");
            method.setAccessible(true);
            method.invoke(controller);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception e) {
            fail("Failed to invoke onBack method: " + e.getMessage());
        }
        
        // Verify - should navigate back to search
    }

    @Test
    void testLoadPayHistory_WithPayrolls() throws SQLException {
        // Setup
        Employee employee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        
        controller.setEmployee(employee);
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> controller.setEmployee(employee));
    }

    @Test
    void testLoadPayHistory_NoPayrolls() {
        // Setup
        Employee employee = new Employee(1, "John", "Doe", "123456789", "john@example.com");
        
        // Execute
        assertDoesNotThrow(() -> {
            controller.setEmployee(employee);
            WaitForAsyncUtils.waitForFxEvents();
        });
        
        // Verify - method completes successfully
    }

    @Test
    void testLoadPayHistory_NullEmployeeId() {
        // Setup
        Employee employee = new Employee(null, "John", "Doe", "123456789", "john@example.com");
        
        // Execute
        assertDoesNotThrow(() -> {
            controller.setEmployee(employee);
            WaitForAsyncUtils.waitForFxEvents();
        });
        
        // Verify - should handle null employee ID 
    }
}

