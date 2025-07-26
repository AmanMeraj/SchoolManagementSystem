package com.school.schoolmanagement.Model;

public class AttendanceEntries {
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public AttendanceSummary getAttendanceSummary() {
        return attendanceSummary;
    }

    public void setAttendanceSummary(AttendanceSummary attendanceSummary) {
        this.attendanceSummary = attendanceSummary;
    }

    public String getDailyAttendanceCompressed() {
        return dailyAttendanceCompressed;
    }

    public void setDailyAttendanceCompressed(String dailyAttendanceCompressed) {
        this.dailyAttendanceCompressed = dailyAttendanceCompressed;
    }

    public Object getLastAttendanceDate() {
        return lastAttendanceDate;
    }

    public void setLastAttendanceDate(Object lastAttendanceDate) {
        this.lastAttendanceDate = lastAttendanceDate;
    }

    public String getLastAttendanceStatus() {
        return lastAttendanceStatus;
    }

    public void setLastAttendanceStatus(String lastAttendanceStatus) {
        this.lastAttendanceStatus = lastAttendanceStatus;
    }

    public int studentId;
    public String studentName;
    public String rollNumber;
    public AttendanceSummary attendanceSummary;
    public String dailyAttendanceCompressed;
    public Object lastAttendanceDate;
    public String lastAttendanceStatus;
}
