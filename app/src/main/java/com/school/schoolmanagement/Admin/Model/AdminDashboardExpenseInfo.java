package com.school.schoolmanagement.Admin.Model;

public class AdminDashboardExpenseInfo {
    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double getExpensesThisMonth() {
        return expensesThisMonth;
    }

    public void setExpensesThisMonth(double expensesThisMonth) {
        this.expensesThisMonth = expensesThisMonth;
    }

    public double totalExpenses;
    public double expensesThisMonth;
}
