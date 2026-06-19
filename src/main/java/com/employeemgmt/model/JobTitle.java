package com.employeemgmt.model;

public class JobTitle {
    private Integer jobTitleId;
    private String title;

    public JobTitle() {
    }

    public JobTitle(Integer jobTitleId, String title) {
        this.setJobTitleId(jobTitleId);
        this.setTitle(title);
    }

    public JobTitle(String title) {
        this(null, title);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("JobTitle[ID: %d, Title: %s]",
            jobTitleId != null ? jobTitleId : 0,
            title != null ? title : ""
        );
    }
}

