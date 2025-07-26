package com.school.schoolmanagement.Admin.Model;

public class AdminDashboardStudentInfo {
    public int getTotalStudentCount() {
        return totalStudentCount;
    }

    public void setTotalStudentCount(int totalStudentCount) {
        this.totalStudentCount = totalStudentCount;
    }

    public int getAddedThisMonth() {
        return addedThisMonth;
    }

    public void setAddedThisMonth(int addedThisMonth) {
        this.addedThisMonth = addedThisMonth;
    }

    public int totalStudentCount;
    public int addedThisMonth;
}
