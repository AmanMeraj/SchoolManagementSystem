package com.school.schoolmanagement.Admin.Model;

public class FeesDefaulterModel {
    private String number;
    private String name;

    public FeesDefaulterModel(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}

