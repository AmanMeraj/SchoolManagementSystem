package com.school.schoolmanagement.Model;

public class GradingItem {
    private String grade;
    private String percentFrom;
    private String percentUpto;
    private String status;

    public GradingItem() {
        // Default constructor
    }

    public GradingItem(String grade, String percentFrom, String percentUpto, String status) {
        this.grade = grade;
        this.percentFrom = percentFrom;
        this.percentUpto = percentUpto;
        this.status = status;
    }

    // Getters
    public String getGrade() {
        return grade;
    }

    public String getPercentFrom() {
        return percentFrom;
    }

    public String getPercentUpto() {
        return percentUpto;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setPercentFrom(String percentFrom) {
        this.percentFrom = percentFrom;
    }

    public void setPercentUpto(String percentUpto) {
        this.percentUpto = percentUpto;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
