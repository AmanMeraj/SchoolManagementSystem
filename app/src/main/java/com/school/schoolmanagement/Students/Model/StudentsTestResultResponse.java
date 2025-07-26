package com.school.schoolmanagement.Students.Model;

import java.util.ArrayList;

public class StudentsTestResultResponse {
    private int status;
    private String message;
    private Data data;

    // Getters and Setters for main response
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

    // Inner Data class that matches the nested "data" object in JSON
    public static class Data {
        private String studentName;
        private String profileImage;
        private ArrayList<Datum> data;

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public ArrayList<Datum> getData() {
            return data;
        }

        public void setData(ArrayList<Datum> data) {
            this.data = data;
        }
    }

    // Test result item class
    public static class Datum {
        private String testDate;
        private String className;
        private String subjectName;
        private int totalMarks;
        private int obtainedMarks;

        public String getTestDate() {
            return testDate;
        }

        public void setTestDate(String testDate) {
            this.testDate = testDate;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
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
    }
}