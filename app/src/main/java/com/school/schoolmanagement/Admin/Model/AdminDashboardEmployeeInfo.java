package com.school.schoolmanagement.Admin.Model;

public class AdminDashboardEmployeeInfo {
    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getAddedThisMonth() {
        return addedThisMonth;
    }

    public void setAddedThisMonth(int addedThisMonth) {
        this.addedThisMonth = addedThisMonth;
    }

    public int totalEmployees;
    public int addedThisMonth;
}
