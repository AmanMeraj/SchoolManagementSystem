package com.school.schoolmanagement.Admin.Model;

public class SubjectMark {
    private String subject;
    private int marks;

    public SubjectMark() {}

    public SubjectMark(String subject, int marks) {
        this.subject = subject;
        this.marks = marks;
    }

    // Getters and Setters
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "SubjectMark{" +
                "subject='" + subject + '\'' +
                ", marks=" + marks +
                '}';
    }
}
