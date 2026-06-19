package com.employeemgmt.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payroll {
    private Integer payrollId;
    private Integer employeeId;
    private BigDecimal amount;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;

    public Payroll() {
    }

    public Payroll(Integer payrollId, Integer employeeId, BigDecimal amount, 
                   LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        this.setPayrollId(payrollId);
        this.setEmployeeId(employeeId);
        this.setAmount(amount);
        this.setPayPeriodStart(payPeriodStart);
        this.setPayPeriodEnd(payPeriodEnd);
    }

    public Payroll(Integer employeeId, BigDecimal amount, 
                   LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        this(null, employeeId, amount, payPeriodStart, payPeriodEnd);
    }

    public Integer getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(Integer payrollId) {
        if (payrollId != null && payrollId < 0) {
            throw new IllegalArgumentException("Payroll ID must be non-negative");
        }
        this.payrollId = payrollId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        this.amount = amount;
    }

    public LocalDate getPayPeriodStart() {
        return payPeriodStart;
    }

    public void setPayPeriodStart(LocalDate payPeriodStart) {
        this.payPeriodStart = payPeriodStart;
    }

    public LocalDate getPayPeriodEnd() {
        return payPeriodEnd;
    }

    public void setPayPeriodEnd(LocalDate payPeriodEnd) {
        this.payPeriodEnd = payPeriodEnd;
    }

    @Override
    public String toString() {
        return String.format("Payroll[ID: %d, Employee ID: %d, Amount: $%.2f, Period: %s to %s]",
            payrollId != null ? payrollId : 0,
            employeeId != null ? employeeId : 0,
            amount != null ? amount : BigDecimal.ZERO,
            payPeriodStart != null ? payPeriodStart : "",
            payPeriodEnd != null ? payPeriodEnd : ""
        );
    }
}

