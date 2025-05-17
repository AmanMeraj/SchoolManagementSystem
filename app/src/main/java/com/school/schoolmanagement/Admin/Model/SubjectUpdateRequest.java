package com.school.schoolmanagement.Admin.Model;

public class SubjectUpdateRequest {
    private int id;
    private String name;
    private int totalMarks;

    public SubjectUpdateRequest(int id, String name, int totalMarks) {
        this.id = id;
        this.name = name;
        this.totalMarks = totalMarks;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }
}
