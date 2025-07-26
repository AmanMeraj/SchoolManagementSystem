package com.school.schoolmanagement.Model;

class AttendanceSummaryModel {
    private int presentDays;
    private int lateDays;
    private int absentDays;
    private int holidayDays;
    private int halfDays;
    private double attendancePercentage;

    public AttendanceSummaryModel(int presentDays, int lateDays, int absentDays,
                                  int holidayDays, int halfDays, double attendancePercentage) {
        this.presentDays = presentDays;
        this.lateDays = lateDays;
        this.absentDays = absentDays;
        this.holidayDays = holidayDays;
        this.halfDays = halfDays;
        this.attendancePercentage = attendancePercentage;
    }

    // Getters
    public int getPresentDays() { return presentDays; }
    public int getLateDays() { return lateDays; }
    public int getAbsentDays() { return absentDays; }
    public int getHolidayDays() { return holidayDays; }
    public int getHalfDays() { return halfDays; }
    public double getAttendancePercentage() { return attendancePercentage; }
}