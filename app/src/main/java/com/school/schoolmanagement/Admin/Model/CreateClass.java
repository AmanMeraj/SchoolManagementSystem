package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;

public class CreateClass implements Serializable {

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getMonthlyTuitionFees() {
        return monthlyTuitionFees;
    }

    public void setMonthlyTuitionFees(int monthlyTuitionFees) {
        this.monthlyTuitionFees = monthlyTuitionFees;
    }

    public int getClassTeacherId() {
        return classTeacherId;
    }

    public void setClassTeacherId(int classTeacherId) {
        this.classTeacherId = classTeacherId;
    }

    public String className;
    public int monthlyTuitionFees;
    public int classTeacherId;
}
