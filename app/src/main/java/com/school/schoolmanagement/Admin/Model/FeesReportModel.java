package com.school.schoolmanagement.Admin.Model;

public class FeesReportModel {
    private String id;
    private String name;
    private String dueDate;
    private String amount;
    private String status;

    public String getDepositedAmount() {
        return depositedAmount;
    }

    public void setDepositedAmount(String depositedAmount) {
        this.depositedAmount = depositedAmount;
    }

    public String getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(String dueAmount) {
        this.dueAmount = dueAmount;
    }

    private String depositedAmount;
    private String dueAmount;

    public FeesReportModel(String id, String name, String dueDate, String amount,String depositedAmount,String dueAmount, String status) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.amount = amount;
        this.depositedAmount=depositedAmount;
        this.dueAmount=dueAmount;
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
