package com.school.schoolmanagement.Admin.Model;


public class AccountStatementModel {
    private String date;
    private String status;
    private String amount1;
    private String amount2;
    private String amount3;

    public AccountStatementModel(String date, String status, String amount1, String amount2, String amount3) {
        this.date = date;
        this.status = status;
        this.amount1 = amount1;
        this.amount2 = amount2;
        this.amount3 = amount3;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount1() {
        return amount1;
    }

    public void setAmount1(String amount1) {
        this.amount1 = amount1;
    }

    public String getAmount2() {
        return amount2;
    }

    public void setAmount2(String amount2) {
        this.amount2 = amount2;
    }

    public String getAmount3() {
        return amount3;
    }

    public void setAmount3(String amount3) {
        this.amount3 = amount3;
    }
}
