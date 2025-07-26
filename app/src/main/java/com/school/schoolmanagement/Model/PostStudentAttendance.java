package com.school.schoolmanagement.Model;

import java.util.ArrayList;

public class PostStudentAttendance {
    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public ArrayList<StudentAttendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(ArrayList<StudentAttendance> attendances) {
        this.attendances = attendances;
    }

    public int classId;
    public String attendanceDate;
    public ArrayList<StudentAttendance> attendances;
}
