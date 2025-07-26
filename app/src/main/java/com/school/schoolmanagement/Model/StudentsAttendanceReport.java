package com.school.schoolmanagement.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StudentsAttendanceReport {
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
        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getMyclass() {
            return myclass;
        }

        public void setMyclass(String myclass) {
            this.myclass = myclass;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String studentName;
        public String day;
        @SerializedName("class")
        public String myclass;
        public String studentId;
        public String date;
        public String status;
    }
}
