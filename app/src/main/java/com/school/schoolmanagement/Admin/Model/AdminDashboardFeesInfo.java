package com.school.schoolmanagement.Admin.Model;

public class AdminDashboardFeesInfo {
    public double getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(double totalReceived) {
        this.totalReceived = totalReceived;
    }

    public double getReceivedThisMonth() {
        return receivedThisMonth;
    }

    public void setReceivedThisMonth(double receivedThisMonth) {
        this.receivedThisMonth = receivedThisMonth;
    }

    public double totalReceived;
    public double receivedThisMonth;
}
