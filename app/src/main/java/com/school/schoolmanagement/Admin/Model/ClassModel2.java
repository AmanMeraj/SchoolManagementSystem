package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class ClassModel2 {
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

        public String getClassTeacherName() {
            return classTeacherName;
        }

        public void setClassTeacherName(String classTeacherName) {
            this.classTeacherName = classTeacherName;
        }

        public int getClassTeacherId() {
            return classTeacherId;
        }

        public void setClassTeacherId(int classTeacherId) {
            this.classTeacherId = classTeacherId;
        }

        public double getMonthlyTuitionFees() {
            return monthlyTuitionFees;
        }

        public void setMonthlyTuitionFees(double monthlyTuitionFees) {
            this.monthlyTuitionFees = monthlyTuitionFees;
        }

        public ArrayList<FeesStructures> getFeesStructure() {
            return feesStructure;
        }

        public void setFeesStructure(ArrayList<FeesStructures> feesStructure) {
            this.feesStructure = feesStructure;
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
        public String classTeacherName;
        public int classTeacherId;
        public double monthlyTuitionFees;
        public ArrayList<FeesStructures> feesStructure;
        public int totalStudents;
        public int boysCount;
        public int girlsCount;
        public int naCount;
    }

    public class FeesStructures{
        public String getFeeType() {
            return feeType;
        }

        public void setFeeType(String feeType) {
            this.feeType = feeType;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String feeType;
        public double amount;
    }
}
