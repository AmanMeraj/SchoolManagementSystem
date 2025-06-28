package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassModel implements Serializable {
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

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public int status;
    public String message;
    public ArrayList<Data> data;

    public static class Data implements Serializable{
        public int getClassId() {
            return classId;
        }

        public void setClassId(int classId) {
            this.classId = classId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public int getTotalStudents() {
            return totalStudents;
        }

        public void setTotalStudents(int totalStudents) {
            this.totalStudents = totalStudents;
        }

        public int getBoysCount() {
            return boysCount;
        }

        public void setBoysCount(int boysCount) {
            this.boysCount = boysCount;
        }

        public int getGirlsCount() {
            return girlsCount;
        }

        public void setGirlsCount(int girlsCount) {
            this.girlsCount = girlsCount;
        }

        public int getNaCount() {
            return naCount;
        }

        public void setNaCount(int naCount) {
            this.naCount = naCount;
        }

        public int classId;
        public String className;
        public int totalStudents;
        public int boysCount;
        public int girlsCount;
        public int naCount;
    }
}
