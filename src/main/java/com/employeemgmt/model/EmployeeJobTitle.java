package com.employeemgmt.model;

public class EmployeeJobTitle {
    private Integer employeeId;
    private Integer jobTitleId;

    public EmployeeJobTitle() {
    }

    public EmployeeJobTitle(Integer employeeId, Integer jobTitleId) {
        this.setEmployeeId(employeeId);
        this.setJobTitleId(jobTitleId);
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

    public Integer getJobTitleId() {
        return jobTitleId;
    }

    public void setJobTitleId(Integer jobTitleId) {
        if (jobTitleId != null && jobTitleId < 0) {
            throw new IllegalArgumentException("Job Title ID must be non-negative");
        }
        this.jobTitleId = jobTitleId;
    }

    @Override
    public String toString() {
        return String.format("EmployeeJobTitle[Employee ID: %d, Job Title ID: %d]",
            employeeId != null ? employeeId : 0,
            jobTitleId != null ? jobTitleId : 0
        );
    }
}

