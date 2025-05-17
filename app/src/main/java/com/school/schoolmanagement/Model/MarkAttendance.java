package com.school.schoolmanagement.Model;

public class MarkAttendance {
    private String studentId;
    private String studentName;
    private int attendanceStatus; // 0 = No status, 1 = Present, 2 = Leave, 3 = Absent

    // Constants for attendance status
    public static final int STATUS_NONE = 0;
    public static final int STATUS_PRESENT = 1;
    public static final int STATUS_LEAVE = 2;
    public static final int STATUS_ABSENT = 3;

    // Default constructor required for Firebase/database operations
    public MarkAttendance() {
    }

    public MarkAttendance(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.attendanceStatus = STATUS_NONE;
    }

    public MarkAttendance(String studentId, String studentName, int attendanceStatus) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.attendanceStatus = attendanceStatus;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(int attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    // Helper methods to check status
    public boolean isPresent() {
        return attendanceStatus == STATUS_PRESENT;
    }

    public boolean isOnLeave() {
        return attendanceStatus == STATUS_LEAVE;
    }

    public boolean isAbsent() {
        return attendanceStatus == STATUS_ABSENT;
    }

    public boolean hasNoStatus() {
        return attendanceStatus == STATUS_NONE;
    }
}