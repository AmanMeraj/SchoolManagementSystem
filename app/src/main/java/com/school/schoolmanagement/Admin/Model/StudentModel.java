package com.school.schoolmanagement.Admin.Model;

public class StudentModel {
    private String name;
    private String number;

    public StudentModel() {
        // Default constructor required for Firebase or serialization
    }

    public StudentModel( String name, String number) {
        this.name = name;
        this.number = number;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

