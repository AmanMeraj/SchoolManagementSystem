package com.school.schoolmanagement.Admin.Model;

public class Subject {
    private String subjectName;
    private String subjectId;

    public Subject(String subjectId, String subjectName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectId() {
        return subjectId;
    }
}
