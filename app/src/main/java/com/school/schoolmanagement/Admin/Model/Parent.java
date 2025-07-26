package com.school.schoolmanagement.Admin.Model;

public class Parent {
    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherMobileNumber() {
        return fatherMobileNumber;
    }

    public void setFatherMobileNumber(String fatherMobileNumber) {
        this.fatherMobileNumber = fatherMobileNumber;
    }

    public String getFatherProfession() {
        return fatherProfession;
    }

    public void setFatherProfession(String fatherProfession) {
        this.fatherProfession = fatherProfession;
    }

    public double getFatherIncome() {
        return fatherIncome;
    }

    public void setFatherIncome(double fatherIncome) {
        this.fatherIncome = fatherIncome;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherMobileNumber() {
        return motherMobileNumber;
    }

    public void setMotherMobileNumber(String motherMobileNumber) {
        this.motherMobileNumber = motherMobileNumber;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public double getMotherIncome() {
        return motherIncome;
    }

    public void setMotherIncome(double motherIncome) {
        this.motherIncome = motherIncome;
    }

    public Object getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(Object parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String fatherName;
    public String fatherMobileNumber;
    public String fatherProfession;
    public double fatherIncome;
    public String motherName;
    public String motherMobileNumber;
    public String motherOccupation;
    public double motherIncome;
    public Object parentEmail;
}
