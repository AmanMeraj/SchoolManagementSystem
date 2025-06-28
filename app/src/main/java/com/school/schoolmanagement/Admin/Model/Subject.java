package com.school.schoolmanagement.Admin.Model;

public class Subject {
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    private String subjectName;
    private String subjectId;

    public Subject(String subjectId, String subjectName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }
    public Subject(){
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectId() {
        return subjectId;
    }
}
