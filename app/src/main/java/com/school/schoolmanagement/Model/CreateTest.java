package com.school.schoolmanagement.Model;

import com.school.schoolmanagement.Admin.Model.StudentMark;

import java.util.ArrayList;

public class CreateTest {
    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public ArrayList<StudentMark> getStudentMarks() {
        return studentMarks;
    }

    public void setStudentMarks(ArrayList<StudentMark> studentMarks) {
        this.studentMarks = studentMarks;
    }

    public int classId;
    public int subjectId;
    public String testDate;
    public int totalMarks;
    public ArrayList<StudentMark> studentMarks;
}
