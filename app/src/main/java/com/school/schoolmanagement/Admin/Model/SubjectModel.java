package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;

public class SubjectModel implements Serializable {
    private String name;
    private int marks;

    public SubjectModel(String name, int marks) {
        this.name = name;
        this.marks = marks;
    }

    public String getName() {
        return name;
    }

    public int getMarks() {
        return marks;
    }
}

