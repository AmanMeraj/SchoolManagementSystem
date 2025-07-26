package com.school.schoolmanagement.Model;

public class AttendanceSummary {
    public int getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(int presentDays) {
        this.presentDays = presentDays;
    }

    public int getLateDays() {
        return lateDays;
    }

    public void setLateDays(int lateDays) {
        this.lateDays = lateDays;
    }

    public int getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(int absentDays) {
        this.absentDays = absentDays;
    }

    public int getHolidayDays() {
        return holidayDays;
    }

    public void setHolidayDays(int holidayDays) {
        this.holidayDays = holidayDays;
    }

    public int getHalfDays() {
        return halfDays;
    }

    public void setHalfDays(int halfDays) {
        this.halfDays = halfDays;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public int presentDays;
    public int lateDays;
    public int absentDays;
    public int holidayDays;
    public int halfDays;
    public double attendancePercentage;
}
