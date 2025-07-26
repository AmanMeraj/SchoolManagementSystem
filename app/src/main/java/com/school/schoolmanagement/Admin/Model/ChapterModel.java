package com.school.schoolmanagement.Admin.Model;

public class ChapterModel {
    private int id;
    private String title;
    private String subjectName;
    private String className;

    public ChapterModel(int id, String title, String subjectName, String className) {
        this.id = id;
        this.title = title;
        this.subjectName = subjectName;
        this.className = className;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
