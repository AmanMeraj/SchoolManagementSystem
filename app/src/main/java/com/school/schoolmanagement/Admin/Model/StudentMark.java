package com.school.schoolmanagement.Admin.Model;

public class StudentMark {
    private int studentId;
    private String studentName;
    private int obtainedMarks;
    private int totalMarks;

    // Default constructor
    public StudentMark() {
    }

    // Parameterized constructor
    public StudentMark(int studentId, String studentName, int obtainedMarks) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.obtainedMarks = obtainedMarks;
    }

    // Parameterized constructor with total marks
    public StudentMark(int studentId, String studentName, int obtainedMarks, int totalMarks) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.obtainedMarks = obtainedMarks;
        this.totalMarks = totalMarks;
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

    public int getTotalMarks() {
        return totalMarks;
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

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    @Override
    public String toString() {
        return "StudentMark{" +
                "studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", obtainedMarks=" + obtainedMarks +
                ", totalMarks=" + totalMarks +
                '}';
    }
}