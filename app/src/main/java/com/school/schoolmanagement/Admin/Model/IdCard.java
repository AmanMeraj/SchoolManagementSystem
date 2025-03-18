package com.school.schoolmanagement.Admin.Model;

public class IdCard {
    private String name;
    private String id;
    private String classs;

    public IdCard(String name, String id, String classs) {
        this.name = name;
        this.id = id;
        this.classs = classs;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getClasss() {
        return classs;
    }
}
