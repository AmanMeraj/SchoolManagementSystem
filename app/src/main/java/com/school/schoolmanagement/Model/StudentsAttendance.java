package com.school.schoolmanagement.Model;

import com.school.schoolmanagement.Admin.Model.Pagination;

import java.util.ArrayList;

public class StudentsAttendance {
    public ArrayList<AttendanceEntries> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<AttendanceEntries> entries) {
        this.entries = entries;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public ArrayList<AttendanceEntries> entries;
    public Pagination pagination;
}
