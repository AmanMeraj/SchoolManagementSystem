package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;

public class ClassModel implements Serializable {
    private String className;
    private int totalStudents;
    private int boysCount, girlsCount, naCount;


    public ClassModel(String className, int totalStudents, int boysCount, int girlsCount, int naCount) {
        this.className = className;
        this.totalStudents = totalStudents;
        this.boysCount = boysCount;
        this.girlsCount = girlsCount;
        this.naCount = naCount;
    }

    public String getClassName() { return className; }
    public int getTotalStudents() { return totalStudents; }
    public int getBoysCount() { return boysCount; }
    public int getGirlsCount() { return girlsCount; }
    public int getNaCount() { return naCount; }

    public float getBoysPercentage() { return (boysCount * 100f) / totalStudents; }
    public float getGirlsPercentage() { return (girlsCount * 100f) / totalStudents; }
    public float getNaPercentage() { return (naCount * 100f) / totalStudents; }
}
