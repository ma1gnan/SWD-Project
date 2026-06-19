package com.employeemgmt.model;

public class Division {
    private Integer divisionId;
    private String name;

    public Division() {
    }

    public Division(Integer divisionId, String name) {
        this.setDivisionId(divisionId);
        this.setName(name);
    }

    public Division(String name) {
        this(null, name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Division[ID: %d, Name: %s]",
            divisionId != null ? divisionId : 0,
            name != null ? name : ""
        );
    }
}

