package com.school.schoolmanagement.Students.Model;

public class StudentTestModel {
    private String date;
    private String className;
    private String subject;
    private String totalMark;
    private String obtainedMark;

    public StudentTestModel(String date, String className, String subject, String totalMark, String obtainedMark) {
        this.date = date;
        this.className = className;
        this.subject = subject;
        this.totalMark = totalMark;
        this.obtainedMark = obtainedMark;
    }

    public String getDate() {
        return date;
    }

    public String getClassName() {
        return className;
    }

    public String getSubject() {
        return subject;
    }

    public String getTotalMark() {
        return totalMark;
    }

    public String getObtainedMark() {
        return obtainedMark;
    }
}
