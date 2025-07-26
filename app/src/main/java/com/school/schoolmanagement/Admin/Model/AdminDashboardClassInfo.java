package com.school.schoolmanagement.Admin.Model;

public class AdminDashboardClassInfo {
    public int getTotalClassCount() {
        return totalClassCount;
    }

    public void setTotalClassCount(int totalClassCount) {
        this.totalClassCount = totalClassCount;
    }

    public int getAddedThisMonth() {
        return addedThisMonth;
    }

    public void setAddedThisMonth(int addedThisMonth) {
        this.addedThisMonth = addedThisMonth;
    }

    public int totalClassCount;
    public int addedThisMonth;
}
