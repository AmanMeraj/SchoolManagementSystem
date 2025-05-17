package com.school.schoolmanagement.Teachers.Model;

public class AttendanceReport {
    private String date;
    private String day;
    private String id;
    private String studentName;
    private String className;
    private String status;

    public AttendanceReport(String date, String day, String id, String studentName, String className, String status) {
        this.date = date;
        this.day = day;
        this.id = id;
        this.studentName = studentName;
        this.className = className;
        this.status = status;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getClassName() {
        return className;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}