package com.school.schoolmanagement.Admin.Model;

public class AdminDashboardSubjectInfo {
    public int getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(int totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public int getAddedThisMonth() {
        return addedThisMonth;
    }

    public void setAddedThisMonth(int addedThisMonth) {
        this.addedThisMonth = addedThisMonth;
    }

    public int totalSubjects;
    public int addedThisMonth;
}
