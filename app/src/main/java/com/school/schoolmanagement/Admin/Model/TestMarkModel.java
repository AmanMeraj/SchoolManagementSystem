// TestMarkModel.java
package com.school.schoolmanagement.Admin.Model;

public class TestMarkModel {
    private int studentId;
    private String studentName;
    private int obtainedMarks;
    private int total; // Added this field since it's used in TestApiHelper

    // Default constructor
    public TestMarkModel() {
    }

    // Parameterized constructor
    public TestMarkModel(int studentId, String studentName, int obtainedMarks) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.obtainedMarks = obtainedMarks;
    }

    // Parameterized constructor with total
    public TestMarkModel(int studentId, String studentName, int obtainedMarks, int total) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.obtainedMarks = obtainedMarks;
        this.total = total;
    }

    // Getters
    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getObtainedMarks() {
        return obtainedMarks;
    }

    public int getTotal() {
        return total;
    }

    // Setters
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setObtainedMarks(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "TestMarkModel{" +
                "studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", obtainedMarks=" + obtainedMarks +
                ", total=" + total +
                '}';
    }
}

// StudentMark.java
