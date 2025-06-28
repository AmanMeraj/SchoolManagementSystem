package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;
import java.util.List;

public class StudentMarksData {
    private int studentId;
    private List<SubjectMark> subjectMarks;

    public StudentMarksData() {
        this.subjectMarks = new ArrayList<>();
    }

    public StudentMarksData(int studentId, List<SubjectMark> subjectMarks) {
        this.studentId = studentId;
        this.subjectMarks = subjectMarks != null ? subjectMarks : new ArrayList<>();
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public List<SubjectMark> getSubjectMarks() {
        return subjectMarks;
    }

    public void setSubjectMarks(List<SubjectMark> subjectMarks) {
        this.subjectMarks = subjectMarks != null ? subjectMarks : new ArrayList<>();
    }

    // Helper method to add subject mark
    public void addSubjectMark(SubjectMark subjectMark) {
        if (this.subjectMarks == null) {
            this.subjectMarks = new ArrayList<>();
        }
        this.subjectMarks.add(subjectMark);
    }

    // Helper method to add subject mark with parameters
    public void addSubjectMark(String subject, int marks) {
        addSubjectMark(new SubjectMark(subject, marks));
    }

    @Override
    public String toString() {
        return "StudentMarksData{" +
                "studentId=" + studentId +
                ", subjectMarks=" + subjectMarks +
                '}';
    }
}
