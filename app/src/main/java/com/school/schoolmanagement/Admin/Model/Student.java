package com.school.schoolmanagement.Admin.Model;

public class Student {
    private String name;
    private String subject;
    private int imageResId;

    public Student(String name, String subject, int imageResId) {
        this.name = name;
        this.subject = subject;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public int getImageResId() {
        return imageResId;
    }
}

