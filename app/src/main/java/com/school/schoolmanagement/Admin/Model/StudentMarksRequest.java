package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;
import java.util.List;

public class StudentMarksRequest {
    private int examId;
    private int classId;
    private List<StudentMarksData> studentMarks;

    public StudentMarksRequest() {
        this.studentMarks = new ArrayList<>();
    }

    public StudentMarksRequest(int examId, int classId, List<StudentMarksData> studentMarks) {
        this.examId = examId;
        this.classId = classId;
        this.studentMarks = studentMarks != null ? studentMarks : new ArrayList<>();
    }

    // Getters and Setters
    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public List<StudentMarksData> getStudentMarks() {
        return studentMarks;
    }

    public void setStudentMarks(List<StudentMarksData> studentMarks) {
        this.studentMarks = studentMarks != null ? studentMarks : new ArrayList<>();
    }

    // Helper method to add student marks
    public void addStudentMarks(StudentMarksData studentMarks) {
        if (this.studentMarks == null) {
            this.studentMarks = new ArrayList<>();
        }
        this.studentMarks.add(studentMarks);
    }

    @Override
    public String toString() {
        return "StudentMarksRequest{" +
                "examId=" + examId +
                ", classId=" + classId +
                ", studentMarks=" + studentMarks +
                '}';
    }
}
