package com.school.schoolmanagement.Admin.Model;

public class ClassData {
    private int classId;
    private String className;
    private String classTeacherName;
    private int classTeacherId;
    private double monthlyTuitionFees;
    private int totalStudents;
    private int boysCount;
    private int girlsCount;
    private int naCount;

    public ClassData() {
    }

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
}