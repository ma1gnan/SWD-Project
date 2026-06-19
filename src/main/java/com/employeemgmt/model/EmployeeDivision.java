package com.employeemgmt.model;

public class EmployeeDivision {
    private Integer employeeId;
    private Integer divisionId;

    public EmployeeDivision() {
    }

    public EmployeeDivision(Integer employeeId, Integer divisionId) {
        this.setEmployeeId(employeeId);
        this.setDivisionId(divisionId);
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        if (employeeId != null && employeeId < 0) {
            throw new IllegalArgumentException("Employee ID must be non-negative");
        }
        this.employeeId = employeeId;
    }

    public Integer getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Integer divisionId) {
        if (divisionId != null && divisionId < 0) {
            throw new IllegalArgumentException("Division ID must be non-negative");
        }
        this.divisionId = divisionId;
    }

    @Override
    public String toString() {
        return String.format("EmployeeDivision[Employee ID: %d, Division ID: %d]",
            employeeId != null ? employeeId : 0,
            divisionId != null ? divisionId : 0
        );
    }
}

