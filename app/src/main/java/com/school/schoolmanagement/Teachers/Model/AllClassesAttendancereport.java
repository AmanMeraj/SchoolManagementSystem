package com.school.schoolmanagement.Teachers.Model;

public class AllClassesAttendancereport {
    private String date;
    private String yearName;
    private int presentCount;
    private int presentPercentage;
    private int onLeaveCount;
    private int onLeavePercentage;
    private int absentCount;
    private int absentPercentage;

    public AllClassesAttendancereport(String date, String yearName, int presentCount, int presentPercentage,
                                      int onLeaveCount, int onLeavePercentage, int absentCount, int absentPercentage) {
        this.date = date;
        this.yearName = yearName;
        this.presentCount = presentCount;
        this.presentPercentage = presentPercentage;
        this.onLeaveCount = onLeaveCount;
        this.onLeavePercentage = onLeavePercentage;
        this.absentCount = absentCount;
        this.absentPercentage = absentPercentage;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getYearName() {
        return yearName;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public int getPresentPercentage() {
        return presentPercentage;
    }

    public int getOnLeaveCount() {
        return onLeaveCount;
    }

    public int getOnLeavePercentage() {
        return onLeavePercentage;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public int getAbsentPercentage() {
        return absentPercentage;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public void setPresentPercentage(int presentPercentage) {
        this.presentPercentage = presentPercentage;
    }

    public void setOnLeaveCount(int onLeaveCount) {
        this.onLeaveCount = onLeaveCount;
    }

    public void setOnLeavePercentage(int onLeavePercentage) {
        this.onLeavePercentage = onLeavePercentage;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public void setAbsentPercentage(int absentPercentage) {
        this.absentPercentage = absentPercentage;
    }
}
