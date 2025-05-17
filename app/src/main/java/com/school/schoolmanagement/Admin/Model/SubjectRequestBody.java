package com.school.schoolmanagement.Admin.Model;

public class SubjectRequestBody {
    private String name;
    private int totalMarks;

    public SubjectRequestBody(String name, int totalMarks) {
        this.name = name;
        this.totalMarks = totalMarks;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }
}