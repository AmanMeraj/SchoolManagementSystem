package com.school.schoolmanagement.Admin.Model;


public class SalaryReportModel {
    private String id;
    private String name;
    private String dueDate;
    private String amount;
    private String status;

    public SalaryReportModel(String id, String name, String dueDate, String amount, String status) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}

