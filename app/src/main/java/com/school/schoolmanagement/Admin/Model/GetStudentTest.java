package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class GetStudentTest {

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int status;
    public String message;
    public Data data;

    public class Data{
        public int getTestId() {
            return testId;
        }

        public void setTestId(int testId) {
            this.testId = testId;
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

        public int testId;
        public int totalMarks;
        public ArrayList<StudentMark> studentMarks;
    }
}
