package com.school.schoolmanagement.Students.Model;

import java.util.ArrayList;

public class StudentDashboardApiResponse2 {
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

    public class ClassTestReport{
        public int getTestId() {
            return testId;
        }

        public void setTestId(int testId) {
            this.testId = testId;
        }

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public int getTotalMarks() {
            return totalMarks;
        }

        public void setTotalMarks(int totalMarks) {
            this.totalMarks = totalMarks;
        }

        public int getObtainedMarks() {
            return obtainedMarks;
        }

        public void setObtainedMarks(int obtainedMarks) {
            this.obtainedMarks = obtainedMarks;
        }

        public double getClassAverageMarks() {
            return classAverageMarks;
        }

        public void setClassAverageMarks(double classAverageMarks) {
            this.classAverageMarks = classAverageMarks;
        }

        public boolean isAppeared() {
            return appeared;
        }

        public void setAppeared(boolean appeared) {
            this.appeared = appeared;
        }

        public String getTestDate() {
            return testDate;
        }

        public void setTestDate(String testDate) {
            this.testDate = testDate;
        }

        public int testId;
        public String testName;
        public String subjectName;
        public int totalMarks;
        public int obtainedMarks;
        public double classAverageMarks;
        public boolean appeared;
        public String testDate;
    }

    public class Data{
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

        public ArrayList<ClassTestReport> getClassTestReport() {
            return classTestReport;
        }

        public void setClassTestReport(ArrayList<ClassTestReport> classTestReport) {
            this.classTestReport = classTestReport;
        }

        public int studentId;
        public String studentName;
        public ArrayList<ClassTestReport> classTestReport;
    }

}
