package com.school.schoolmanagement.Model;

import java.util.ArrayList;

public class PostEmployeeAttendance {
    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public ArrayList<EmployeeAttendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(ArrayList<EmployeeAttendance> attendances) {
        this.attendances = attendances;
    }

    public String attendanceDate;
    public ArrayList<EmployeeAttendance> attendances;
}
