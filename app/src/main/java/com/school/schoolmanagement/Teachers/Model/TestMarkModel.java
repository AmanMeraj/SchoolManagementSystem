package com.school.schoolmanagement.Teachers.Model;

public class TestMarkModel {
    private String date;
    private String studentId;
    private String studentName;
    private String studentClass;
    private String subject;
    private int totalMarks;
    private int obtainedMarks;

    // Constructor
    public TestMarkModel(String date, String studentId, String studentName,
                         String studentClass, String subject,
                         int totalMarks, int obtainedMarks) {
        this.date = date;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.subject = subject;
        this.totalMarks = totalMarks;
        this.obtainedMarks = obtainedMarks;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }
}