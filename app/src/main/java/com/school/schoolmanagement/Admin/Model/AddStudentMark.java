package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class AddStudentMark {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public int status;
    public String message;
    public ArrayList<Datum> data;


    public class Datum{
        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public ArrayList<SubjectMark> getSubjectMarks() {
            return subjectMarks;
        }

        public void setSubjectMarks(ArrayList<SubjectMark> subjectMarks) {
            this.subjectMarks = subjectMarks;
        }

        public int studentId;
        public String studentName;
        public ArrayList<SubjectMark> subjectMarks;
    }
}
